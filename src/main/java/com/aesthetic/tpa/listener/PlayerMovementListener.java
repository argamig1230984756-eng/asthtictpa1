/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerMoveEvent
 */
package com.aesthetic.tpa.listener;

import com.aesthetic.tpa.AestheticTPA;
import com.aesthetic.tpa.manager.TeleportWarmupManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener
implements Listener {
    private final AestheticTPA plugin;

    public PlayerMovementListener(AestheticTPA aestheticTPA) {
        this.plugin = aestheticTPA;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        double d;
        if (!this.plugin.getConfigManager().isCancelOnMove()) {
            return;
        }
        Player player = playerMoveEvent.getPlayer();
        if (!this.plugin.getTeleportWarmupManager().isWarmingUp(player.getUniqueId())) {
            return;
        }
        Location location = playerMoveEvent.getFrom();
        Location location2 = playerMoveEvent.getTo();
        if (location2 == null) {
            return;
        }
        if (this.plugin.getConfigManager().isBlockMovementOnly()) {
            if (location.getBlockX() != location2.getBlockX() || location.getBlockY() != location2.getBlockY() || location.getBlockZ() != location2.getBlockZ()) {
                this.plugin.getTeleportWarmupManager().cancelWarmup(player.getUniqueId(), TeleportWarmupManager.CancelReason.MOVED);
            }
        } else if ((location.getX() != location2.getX() || location.getY() != location2.getY() || location.getZ() != location2.getZ()) && (d = Math.pow(location.getX() - location2.getX(), 2.0) + Math.pow(location.getY() - location2.getY(), 2.0) + Math.pow(location.getZ() - location2.getZ(), 2.0)) > 0.0025) {
            this.plugin.getTeleportWarmupManager().cancelWarmup(player.getUniqueId(), TeleportWarmupManager.CancelReason.MOVED);
        }
    }
}

