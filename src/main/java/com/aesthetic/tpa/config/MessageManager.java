/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatMessageType
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ClickEvent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 *  net.md_5.bungee.api.chat.ComponentBuilder
 *  net.md_5.bungee.api.chat.HoverEvent
 *  net.md_5.bungee.api.chat.HoverEvent$Action
 *  net.md_5.bungee.api.chat.TextComponent
 *  org.bukkit.Sound
 *  org.bukkit.configuration.Configuration
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 */
package com.aesthetic.tpa.config;

import com.aesthetic.tpa.AestheticTPA;
import com.aesthetic.tpa.model.RequestType;
import com.aesthetic.tpa.util.ColorUtil;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MessageManager {
    private final AestheticTPA plugin;
    private File messagesFile;
    private FileConfiguration messages;

    public MessageManager(AestheticTPA aestheticTPA) {
        this.plugin = aestheticTPA;
        this.loadMessages();
    }

    public void loadMessages() {
        if (this.messagesFile == null) {
            this.messagesFile = new File(this.plugin.getDataFolder(), "messages.yml");
        }
        if (!this.messagesFile.exists()) {
            this.plugin.saveResource("messages.yml", false);
        }
        this.messages = YamlConfiguration.loadConfiguration((File)this.messagesFile);
        InputStream inputStream = this.plugin.getResource("messages.yml");
        if (inputStream != null) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration((Reader)new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            this.messages.setDefaults((Configuration)yamlConfiguration);
        }
    }

    public String getPrefix() {
        return ColorUtil.colorize(this.messages.getString("prefix", "&#C4B5FD\u2726 &#E0E7FFTPA &#64748B\u00bb "));
    }

    public String getMessage(String string) {
        String string2 = this.messages.getString(string);
        if (string2 == null || string2.isEmpty()) {
            return "";
        }
        return ColorUtil.colorize(string2.replace("%prefix%", this.getPrefix()));
    }

    public String getMessage(String string, String ... stringArray) {
        String string2 = this.messages.getString(string);
        if (string2 == null || string2.isEmpty()) {
            return "";
        }
        string2 = string2.replace("%prefix%", this.getPrefix());
        for (int i = 0; i < stringArray.length - 1; i += 2) {
            string2 = string2.replace(stringArray[i], stringArray[i + 1]);
        }
        return ColorUtil.colorize(string2);
    }

    public void send(Player player, String string, String ... stringArray) {
        String string2 = this.getMessage(string, stringArray);
        if (!string2.isEmpty()) {
            player.sendMessage(string2);
        }
    }

    public void playSound(Player player, Sound sound) {
        if (sound != null && this.plugin.getConfigManager().isSoundsEnabled()) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    public void sendActionBar(Player player, String string) {
        BaseComponent[] baseComponentArray = TextComponent.fromLegacyText((String)ColorUtil.colorize(string));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, baseComponentArray);
    }

    public void sendTitle(Player player, String string, String string2, int n, int n2, int n3) {
        player.sendTitle(ColorUtil.colorize(string), ColorUtil.colorize(string2), n, n2, n3);
    }

    public void sendIncomingRequestCard(Player player, Player player2, RequestType requestType, long l) {
        String string = requestType == RequestType.TPA ? "tpa" : "tpahere";
        String string2 = this.getMessage(string + ".received", "%player%", player2.getName());
        String string3 = this.getMessage(string + ".review-btn");
        String string4 = this.getMessage(string + ".review-hover");
        TextComponent textComponent = new TextComponent(this.getPrefix() + string2);
        TextComponent textComponent2 = new TextComponent(ColorUtil.colorize(string3));
        textComponent2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + player2.getName()));
        textComponent2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ColorUtil.colorize(string4)).create()));
        textComponent.addExtra((BaseComponent)textComponent2);
        player.spigot().sendMessage((BaseComponent)textComponent);
        this.playSound(player, this.plugin.getConfigManager().getSoundRequestReceived());
    }

    public void sendSentRequestMessage(Player player, Player player2, RequestType requestType) {
        String string = requestType == RequestType.TPA ? "tpa" : "tpahere";
        String string2 = this.getMessage(string + ".sent", "%player%", player2.getName());
        String string3 = this.getMessage(string + ".cancel-btn");
        String string4 = this.getMessage(string + ".cancel-hover");
        TextComponent textComponent = new TextComponent(this.getPrefix() + string2);
        TextComponent textComponent2 = new TextComponent(ColorUtil.colorize(string3));
        textComponent2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpacancel " + player2.getName()));
        textComponent2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ColorUtil.colorize(string4)).create()));
        textComponent.addExtra((BaseComponent)textComponent2);
        player.spigot().sendMessage((BaseComponent)textComponent);
        this.playSound(player, this.plugin.getConfigManager().getSoundRequestSent());
    }
}

