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
import com.aesthetic.tpa.gui.AnimationGui;
import com.aesthetic.tpa.gui.MainMenuGui;
import com.aesthetic.tpa.model.RequestType;
import com.aesthetic.tpa.model.TeleportAnimation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TpaCommand
implements CommandExecutor,
TabCompleter {
    private final AestheticTPA plugin;

    public TpaCommand(AestheticTPA aestheticTPA) {
        this.plugin = aestheticTPA;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (!(commandSender instanceof Player)) {
            if (stringArray.length > 0 && (stringArray[0].equalsIgnoreCase("giveanim") || stringArray[0].equalsIgnoreCase("takeanim") || stringArray[0].equalsIgnoreCase("setanim"))) {
                return this.handleAdminAnimCommands(commandSender, stringArray);
            }
            commandSender.sendMessage(this.plugin.getMessageManager().getMessage("player-only"));
            return true;
        }
        Player player = (Player)commandSender;
        if (!player.hasPermission("aesthetictpa.use")) {
            this.plugin.getMessageManager().send(player, "no-permission", new String[0]);
            return true;
        }
        if (stringArray.length == 0 || stringArray[0].equalsIgnoreCase("gui") || stringArray[0].equalsIgnoreCase("menu")) {
            MainMenuGui.open(this.plugin, player);
            return true;
        }
        if (stringArray[0].equalsIgnoreCase("animations") || stringArray[0].equalsIgnoreCase("effects")) {
            AnimationGui.open(this.plugin, player);
            return true;
        }
        if (stringArray[0].equalsIgnoreCase("giveanim") || stringArray[0].equalsIgnoreCase("takeanim") || stringArray[0].equalsIgnoreCase("setanim")) {
            return this.handleAdminAnimCommands(commandSender, stringArray);
        }
        String string2 = stringArray[0];
        Player player2 = Bukkit.getPlayer((String)string2);
        if (player2 == null || !player2.isOnline()) {
            this.plugin.getMessageManager().send(player, "player-not-found", "%player%", string2);
            return true;
        }
        this.plugin.getRequestManager().sendRequest(player, player2, RequestType.TPA);
        return true;
    }

    private boolean handleAdminAnimCommands(CommandSender commandSender, String[] stringArray) {
        if (!commandSender.hasPermission("aesthetictpa.admin")) {
            commandSender.sendMessage(this.plugin.getMessageManager().getMessage("no-permission"));
            return true;
        }
        String string = stringArray[0].toLowerCase();
        if (string.equals("giveanim")) {
            if (stringArray.length < 3) {
                commandSender.sendMessage(this.plugin.getMessageManager().getPrefix() + "\u00a7cUsage: /tpa giveanim <player> <animation|all>");
                return true;
            }
            Player player = Bukkit.getPlayer((String)stringArray[1]);
            if (player == null) {
                commandSender.sendMessage(this.plugin.getMessageManager().getMessage("player-not-found", "%player%", stringArray[1]));
                return true;
            }
            String string2 = stringArray[2].toUpperCase();
            if (string2.equals("ALL")) {
                this.plugin.getPlayerSettingsManager().unlockAllAnimations(player.getUniqueId());
                commandSender.sendMessage(this.plugin.getMessageManager().getPrefix() + "\u00a7aUnlocked ALL teleport animations for \u00a7d" + player.getName() + "\u00a7a!");
                player.sendMessage(this.plugin.getMessageManager().getPrefix() + "\u00a7aYou were granted access to ALL teleport animations!");
            } else {
                TeleportAnimation teleportAnimation = TeleportAnimation.fromName(string2);
                this.plugin.getPlayerSettingsManager().unlockAnimation(player.getUniqueId(), teleportAnimation);
                commandSender.sendMessage(this.plugin.getMessageManager().getPrefix() + "\u00a7aUnlocked \u00a7d" + teleportAnimation.getDisplayName() + " \u00a7aanimation for \u00a7d" + player.getName() + "\u00a7a!");
                player.sendMessage(this.plugin.getMessageManager().getPrefix() + "\u00a7aYou unlocked the \u00a7d" + teleportAnimation.getDisplayName() + " \u00a7ateleport animation!");
            }
            return true;
        }
        if (string.equals("takeanim")) {
            if (stringArray.length < 3) {
                commandSender.sendMessage(this.plugin.getMessageManager().getPrefix() + "\u00a7cUsage: /tpa takeanim <player> <animation|all>");
                return true;
            }
            Player player = Bukkit.getPlayer((String)stringArray[1]);
            if (player == null) {
                commandSender.sendMessage(this.plugin.getMessageManager().getMessage("player-not-found", "%player%", stringArray[1]));
                return true;
            }
            String string3 = stringArray[2].toUpperCase();
            if (string3.equals("ALL")) {
                this.plugin.getPlayerSettingsManager().lockAllAnimations(player.getUniqueId());
                commandSender.sendMessage(this.plugin.getMessageManager().getPrefix() + "\u00a7cRemoved all unlocked animations from \u00a7d" + player.getName() + "\u00a7c.");
            } else {
                TeleportAnimation teleportAnimation = TeleportAnimation.fromName(string3);
                this.plugin.getPlayerSettingsManager().lockAnimation(player.getUniqueId(), teleportAnimation);
                commandSender.sendMessage(this.plugin.getMessageManager().getPrefix() + "\u00a7cRemoved \u00a7d" + teleportAnimation.getDisplayName() + " \u00a7canimation from \u00a7d" + player.getName() + "\u00a7c.");
            }
            return true;
        }
        if (string.equals("setanim")) {
            if (stringArray.length < 3) {
                commandSender.sendMessage(this.plugin.getMessageManager().getPrefix() + "\u00a7cUsage: /tpa setanim <player> <animation>");
                return true;
            }
            Player player = Bukkit.getPlayer((String)stringArray[1]);
            if (player == null) {
                commandSender.sendMessage(this.plugin.getMessageManager().getMessage("player-not-found", "%player%", stringArray[1]));
                return true;
            }
            TeleportAnimation teleportAnimation = TeleportAnimation.fromName(stringArray[2]);
            this.plugin.getPlayerSettingsManager().setEquippedAnimation(player.getUniqueId(), teleportAnimation);
            commandSender.sendMessage(this.plugin.getMessageManager().getPrefix() + "\u00a7aSet \u00a7d" + player.getName() + "\u00a7a's active animation to \u00a7d" + teleportAnimation.getDisplayName() + "\u00a7a!");
            player.sendMessage(this.plugin.getMessageManager().getPrefix() + "\u00a7aYour teleport animation was set to \u00a7d" + teleportAnimation.getDisplayName() + "\u00a7a!");
            return true;
        }
        return false;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (stringArray.length == 1) {
            String string3 = stringArray[0].toLowerCase();
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add("gui");
            arrayList.add("animations");
            if (commandSender.hasPermission("aesthetictpa.admin")) {
                arrayList.add("giveanim");
                arrayList.add("takeanim");
                arrayList.add("setanim");
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().equalsIgnoreCase(commandSender.getName()) || !player.getName().toLowerCase().startsWith(string3)) continue;
                arrayList.add(player.getName());
            }
            return arrayList.stream().filter(string2 -> string2.toLowerCase().startsWith(string3)).collect(Collectors.toList());
        }
        if (stringArray.length == 2 && (stringArray[0].equalsIgnoreCase("giveanim") || stringArray[0].equalsIgnoreCase("takeanim") || stringArray[0].equalsIgnoreCase("setanim"))) {
            String string4 = stringArray[1].toLowerCase();
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(string2 -> string2.toLowerCase().startsWith(string4)).collect(Collectors.toList());
        }
        if (stringArray.length == 3 && (stringArray[0].equalsIgnoreCase("giveanim") || stringArray[0].equalsIgnoreCase("takeanim"))) {
            String string5 = stringArray[2].toLowerCase();
            ArrayList<String> arrayList = new ArrayList<String>(Collections.singletonList("all"));
            for (TeleportAnimation teleportAnimation2 : TeleportAnimation.values()) {
                arrayList.add(teleportAnimation2.name().toLowerCase());
            }
            return arrayList.stream().filter(string2 -> string2.toLowerCase().startsWith(string5)).collect(Collectors.toList());
        }
        if (stringArray.length == 3 && stringArray[0].equalsIgnoreCase("setanim")) {
            String string6 = stringArray[2].toLowerCase();
            return Arrays.stream(TeleportAnimation.values()).map(teleportAnimation -> teleportAnimation.name().toLowerCase()).filter(string2 -> string2.toLowerCase().startsWith(string6)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}

