/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.aesthetic.tpa.command;

import com.aesthetic.tpa.AestheticTPA;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaToggleCommand
implements CommandExecutor {
    private final AestheticTPA plugin;

    public TpaToggleCommand(AestheticTPA aestheticTPA) {
        this.plugin = aestheticTPA;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessageManager().getMessage("player-only"));
            return true;
        }
        Player player = (Player)commandSender;
        if (!player.hasPermission("aesthetictpa.use")) {
            this.plugin.getMessageManager().send(player, "no-permission", new String[0]);
            return true;
        }
        boolean bl = this.plugin.getPlayerSettingsManager().toggleTpa(player.getUniqueId());
        this.plugin.getMessageManager().playSound(player, this.plugin.getConfigManager().getSoundToggleSetting());
        if (bl) {
            this.plugin.getMessageManager().send(player, "toggle.tpa-enabled", new String[0]);
        } else {
            this.plugin.getMessageManager().send(player, "toggle.tpa-disabled", new String[0]);
        }
        return true;
    }
}

