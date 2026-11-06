/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package com.aesthetic.tpa.command;

import com.aesthetic.tpa.AestheticTPA;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TpaReloadCommand
implements CommandExecutor {
    private final AestheticTPA plugin;

    public TpaReloadCommand(AestheticTPA aestheticTPA) {
        this.plugin = aestheticTPA;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (!commandSender.hasPermission("aesthetictpa.admin")) {
            commandSender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        this.plugin.getConfigManager().loadConfig();
        this.plugin.getMessageManager().loadMessages();
        this.plugin.getRequestManager().startCleanupTask();
        commandSender.sendMessage(this.plugin.getMessageManager().getMessage("config-reloaded"));
        return true;
    }
}

