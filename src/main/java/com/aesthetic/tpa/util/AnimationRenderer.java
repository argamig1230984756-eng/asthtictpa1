/*
 * Recovered from AestheticTPA 1.0.0 and extended:
 *  - Dragon now flies in a small circle around the player
 *  - Every warmup animation is configurable in config.yml (animations.*)
 */
package com.aesthetic.tpa.util;

import com.aesthetic.tpa.AestheticTPA;
import com.aesthetic.tpa.model.TeleportAnimation;
import com.aesthetic.tpa.model.WarmupTask;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class AnimationRenderer {
    private static boolean scaleWarned = false;

    // ------------------------------------------------------------------
    // Config helpers  (config.yml -> animations.<name>.<key>)
    // Defaults are also embedded in the jar's config.yml, so old config
    // files without these keys keep working.
    // ------------------------------------------------------------------
    private static double cfg(String path, double def) {
        return AestheticTPA.getInstance().getConfig().getDouble("animations." + path, def);
    }

    private static boolean cfgBool(String path, boolean def) {
        return AestheticTPA.getInstance().getConfig().getBoolean("animations." + path, def);
    }

    /** Player location shifted by animations.<name>.offset.x/y/z */
    private static Location base(Player player, String name) {
        return player.getLocation().add(cfg(name + ".offset.x", 0.0), cfg(name + ".offset.y", 0.0), cfg(name + ".offset.z", 0.0));
    }

    /** Changes the model size of an entity (needs Minecraft 1.20.5+). */
    private static void applyScale(LivingEntity entity, double scale) {
        if (scale <= 0.0 || scale == 1.0) {
            return;
        }
        try {
            Attribute attribute = null;
            for (String fieldName : new String[]{"GENERIC_SCALE", "SCALE"}) {
                try {
                    attribute = (Attribute)Attribute.class.getField(fieldName).get(null);
                    break;
                }
                catch (ReflectiveOperationException reflectiveOperationException) {
                    // try next name
                }
            }
            if (attribute == null) {
                if (!scaleWarned) {
                    scaleWarned = true;
                    AestheticTPA.getInstance().getLogger().warning("Animation 'size' needs Minecraft 1.20.5 or newer - ignoring size setting.");
                }
                return;
            }
            AttributeInstance instance = entity.getAttribute(attribute);
            if (instance != null) {
                instance.setBaseValue(Math.max(0.0625, Math.min(16.0, scale)));
            }
        }
        catch (Throwable throwable) {
            if (!scaleWarned) {
                scaleWarned = true;
                AestheticTPA.getInstance().getLogger().warning("Could not apply animation size: " + throwable.getMessage());
            }
        }
    }

    /** Where the dragon should be at animation step n (circles the player). */
    private static Location dragonLocation(Player player, int n) {
        Location center = base(player, "dragon");
        double radius = cfg("dragon.radius", 2.5);
        double height = cfg("dragon.height", 1.5);
        double wave = cfg("dragon.height-wave", 0.4);
        double speed = cfg("dragon.speed", 0.12);
        boolean clockwise = cfgBool("dragon.clockwise", false);
        double yawOffset = cfg("dragon.yaw-offset", 180.0);
        double angle = (double)n * speed * (clockwise ? -1.0 : 1.0);
        double y = height + Math.sin((double)n * 0.08) * wave;
        Location loc = center.clone().add(Math.cos(angle) * radius, y, Math.sin(angle) * radius);
        // yaw = direction of travel (+180 when flying clockwise), then the model offset
        loc.setYaw((float)(Math.toDegrees(angle) + (clockwise ? 180.0 : 0.0) + yawOffset));
        loc.setPitch((float)(Math.cos((double)n * 0.08) * -6.0));
        return loc;
    }

    public static void initWarmup(AestheticTPA aestheticTPA, Player player, TeleportAnimation teleportAnimation, WarmupTask warmupTask) {
        Location location = player.getLocation();
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        switch (teleportAnimation) {
            case DRAGON: {
                if (cfgBool("dragon.sounds", true)) {
                    world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.7f, 1.4f);
                    world.playSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.9f, 1.0f);
                }
                try {
                    Location location2 = dragonLocation(player, 0);
                    double dragonSize = cfg("dragon.size", 0.35);
                    EnderDragon enderDragon2 = (EnderDragon)world.spawn(location2, EnderDragon.class, enderDragon -> {
                        enderDragon.setMetadata("aesthetictpa_anim", (MetadataValue)new FixedMetadataValue((Plugin)aestheticTPA, (Object)true));
                        enderDragon.setPhase(EnderDragon.Phase.HOVER);
                        enderDragon.setAI(true);
                        enderDragon.setInvulnerable(true);
                        enderDragon.setSilent(true);
                        enderDragon.setCollidable(false);
                        applyScale((LivingEntity)enderDragon, dragonSize);
                        if (enderDragon.getBossBar() != null) {
                            enderDragon.getBossBar().setVisible(false);
                            enderDragon.getBossBar().removeAll();
                        }
                    });
                    for (ComplexEntityPart complexEntityPart : enderDragon2.getParts()) {
                        complexEntityPart.setMetadata("aesthetictpa_anim", (MetadataValue)new FixedMetadataValue((Plugin)aestheticTPA, (Object)true));
                    }
                    warmupTask.addSpawnedEntity((Entity)enderDragon2);
                }
                catch (Exception exception) {
                    aestheticTPA.getLogger().warning("Could not spawn animation dragon: " + exception.getMessage());
                }
                break;
            }
            case WOLF: {
                if (cfgBool("wolf.sounds", true)) {
                    world.playSound(location, Sound.ENTITY_WOLF_HOWL, 0.85f, 1.0f);
                }
                try {
                    int count = Math.max(1, Math.min(8, (int)cfg("wolf.count", 2.0)));
                    double radius = cfg("wolf.radius", 1.3);
                    double wolfSize = cfg("wolf.size", 1.0);
                    Location center = base(player, "wolf");
                    for (int i = 0; i < count; ++i) {
                        double a = Math.PI * 2 * (double)i / (double)count;
                        Location spawnLoc = center.clone().add(Math.cos(a) * radius, 0.0, Math.sin(a) * radius);
                        Wolf spawned = (Wolf)world.spawn(spawnLoc, Wolf.class, wolf -> {
                            wolf.setMetadata("aesthetictpa_anim", (MetadataValue)new FixedMetadataValue((Plugin)aestheticTPA, (Object)true));
                            wolf.setInvulnerable(true);
                            wolf.setSilent(true);
                            wolf.setCollidable(false);
                            wolf.setAI(false);
                            wolf.setTamed(true);
                            wolf.setOwner((AnimalTamer)player);
                            applyScale((LivingEntity)wolf, wolfSize);
                        });
                        warmupTask.addSpawnedEntity((Entity)spawned);
                    }
                }
                catch (Exception exception) {
                    aestheticTPA.getLogger().warning("Could not spawn animation wolves: " + exception.getMessage());
                }
                break;
            }
            case PHOENIX: {
                if (cfgBool("phoenix.sounds", true)) {
                    world.playSound(location, Sound.ITEM_FIRECHARGE_USE, 0.9f, 1.0f);
                    world.playSound(location, Sound.BLOCK_FIRE_AMBIENT, 0.8f, 1.2f);
                }
                break;
            }
            case ANGEL: {
                if (cfgBool("angel.sounds", true)) {
                    world.playSound(location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.6f);
                }
                break;
            }
        }
    }

    public static void renderWarmupStep(Player player, TeleportAnimation teleportAnimation, int n, WarmupTask warmupTask) {
        Location location = player.getLocation();
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        switch (teleportAnimation) {
            case DRAGON: {
                Location location2 = dragonLocation(player, n);
                for (Entity entity : warmupTask.getSpawnedEntities()) {
                    if (!(entity instanceof EnderDragon) || !entity.isValid()) continue;
                    EnderDragon enderDragon = (EnderDragon)entity;
                    enderDragon.teleport(location2);
                    if (enderDragon.getBossBar() == null) continue;
                    enderDragon.getBossBar().setVisible(false);
                    enderDragon.getBossBar().removeAll();
                }
                if (n % 5 == 0 && cfgBool("dragon.sounds", true)) {
                    world.playSound(location2, Sound.ENTITY_ENDER_DRAGON_FLAP, 0.6f, 1.1f);
                }
                if (cfgBool("dragon.particles", true)) {
                    world.spawnParticle(Particle.DRAGON_BREATH, location2.clone().subtract(0.0, 0.8, 0.0), 2, 0.1, 0.1, 0.1, 0.02);
                    world.spawnParticle(Particle.SOUL_FIRE_FLAME, location2.clone().subtract(0.0, 0.5, 0.0), 1, 0.0, 0.0, 0.0, 0.01);
                    world.spawnParticle(Particle.PORTAL, location.clone().add(0.0, 1.0, 0.0), 4, 0.3, 0.4, 0.3, 0.05);
                }
                break;
            }
            case WOLF: {
                Location center = base(player, "wolf");
                double d = (double)n * cfg("wolf.speed", 0.25);
                double d6 = cfg("wolf.radius", 1.3);
                boolean particles = cfgBool("wolf.particles", true);
                int total = 0;
                for (Entity entity : warmupTask.getSpawnedEntities()) {
                    if (entity instanceof Wolf) ++total;
                }
                int n2 = 0;
                for (Entity entity : warmupTask.getSpawnedEntities()) {
                    if (!(entity instanceof Wolf) || !entity.isValid()) continue;
                    double d7 = d + (double)n2 * (Math.PI * 2) / (double)Math.max(1, total);
                    double d8 = Math.cos(d7) * d6;
                    double d9 = Math.sin(d7) * d6;
                    float f = (float)Math.toDegrees(Math.atan2(-d8, d9));
                    Location location3 = center.clone().add(d8, 0.0, d9);
                    location3.setYaw(f);
                    entity.teleport(location3);
                    ++n2;
                }
                if (!particles) break;
                world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, center.clone().add(Math.cos(d) * d6, 0.1, Math.sin(d) * d6), 1, 0.01, 0.01, 0.01, 0.005);
                if (n % 4 != 0) break;
                world.spawnParticle(Particle.HEART, center.clone().add(Math.cos(-d) * 0.8, 1.0 + Math.sin(d) * 0.3, Math.sin(-d) * 0.8), 1, 0.0, 0.0, 0.0, 0.0);
                break;
            }
            case ANGEL: {
                if (!cfgBool("angel.particles", true)) break;
                Location center = base(player, "angel");
                AnimationRenderer.renderAngelWings(player, n);
                double d = cfg("angel.halo-radius", 0.45);
                double haloY = cfg("angel.halo-height", 2.25);
                double d10 = (double)n * cfg("angel.halo-speed", 0.3);
                world.spawnParticle(Particle.TOTEM, center.clone().add(Math.cos(d10) * d, haloY, Math.sin(d10) * d), 1, 0.0, 0.0, 0.0, 0.01);
                world.spawnParticle(Particle.END_ROD, center.clone().add(Math.cos(d10 + Math.PI) * d, haloY, Math.sin(d10 + Math.PI) * d), 1, 0.0, 0.0, 0.0, 0.01);
                break;
            }
            case PHOENIX: {
                if (!cfgBool("phoenix.particles", true)) break;
                Location center = base(player, "phoenix");
                double d = (double)n * cfg("phoenix.speed", 0.35);
                double maxHeight = cfg("phoenix.height", 2.2);
                double d11 = (double)n * cfg("phoenix.rise-speed", 0.1) % Math.max(0.1, maxHeight);
                double d12 = cfg("phoenix.radius", 0.9);
                world.spawnParticle(Particle.FLAME, center.clone().add(Math.cos(d) * d12, d11, Math.sin(d) * d12), 1, 0.0, 0.0, 0.0, 0.01);
                world.spawnParticle(Particle.SOUL_FIRE_FLAME, center.clone().add(Math.cos(d + 2.0943951023931953) * d12, d11, Math.sin(d + 2.0943951023931953) * d12), 1, 0.0, 0.0, 0.0, 0.01);
                world.spawnParticle(Particle.FLAME, center.clone().add(Math.cos(d + 4.1887902047863905) * d12, d11, Math.sin(d + 4.1887902047863905) * d12), 1, 0.0, 0.0, 0.0, 0.01);
                if (n % 5 != 0) break;
                world.spawnParticle(Particle.LAVA, center.clone().add(0.0, 0.2, 0.0), 2, 0.3, 0.1, 0.3, 0.0);
                break;
            }
            default: {
                if (!cfgBool("portal.particles", true)) break;
                Location center = base(player, "portal");
                double d = (double)n * cfg("portal.speed", 0.35);
                double d13 = (double)n * cfg("portal.rise-speed", 0.08) % Math.max(0.1, cfg("portal.height", 2.1));
                double d14 = cfg("portal.radius", 0.85);
                world.spawnParticle(Particle.PORTAL, center.clone().add(Math.cos(d) * d14, d13, Math.sin(d) * d14), 3, 0.02, 0.02, 0.02, 0.05);
                world.spawnParticle(Particle.ENCHANTMENT_TABLE, center.clone().add(Math.cos(d + Math.PI) * d14, d13, Math.sin(d + Math.PI) * d14), 1, 0.0, 0.0, 0.0, 0.05);
                break;
            }
        }
    }

    private static void renderAngelWings(Player player, int n) {
        double[][] dArrayArray;
        Location location = base(player, "angel");
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        double wingSize = cfg("angel.wings-size", 1.0);
        double d = Math.toRadians(-player.getLocation().getYaw() - 90.0f);
        double d2 = Math.sin((double)n * cfg("angel.flap-speed", 0.4)) * cfg("angel.flap-amount", 0.35);
        Vector vector = new Vector(Math.cos(d), 0.0, Math.sin(d));
        Vector vector2 = new Vector(-vector.getZ(), 0.0, vector.getX());
        Location location2 = location.clone().add(0.0, cfg("angel.wings-height", 1.25), 0.0).subtract(vector.clone().multiply(0.25));
        for (double[] dArray : dArrayArray = new double[][]{{0.25, 0.1}, {0.55, 0.35}, {0.85, 0.65}, {1.15, 0.85}, {1.35, 0.7}, {1.1, 0.4}, {0.8, 0.15}, {0.45, 0.0}}) {
            double d3 = dArray[0] * wingSize;
            double d4 = dArray[1] * wingSize;
            Vector vector3 = vector2.clone().multiply(d3).add(vector.clone().multiply(-d3 * d2)).add(new Vector(0.0, d4, 0.0));
            world.spawnParticle(Particle.TOTEM, location2.clone().add(vector3), 1, 0.0, 0.0, 0.0, 0.01);
            Vector vector4 = vector2.clone().multiply(-d3).add(vector.clone().multiply(-d3 * d2)).add(new Vector(0.0, d4, 0.0));
            world.spawnParticle(Particle.TOTEM, location2.clone().add(vector4), 1, 0.0, 0.0, 0.0, 0.01);
        }
        if (n % 3 == 0) {
            world.spawnParticle(Particle.END_ROD, location2.clone().add(vector2.clone().multiply(1.15 * wingSize)), 1, 0.0, 0.0, 0.0, 0.0);
            world.spawnParticle(Particle.END_ROD, location2.clone().add(vector2.clone().multiply(-1.15 * wingSize)), 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    public static void renderArrival(Location location, Player player, TeleportAnimation teleportAnimation) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        Location location2 = location.clone().add(0.0, 0.2, 0.0);
        switch (teleportAnimation) {
            case DRAGON: {
                world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.95f, 1.0f);
                world.playSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 0.8f);
                AnimationRenderer.spawnRing(world, location2, 2.5, 24, Particle.SOUL_FIRE_FLAME);
                world.spawnParticle(Particle.DRAGON_BREATH, location2.clone().add(0.0, 1.2, 0.0), 55, 0.7, 0.9, 0.7, 0.12);
                world.spawnParticle(Particle.PORTAL, location2.clone().add(0.0, 1.2, 0.0), 40, 0.5, 0.7, 0.5, 0.08);
                break;
            }
            case WOLF: {
                world.playSound(location, Sound.ENTITY_WOLF_HOWL, 0.9f, 1.0f);
                world.playSound(location, Sound.ENTITY_WOLF_AMBIENT, 1.0f, 1.1f);
                AnimationRenderer.spawnRing(world, location2, 1.8, 16, Particle.HEART);
                world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location2.clone().add(0.0, 0.5, 0.0), 16, 0.5, 0.4, 0.5, 0.02);
                world.spawnParticle(Particle.VILLAGER_HAPPY, location2.clone().add(0.0, 0.8, 0.0), 10, 0.4, 0.4, 0.4, 0.0);
                break;
            }
            case PHOENIX: {
                world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.4f);
                world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);
                AnimationRenderer.spawnRing(world, location2, 2.0, 20, Particle.FLAME);
                world.spawnParticle(Particle.FLAME, location2.clone().add(0.0, 1.0, 0.0), 45, 0.5, 0.8, 0.5, 0.08);
                world.spawnParticle(Particle.LAVA, location2.clone().add(0.0, 0.5, 0.0), 6, 0.3, 0.3, 0.3, 0.0);
                break;
            }
            case ANGEL: {
                world.playSound(location, Sound.ITEM_TOTEM_USE, 0.85f, 1.3f);
                world.playSound(location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.2f);
                AnimationRenderer.spawnRing(world, location2, 1.8, 18, Particle.TOTEM);
                world.spawnParticle(Particle.END_ROD, location2.clone().add(0.0, 1.2, 0.0), 20, 0.4, 0.7, 0.4, 0.05);
                world.spawnParticle(Particle.TOTEM, location2.clone().add(0.0, 1.2, 0.0), 45, 0.5, 0.8, 0.5, 0.15);
                break;
            }
            default: {
                world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                AnimationRenderer.spawnRing(world, location2, 1.5, 14, Particle.PORTAL);
                world.spawnParticle(Particle.PORTAL, location2.clone().add(0.0, 1.0, 0.0), 35, 0.4, 0.6, 0.4, 0.08);
            }
        }
    }

    private static void spawnRing(World world, Location location, double d, int n, Particle particle) {
        for (int i = 0; i < n; ++i) {
            double d2 = Math.PI * 2 * (double)i / (double)n;
            double d3 = Math.cos(d2) * d;
            double d4 = Math.sin(d2) * d;
            world.spawnParticle(particle, location.clone().add(d3, 0.1, d4), 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}

