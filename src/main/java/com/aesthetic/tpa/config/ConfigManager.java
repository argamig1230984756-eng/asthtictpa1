/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Sound
 *  org.bukkit.configuration.file.FileConfiguration
 */
package com.aesthetic.tpa.config;

import com.aesthetic.tpa.AestheticTPA;
import java.util.logging.Level;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final AestheticTPA plugin;
    private FileConfiguration config;
    private int warmupSeconds;
    private boolean cancelOnMove;
    private boolean blockMovementOnly;
    private boolean cancelOnDamage;
    private boolean showActionBar;
    private boolean showTitle;
    private boolean spawnParticles;
    private int timeoutSeconds;
    private int cleanupIntervalSeconds;
    private boolean safeTeleport;
    private boolean soundsEnabled;
    private Sound soundRequestSent;
    private Sound soundRequestReceived;
    private Sound soundCountdownTick;
    private Sound soundTeleportSuccess;
    private Sound soundTeleportCancelled;
    private Sound soundRequestDenied;
    private Sound soundGuiClick;
    private Sound soundToggleSetting;
    private boolean defaultTpaEnabled;
    private boolean defaultTpaHereEnabled;

    public ConfigManager(AestheticTPA aestheticTPA) {
        this.plugin = aestheticTPA;
        this.loadConfig();
    }

    public void loadConfig() {
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
        this.warmupSeconds = this.config.getInt("warmup.time-seconds", 3);
        this.cancelOnMove = this.config.getBoolean("warmup.cancel-on-move", true);
        this.blockMovementOnly = this.config.getBoolean("warmup.block-movement-only", true);
        this.cancelOnDamage = this.config.getBoolean("warmup.cancel-on-damage", true);
        this.showActionBar = this.config.getBoolean("warmup.show-action-bar", true);
        this.showTitle = this.config.getBoolean("warmup.show-title", false);
        this.spawnParticles = this.config.getBoolean("warmup.spawn-particles", true);
        this.timeoutSeconds = this.config.getInt("request.timeout-seconds", 60);
        this.cleanupIntervalSeconds = this.config.getInt("request.cleanup-interval-seconds", 5);
        this.safeTeleport = this.config.getBoolean("safety.safe-teleport", true);
        this.soundsEnabled = this.config.getBoolean("sounds.enabled", true);
        this.soundRequestSent = this.parseSound(this.config.getString("sounds.request-sent"), Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        this.soundRequestReceived = this.parseSound(this.config.getString("sounds.request-received"), Sound.BLOCK_NOTE_BLOCK_CHIME);
        this.soundCountdownTick = this.parseSound(this.config.getString("sounds.countdown-tick"), Sound.BLOCK_NOTE_BLOCK_HAT);
        this.soundTeleportSuccess = this.parseSound(this.config.getString("sounds.teleport-success"), Sound.ENTITY_ENDERMAN_TELEPORT);
        this.soundTeleportCancelled = this.parseSound(this.config.getString("sounds.teleport-cancelled"), Sound.ENTITY_VILLAGER_NO);
        this.soundRequestDenied = this.parseSound(this.config.getString("sounds.request-denied"), Sound.BLOCK_ANVIL_LAND);
        this.soundGuiClick = this.parseSound(this.config.getString("sounds.gui-click"), Sound.UI_BUTTON_CLICK);
        this.soundToggleSetting = this.parseSound(this.config.getString("sounds.toggle-setting"), Sound.BLOCK_NOTE_BLOCK_BELL);
        this.defaultTpaEnabled = this.config.getBoolean("defaults.tpa-enabled", true);
        this.defaultTpaHereEnabled = this.config.getBoolean("defaults.tpahere-enabled", true);
    }

    private Sound parseSound(String string, Sound sound) {
        if (string == null || string.trim().isEmpty()) {
            return sound;
        }
        try {
            return Sound.valueOf((String)string.trim().toUpperCase());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            this.plugin.getLogger().log(Level.WARNING, "Invalid sound configured: " + string + ", using fallback.");
            return sound;
        }
    }

    public int getWarmupSeconds() {
        return this.warmupSeconds;
    }

    public boolean isCancelOnMove() {
        return this.cancelOnMove;
    }

    public boolean isBlockMovementOnly() {
        return this.blockMovementOnly;
    }

    public boolean isCancelOnDamage() {
        return this.cancelOnDamage;
    }

    public boolean isShowActionBar() {
        return this.showActionBar;
    }

    public boolean isShowTitle() {
        return this.showTitle;
    }

    public boolean isSpawnParticles() {
        return this.spawnParticles;
    }

    public int getTimeoutSeconds() {
        return this.timeoutSeconds;
    }

    public int getCleanupIntervalSeconds() {
        return this.cleanupIntervalSeconds;
    }

    public boolean isSafeTeleport() {
        return this.safeTeleport;
    }

    public boolean isSoundsEnabled() {
        return this.soundsEnabled;
    }

    public Sound getSoundRequestSent() {
        return this.soundRequestSent;
    }

    public Sound getSoundRequestReceived() {
        return this.soundRequestReceived;
    }

    public Sound getSoundCountdownTick() {
        return this.soundCountdownTick;
    }

    public Sound getSoundTeleportSuccess() {
        return this.soundTeleportSuccess;
    }

    public Sound getSoundTeleportCancelled() {
        return this.soundTeleportCancelled;
    }

    public Sound getSoundRequestDenied() {
        return this.soundRequestDenied;
    }

    public Sound getSoundGuiClick() {
        return this.soundGuiClick;
    }

    public Sound getSoundToggleSetting() {
        return this.soundToggleSetting;
    }

    public boolean isDefaultTpaEnabled() {
        return this.defaultTpaEnabled;
    }

    public boolean isDefaultTpaHereEnabled() {
        return this.defaultTpaHereEnabled;
    }
}

