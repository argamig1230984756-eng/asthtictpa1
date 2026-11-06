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
import com.aesthetic.tpa.gui.MainMenuGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaMenuCommand
implements CommandExecutor {
    private final AestheticTPA plugin;

    public TpaMenuCommand(AestheticTPA aestheticTPA) {
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
        MainMenuGui.open(this.plugin, player);
        return true;
    }
}

