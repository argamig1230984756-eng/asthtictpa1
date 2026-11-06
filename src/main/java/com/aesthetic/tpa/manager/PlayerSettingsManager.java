/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.Plugin
 */
package com.aesthetic.tpa.manager;

import com.aesthetic.tpa.AestheticTPA;
import com.aesthetic.tpa.model.TeleportAnimation;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class PlayerSettingsManager {
    private final AestheticTPA plugin;
    private final Map<UUID, Boolean> tpaToggles = new ConcurrentHashMap<UUID, Boolean>();
    private final Map<UUID, Boolean> tpaHereToggles = new ConcurrentHashMap<UUID, Boolean>();
    private final Map<UUID, TeleportAnimation> equippedAnimations = new ConcurrentHashMap<UUID, TeleportAnimation>();
    private final Map<UUID, Set<TeleportAnimation>> unlockedAnimations = new ConcurrentHashMap<UUID, Set<TeleportAnimation>>();
    private File dataFile;
    private FileConfiguration dataConfig;

    public PlayerSettingsManager(AestheticTPA aestheticTPA) {
        this.plugin = aestheticTPA;
        this.loadData();
    }

    public void loadData() {
        this.dataFile = new File(this.plugin.getDataFolder(), "playerdata.yml");
        if (!this.dataFile.exists()) {
            try {
                this.dataFile.getParentFile().mkdirs();
                this.dataFile.createNewFile();
            }
            catch (IOException iOException) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not create playerdata.yml", iOException);
            }
        }
        this.dataConfig = YamlConfiguration.loadConfiguration((File)this.dataFile);
        this.tpaToggles.clear();
        this.tpaHereToggles.clear();
        this.equippedAnimations.clear();
        this.unlockedAnimations.clear();
        ConfigurationSection configurationSection = this.dataConfig.getConfigurationSection("players");
        if (configurationSection != null) {
            for (String string : configurationSection.getKeys(false)) {
                try {
                    UUID uUID = UUID.fromString(string);
                    boolean bl = configurationSection.getBoolean(string + ".tpa", this.plugin.getConfigManager().isDefaultTpaEnabled());
                    boolean bl2 = configurationSection.getBoolean(string + ".tpahere", this.plugin.getConfigManager().isDefaultTpaHereEnabled());
                    String string2 = configurationSection.getString(string + ".animation", "PORTAL");
                    List list = configurationSection.getStringList(string + ".unlocked-animations");
                    HashSet<TeleportAnimation> hashSet = new HashSet<TeleportAnimation>();
                    for (String string3 : list) {
                        hashSet.add(TeleportAnimation.fromName(string3));
                    }
                    this.tpaToggles.put(uUID, bl);
                    this.tpaHereToggles.put(uUID, bl2);
                    this.equippedAnimations.put(uUID, TeleportAnimation.fromName(string2));
                    this.unlockedAnimations.put(uUID, hashSet);
                }
                catch (IllegalArgumentException illegalArgumentException) {}
            }
        }
    }

    public void saveData() {
        if (this.dataConfig == null || this.dataFile == null) {
            return;
        }
        for (Map.Entry<UUID, Boolean> entry : this.tpaToggles.entrySet()) {
            this.dataConfig.set("players." + entry.getKey().toString() + ".tpa", (Object)entry.getValue());
        }
        for (Map.Entry<UUID, Boolean> entry : this.tpaHereToggles.entrySet()) {
            this.dataConfig.set("players." + entry.getKey().toString() + ".tpahere", (Object)entry.getValue());
        }
        for (Map.Entry<UUID, Object> entry : this.equippedAnimations.entrySet()) {
            this.dataConfig.set("players." + entry.getKey().toString() + ".animation", (Object)((TeleportAnimation)((Object)entry.getValue())).name());
        }
        for (Map.Entry<UUID, Object> entry : this.unlockedAnimations.entrySet()) {
            List list = ((Set)entry.getValue()).stream().map(Enum::name).collect(Collectors.toList());
            this.dataConfig.set("players." + entry.getKey().toString() + ".unlocked-animations", list);
        }
        try {
            this.dataConfig.save(this.dataFile);
        }
        catch (IOException iOException) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save playerdata.yml", iOException);
        }
    }

    public void saveAsync() {
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, this::saveData);
    }

    public boolean isTpaEnabled(UUID uUID2) {
        return this.tpaToggles.computeIfAbsent(uUID2, uUID -> this.plugin.getConfigManager().isDefaultTpaEnabled());
    }

    public void setTpaEnabled(UUID uUID, boolean bl) {
        this.tpaToggles.put(uUID, bl);
        this.saveAsync();
    }

    public boolean toggleTpa(UUID uUID) {
        boolean bl = !this.isTpaEnabled(uUID);
        this.setTpaEnabled(uUID, bl);
        return bl;
    }

    public boolean isTpaHereEnabled(UUID uUID2) {
        return this.tpaHereToggles.computeIfAbsent(uUID2, uUID -> this.plugin.getConfigManager().isDefaultTpaHereEnabled());
    }

    public void setTpaHereEnabled(UUID uUID, boolean bl) {
        this.tpaHereToggles.put(uUID, bl);
        this.saveAsync();
    }

    public boolean toggleTpaHere(UUID uUID) {
        boolean bl = !this.isTpaHereEnabled(uUID);
        this.setTpaHereEnabled(uUID, bl);
        return bl;
    }

    public TeleportAnimation getEquippedAnimation(UUID uUID2) {
        return this.equippedAnimations.computeIfAbsent(uUID2, uUID -> TeleportAnimation.PORTAL);
    }

    public void setEquippedAnimation(UUID uUID, TeleportAnimation teleportAnimation) {
        this.equippedAnimations.put(uUID, teleportAnimation != null ? teleportAnimation : TeleportAnimation.PORTAL);
        this.saveAsync();
    }

    public boolean isAnimationUnlocked(UUID uUID, TeleportAnimation teleportAnimation) {
        if (teleportAnimation == TeleportAnimation.PORTAL) {
            return true;
        }
        Set<TeleportAnimation> set = this.unlockedAnimations.get(uUID);
        return set != null && set.contains((Object)teleportAnimation);
    }

    public void unlockAnimation(UUID uUID2, TeleportAnimation teleportAnimation) {
        this.unlockedAnimations.computeIfAbsent(uUID2, uUID -> new HashSet()).add(teleportAnimation);
        this.saveAsync();
    }

    public void unlockAllAnimations(UUID uUID2) {
        Set set = this.unlockedAnimations.computeIfAbsent(uUID2, uUID -> new HashSet());
        set.addAll(Arrays.asList(TeleportAnimation.values()));
        this.saveAsync();
    }

    public void lockAnimation(UUID uUID, TeleportAnimation teleportAnimation) {
        Set<TeleportAnimation> set = this.unlockedAnimations.get(uUID);
        if (set != null) {
            set.remove((Object)teleportAnimation);
            this.saveAsync();
        }
        if (this.getEquippedAnimation(uUID) == teleportAnimation) {
            this.setEquippedAnimation(uUID, TeleportAnimation.PORTAL);
        }
    }

    public void lockAllAnimations(UUID uUID) {
        this.unlockedAnimations.remove(uUID);
        this.setEquippedAnimation(uUID, TeleportAnimation.PORTAL);
        this.saveAsync();
    }
}

