/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 */
package com.aesthetic.tpa.model;

import com.aesthetic.tpa.AestheticTPA;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum TeleportAnimation {
    PORTAL("Portal", "aesthetictpa.animation.portal", Material.ENDER_EYE, "Classic ascending purple portal helix."),
    DRAGON("Ender Dragon", "aesthetictpa.animation.dragon", Material.DRAGON_HEAD, "Real hovering Ender Dragon with breath vortex & roar."),
    WOLF("Spirit Wolf", "aesthetictpa.animation.wolf", Material.BONE, "Real spirit wolf pack circling you with howl & heart burst."),
    PHOENIX("Phoenix Flame", "aesthetictpa.animation.phoenix", Material.BLAZE_POWDER, "Triple flame vortex with rising embers & fiery explosion."),
    ANGEL("Celestial Angel", "aesthetictpa.animation.angel", Material.FEATHER, "Flapping glowing angel wings, golden halo & totem chime.");

    private final String displayName;
    private final String permission;
    private final Material icon;
    private final String description;

    private TeleportAnimation(String string2, String string3, Material material, String string4) {
        this.displayName = string2;
        this.permission = string3;
        this.icon = material;
        this.description = string4;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getPermission() {
        return this.permission;
    }

    public Material getIcon() {
        return this.icon;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean hasPermission(Player player, AestheticTPA aestheticTPA) {
        if (this == PORTAL) {
            return true;
        }
        if (player.isOp() || player.hasPermission("aesthetictpa.animations") || player.hasPermission("aesthetictpa.animation.*") || player.hasPermission(this.permission)) {
            return true;
        }
        if (aestheticTPA != null && aestheticTPA.getPlayerSettingsManager() != null) {
            return aestheticTPA.getPlayerSettingsManager().isAnimationUnlocked(player.getUniqueId(), this);
        }
        return false;
    }

    public static TeleportAnimation fromName(String string) {
        if (string == null) {
            return PORTAL;
        }
        try {
            return TeleportAnimation.valueOf(string.toUpperCase());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return PORTAL;
        }
    }
}

