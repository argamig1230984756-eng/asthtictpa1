/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package com.aesthetic.tpa.listener;

import com.aesthetic.tpa.AestheticTPA;
import com.aesthetic.tpa.manager.TeleportWarmupManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener
implements Listener {
    private final AestheticTPA plugin;

    public PlayerQuitListener(AestheticTPA aestheticTPA) {
        this.plugin = aestheticTPA;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        if (this.plugin.getTeleportWarmupManager().isWarmingUp(player.getUniqueId())) {
            this.plugin.getTeleportWarmupManager().cancelWarmup(player.getUniqueId(), TeleportWarmupManager.CancelReason.DISCONNECT);
        }
        this.plugin.getRequestManager().clearPlayer(player.getUniqueId());
    }
}

