/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Particle
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package com.aesthetic.tpa.manager;

import com.aesthetic.tpa.AestheticTPA;
import com.aesthetic.tpa.model.RequestType;
import com.aesthetic.tpa.model.TeleportAnimation;
import com.aesthetic.tpa.model.WarmupTask;
import com.aesthetic.tpa.util.AnimationRenderer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportWarmupManager {
    private final AestheticTPA plugin;
    private final Map<UUID, WarmupTask> activeWarmups = new ConcurrentHashMap<UUID, WarmupTask>();

    public TeleportWarmupManager(AestheticTPA aestheticTPA) {
        this.plugin = aestheticTPA;
    }

    public boolean isWarmingUp(UUID uUID) {
        return this.activeWarmups.containsKey(uUID);
    }

    public WarmupTask getWarmup(UUID uUID) {
        return this.activeWarmups.get(uUID);
    }

    public void startWarmup(Player player, Player player2, RequestType requestType) {
        int n = this.plugin.getConfigManager().getWarmupSeconds();
        TeleportAnimation teleportAnimation = this.plugin.getPlayerSettingsManager().getEquippedAnimation(player.getUniqueId());
        if (!teleportAnimation.hasPermission(player, this.plugin)) {
            teleportAnimation = TeleportAnimation.PORTAL;
        }
        if (n <= 0 || player.hasPermission("aesthetictpa.bypass.warmup")) {
            this.executeTeleport(player, player2, teleportAnimation);
            return;
        }
        if (this.activeWarmups.containsKey(player.getUniqueId())) {
            this.cancelWarmup(player.getUniqueId(), CancelReason.MANUAL);
        }
        final WarmupTask warmupTask = new WarmupTask(player.getUniqueId(), player2.getUniqueId(), player.getLocation(), requestType, n);
        this.notifyCountdown(player, n);
        final TeleportAnimation teleportAnimation2 = teleportAnimation;
        AnimationRenderer.initWarmup(this.plugin, player, teleportAnimation2, warmupTask);
        warmupTask.setTask(new BukkitRunnable(){
            private int tickCount = 0;
            private int step = 0;

            public void run() {
                Player player = Bukkit.getPlayer((UUID)warmupTask.getPlayerToTeleport());
                Player player2 = Bukkit.getPlayer((UUID)warmupTask.getDestinationPlayer());
                if (player == null || !player.isOnline()) {
                    TeleportWarmupManager.this.cancelWarmup(warmupTask.getPlayerToTeleport(), CancelReason.DISCONNECT);
                    return;
                }
                if (player2 == null || !player2.isOnline()) {
                    TeleportWarmupManager.this.cancelWarmup(warmupTask.getPlayerToTeleport(), CancelReason.DISCONNECT);
                    TeleportWarmupManager.this.plugin.getMessageManager().send(player, "player-not-found", "%player%", "Destination Player");
                    return;
                }
                if (TeleportWarmupManager.this.plugin.getConfigManager().isSpawnParticles()) {
                    AnimationRenderer.renderWarmupStep(player, teleportAnimation2, this.step++, warmupTask);
                }
                this.tickCount += 2;
                if (this.tickCount % 20 == 0) {
                    warmupTask.decrementRemaining();
                    int n = warmupTask.getRemainingSeconds();
                    if (n > 0) {
                        TeleportWarmupManager.this.notifyCountdown(player, n);
                    } else {
                        warmupTask.cleanupEntities();
                        TeleportWarmupManager.this.activeWarmups.remove(warmupTask.getPlayerToTeleport());
                        this.cancel();
                        TeleportWarmupManager.this.executeTeleport(player, player2, teleportAnimation2);
                    }
                }
            }
        }.runTaskTimer((Plugin)this.plugin, 2L, 2L));
        this.activeWarmups.put(player.getUniqueId(), warmupTask);
    }

    private void notifyCountdown(Player player, int n) {
        String string;
        if (this.plugin.getConfigManager().isShowActionBar()) {
            string = this.plugin.getMessageManager().getMessage("warmup.action-bar", "%seconds%", String.valueOf(n));
            this.plugin.getMessageManager().sendActionBar(player, string);
        }
        if (this.plugin.getConfigManager().isShowTitle()) {
            string = this.plugin.getMessageManager().getMessage("warmup.title", "%seconds%", String.valueOf(n));
            String string2 = this.plugin.getMessageManager().getMessage("warmup.subtitle", "%seconds%", String.valueOf(n));
            this.plugin.getMessageManager().sendTitle(player, string, string2, 0, 25, 5);
        }
        this.plugin.getMessageManager().playSound(player, this.plugin.getConfigManager().getSoundCountdownTick());
    }

    public void cancelWarmup(UUID uUID, CancelReason cancelReason) {
        WarmupTask warmupTask = this.activeWarmups.remove(uUID);
        if (warmupTask == null) {
            return;
        }
        warmupTask.cancel();
        Player player = Bukkit.getPlayer((UUID)uUID);
        Player player2 = Bukkit.getPlayer((UUID)warmupTask.getDestinationPlayer());
        if (player != null && player.isOnline()) {
            this.plugin.getMessageManager().playSound(player, this.plugin.getConfigManager().getSoundTeleportCancelled());
            switch (cancelReason.ordinal()) {
                case 0: {
                    this.plugin.getMessageManager().send(player, "warmup.cancelled-move", new String[0]);
                    break;
                }
                case 1: {
                    this.plugin.getMessageManager().send(player, "warmup.cancelled-damage", new String[0]);
                    break;
                }
            }
        }
        if (player2 != null && player2.isOnline() && player != null && (cancelReason == CancelReason.MOVED || cancelReason == CancelReason.DAMAGED)) {
            this.plugin.getMessageManager().send(player2, "request-cancelled-target", "%player%", player.getName());
        }
    }

    private void executeTeleport(Player player, Player player2, TeleportAnimation teleportAnimation) {
        Location location = player2.getLocation();
        if (this.plugin.getConfigManager().isSafeTeleport()) {
            location = this.findSafeLocation(location);
        }
        if (this.plugin.getConfigManager().isSpawnParticles()) {
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0.0, 1.0, 0.0), 25, 0.4, 0.6, 0.4, 0.05);
        }
        player.teleport(location);
        if (this.plugin.getConfigManager().isSpawnParticles()) {
            AnimationRenderer.renderArrival(location, player, teleportAnimation);
        } else {
            this.plugin.getMessageManager().playSound(player, this.plugin.getConfigManager().getSoundTeleportSuccess());
            this.plugin.getMessageManager().playSound(player2, this.plugin.getConfigManager().getSoundTeleportSuccess());
        }
        this.plugin.getMessageManager().send(player, "warmup.teleported", new String[0]);
    }

    private Location findSafeLocation(Location location) {
        Location location2 = location.clone();
        Block block = location2.getBlock();
        Block block2 = block.getRelative(0, 1, 0);
        Block block3 = block.getRelative(0, -1, 0);
        if (block.getType().isSolid() || block2.getType().isSolid()) {
            for (int i = 1; i <= 3; ++i) {
                Block block4 = location2.clone().add(0.0, (double)i, 0.0).getBlock();
                Block block5 = block4.getRelative(0, 1, 0);
                if (block4.getType().isSolid() || block5.getType().isSolid()) continue;
                location2.add(0.0, (double)i, 0.0);
                return location2;
            }
        }
        if (block3.getType() == Material.LAVA || block.getType() == Material.LAVA) {
            location2.add(0.0, 1.0, 0.0);
        }
        return location2;
    }

    public void cancelAll() {
        for (WarmupTask warmupTask : this.activeWarmups.values()) {
            warmupTask.cancel();
        }
        this.activeWarmups.clear();
    }

    public static enum CancelReason {
        MOVED,
        DAMAGED,
        DISCONNECT,
        MANUAL;

    }
}

