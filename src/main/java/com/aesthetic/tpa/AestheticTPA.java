/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.aesthetic.tpa;

import com.aesthetic.tpa.command.TpaAcceptCommand;
import com.aesthetic.tpa.command.TpaCancelCommand;
import com.aesthetic.tpa.command.TpaCommand;
import com.aesthetic.tpa.command.TpaDenyCommand;
import com.aesthetic.tpa.command.TpaHereCommand;
import com.aesthetic.tpa.command.TpaHereToggleCommand;
import com.aesthetic.tpa.command.TpaMenuCommand;
import com.aesthetic.tpa.command.TpaReloadCommand;
import com.aesthetic.tpa.command.TpaToggleCommand;
import com.aesthetic.tpa.config.ConfigManager;
import com.aesthetic.tpa.config.MessageManager;
import com.aesthetic.tpa.listener.AnimationEntityListener;
import com.aesthetic.tpa.listener.GuiClickListener;
import com.aesthetic.tpa.listener.PlayerDamageListener;
import com.aesthetic.tpa.listener.PlayerMovementListener;
import com.aesthetic.tpa.listener.PlayerQuitListener;
import com.aesthetic.tpa.manager.PlayerSettingsManager;
import com.aesthetic.tpa.manager.RequestManager;
import com.aesthetic.tpa.manager.TeleportWarmupManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AestheticTPA
extends JavaPlugin {
    private static AestheticTPA instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private PlayerSettingsManager playerSettingsManager;
    private TeleportWarmupManager teleportWarmupManager;
    private RequestManager requestManager;

    public void onEnable() {
        instance = this;
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.playerSettingsManager = new PlayerSettingsManager(this);
        this.teleportWarmupManager = new TeleportWarmupManager(this);
        this.requestManager = new RequestManager(this);
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents((Listener)new GuiClickListener(), (Plugin)this);
        pluginManager.registerEvents((Listener)new PlayerMovementListener(this), (Plugin)this);
        pluginManager.registerEvents((Listener)new PlayerDamageListener(this), (Plugin)this);
        pluginManager.registerEvents((Listener)new PlayerQuitListener(this), (Plugin)this);
        pluginManager.registerEvents((Listener)new AnimationEntityListener(), (Plugin)this);
        this.registerCommands();
        this.getLogger().info("================================================");
        this.getLogger().info(" AestheticTPA v" + this.getDescription().getVersion() + " \u2022 Developed by Chota_Paglu");
        this.getLogger().info(" Clean, aesthetic teleportation system enabled.");
        this.getLogger().info("================================================");
    }

    private void registerCommands() {
        TpaCommand tpaCommand = new TpaCommand(this);
        this.bindCommand("tpa", tpaCommand, tpaCommand);
        TpaHereCommand tpaHereCommand = new TpaHereCommand(this);
        this.bindCommand("tpahere", tpaHereCommand, tpaHereCommand);
        TpaAcceptCommand tpaAcceptCommand = new TpaAcceptCommand(this);
        this.bindCommand("tpaccept", tpaAcceptCommand, tpaAcceptCommand);
        TpaDenyCommand tpaDenyCommand = new TpaDenyCommand(this);
        this.bindCommand("tpadeny", tpaDenyCommand, tpaDenyCommand);
        TpaCancelCommand tpaCancelCommand = new TpaCancelCommand(this);
        this.bindCommand("tpacancel", tpaCancelCommand, tpaCancelCommand);
        TpaToggleCommand tpaToggleCommand = new TpaToggleCommand(this);
        this.bindCommand("tpatoggle", tpaToggleCommand, null);
        TpaHereToggleCommand tpaHereToggleCommand = new TpaHereToggleCommand(this);
        this.bindCommand("tpaheretoggle", tpaHereToggleCommand, null);
        TpaMenuCommand tpaMenuCommand = new TpaMenuCommand(this);
        this.bindCommand("tpamenu", tpaMenuCommand, null);
        TpaReloadCommand tpaReloadCommand = new TpaReloadCommand(this);
        this.bindCommand("tpareload", tpaReloadCommand, null);
    }

    private void bindCommand(String string, CommandExecutor commandExecutor, TabCompleter tabCompleter) {
        PluginCommand pluginCommand = this.getCommand(string);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);
            if (tabCompleter != null) {
                pluginCommand.setTabCompleter(tabCompleter);
            }
        } else {
            this.getLogger().warning("Could not register command /" + string + " (missing in plugin.yml)");
        }
    }

    public void onDisable() {
        if (this.teleportWarmupManager != null) {
            this.teleportWarmupManager.cancelAll();
        }
        if (this.requestManager != null) {
            this.requestManager.cleanup();
        }
        if (this.playerSettingsManager != null) {
            this.playerSettingsManager.saveData();
        }
        this.getLogger().info("AestheticTPA has been disabled.");
    }

    public static AestheticTPA getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public MessageManager getMessageManager() {
        return this.messageManager;
    }

    public PlayerSettingsManager getPlayerSettingsManager() {
        return this.playerSettingsManager;
    }

    public TeleportWarmupManager getTeleportWarmupManager() {
        return this.teleportWarmupManager;
    }

    public RequestManager getRequestManager() {
        return this.requestManager;
    }
}

