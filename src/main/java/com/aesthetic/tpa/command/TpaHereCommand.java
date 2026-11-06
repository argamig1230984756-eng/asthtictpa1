/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 */
package com.aesthetic.tpa.command;

import com.aesthetic.tpa.AestheticTPA;
import com.aesthetic.tpa.gui.PlayerListGui;
import com.aesthetic.tpa.model.RequestType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TpaHereCommand
implements CommandExecutor,
TabCompleter {
    private final AestheticTPA plugin;

    public TpaHereCommand(AestheticTPA aestheticTPA) {
        this.plugin = aestheticTPA;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(this.plugin.getMessageManager().getMessage("player-only"));
            return true;
        }
        Player player = (Player)commandSender;
        if (!player.hasPermission("aesthetictpa.tpahere")) {
            this.plugin.getMessageManager().send(player, "no-permission", new String[0]);
            return true;
        }
        if (stringArray.length == 0) {
            PlayerListGui.open(this.plugin, player, 0);
            return true;
        }
        String string2 = stringArray[0];
        Player player2 = Bukkit.getPlayer((String)string2);
        if (player2 == null || !player2.isOnline()) {
            this.plugin.getMessageManager().send(player, "player-not-found", "%player%", string2);
            return true;
        }
        this.plugin.getRequestManager().sendRequest(player, player2, RequestType.TPA_HERE);
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (stringArray.length == 1) {
            String string2 = stringArray[0].toLowerCase();
            ArrayList<String> arrayList = new ArrayList<String>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().equalsIgnoreCase(commandSender.getName()) || !player.getName().toLowerCase().startsWith(string2)) continue;
                arrayList.add(player.getName());
            }
            return arrayList;
        }
        return Collections.emptyList();
    }
}

