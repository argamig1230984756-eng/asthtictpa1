/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageEvent
 */
package com.aesthetic.tpa.listener;

import com.aesthetic.tpa.AestheticTPA;
import com.aesthetic.tpa.manager.TeleportWarmupManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener
implements Listener {
    private final AestheticTPA plugin;

    public PlayerDamageListener(AestheticTPA aestheticTPA) {
        this.plugin = aestheticTPA;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerDamage(EntityDamageEvent entityDamageEvent) {
        if (!this.plugin.getConfigManager().isCancelOnDamage()) {
            return;
        }
        if (!(entityDamageEvent.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)entityDamageEvent.getEntity();
        if (this.plugin.getTeleportWarmupManager().isWarmingUp(player.getUniqueId())) {
            this.plugin.getTeleportWarmupManager().cancelWarmup(player.getUniqueId(), TeleportWarmupManager.CancelReason.DAMAGED);
        }
    }
}

