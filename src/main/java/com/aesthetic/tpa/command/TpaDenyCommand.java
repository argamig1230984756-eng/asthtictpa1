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
import com.aesthetic.tpa.model.TeleportRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TpaDenyCommand
implements CommandExecutor,
TabCompleter {
    private final AestheticTPA plugin;

    public TpaDenyCommand(AestheticTPA aestheticTPA) {
        this.plugin = aestheticTPA;
    }

    /*
     * Enabled aggressive block sorting
     */
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
        UUID uUID = null;
        if (stringArray.length > 0) {
            Player player2 = Bukkit.getPlayer((String)stringArray[0]);
            if (player2 == null) {
                this.plugin.getMessageManager().send(player, "player-not-found", "%player%", stringArray[0]);
                return true;
            }
            uUID = player2.getUniqueId();
        } else {
            Collection<TeleportRequest> collection = this.plugin.getRequestManager().getIncoming(player.getUniqueId());
            if (collection.isEmpty()) {
                this.plugin.getMessageManager().send(player, "no-pending-requests", new String[0]);
                return true;
            }
            if (collection.size() != 1) {
                this.plugin.getRequestManager().openDecisionOrList(player, null);
                return true;
            }
            uUID = collection.iterator().next().getSender();
        }
        this.plugin.getRequestManager().executeDeny(player, uUID);
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (commandSender instanceof Player && stringArray.length == 1) {
            Player player = (Player)commandSender;
            String string2 = stringArray[0].toLowerCase();
            ArrayList<String> arrayList = new ArrayList<String>();
            for (TeleportRequest teleportRequest : this.plugin.getRequestManager().getIncoming(player.getUniqueId())) {
                Player player2 = Bukkit.getPlayer((UUID)teleportRequest.getSender());
                if (player2 == null || !player2.getName().toLowerCase().startsWith(string2)) continue;
                arrayList.add(player2.getName());
            }
            return arrayList;
        }
        return Collections.emptyList();
    }
}

