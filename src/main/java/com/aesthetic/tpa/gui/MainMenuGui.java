/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package com.aesthetic.tpa.gui;

import com.aesthetic.tpa.AestheticTPA;
import com.aesthetic.tpa.gui.AnimationGui;
import com.aesthetic.tpa.gui.CustomGuiHolder;
import com.aesthetic.tpa.gui.PlayerListGui;
import com.aesthetic.tpa.gui.RequestDecisionGui;
import com.aesthetic.tpa.gui.RequestsGui;
import com.aesthetic.tpa.model.TeleportAnimation;
import com.aesthetic.tpa.model.TeleportRequest;
import com.aesthetic.tpa.util.ColorUtil;
import com.aesthetic.tpa.util.ItemBuilder;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class MainMenuGui {
    public static void open(AestheticTPA aestheticTPA, Player player) {
        CustomGuiHolder customGuiHolder = new CustomGuiHolder();
        String string = aestheticTPA.getConfig().getString("gui.main-menu.title", "&#C4B5FDTeleport Menu");
        int n = aestheticTPA.getConfig().getInt("gui.main-menu.size", 27);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)customGuiHolder, (int)n, (String)ColorUtil.colorize(string));
        customGuiHolder.setInventory(inventory);
        ItemStack itemStack = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < n; ++i) {
            inventory.setItem(i, itemStack);
        }
        Collection<TeleportRequest> collection = aestheticTPA.getRequestManager().getIncoming(player.getUniqueId());
        Collection<TeleportRequest> collection2 = aestheticTPA.getRequestManager().getOutgoing(player.getUniqueId());
        boolean bl = aestheticTPA.getPlayerSettingsManager().isTpaEnabled(player.getUniqueId());
        boolean bl2 = aestheticTPA.getPlayerSettingsManager().isTpaHereEnabled(player.getUniqueId());
        TeleportAnimation teleportAnimation = aestheticTPA.getPlayerSettingsManager().getEquippedAnimation(player.getUniqueId());
        long l = Bukkit.getOnlinePlayers().stream().filter(player2 -> !player2.getUniqueId().equals(player.getUniqueId())).count();
        ItemStack itemStack2 = ItemBuilder.of(Material.COMPASS).name("&#C4B5FD&lOnline Players").lore("&#94A3B8Browse online players to teleport.", "&#94A3B8Online: &#FDE047" + l, "", "&#86EFAC[Tap / Click to Open]").hideAllFlags().build();
        inventory.setItem(10, itemStack2);
        customGuiHolder.setClickHandler(10, inventoryClickEvent -> {
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            PlayerListGui.open(aestheticTPA, player, 0);
        });
        ItemStack itemStack3 = ItemBuilder.of(Material.CLOCK).name("&#C4B5FD&lIncoming Requests").lore("&#94A3B8Pending requests sent to you.", "&#94A3B8Active: &#FDE047" + collection.size(), "", "&#86EFAC[Tap / Click to View]").glow(!collection.isEmpty()).hideAllFlags().build();
        inventory.setItem(12, itemStack3);
        customGuiHolder.setClickHandler(12, inventoryClickEvent -> {
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            if (collection.size() == 1) {
                RequestDecisionGui.open(aestheticTPA, player, (TeleportRequest)collection.iterator().next());
            } else {
                RequestsGui.open(aestheticTPA, player);
            }
        });
        ItemStack itemStack4 = ItemBuilder.of(Material.ENDER_PEARL).name("&#C4B5FD&lOutgoing Requests").lore("&#94A3B8Requests you have sent to others.", "&#94A3B8Active: &#FDE047" + collection2.size(), "", "&#86EFAC[Tap / Click to View]").glow(!collection2.isEmpty()).hideAllFlags().build();
        inventory.setItem(14, itemStack4);
        customGuiHolder.setClickHandler(14, inventoryClickEvent -> {
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            RequestsGui.open(aestheticTPA, player);
        });
        ItemStack itemStack5 = ItemBuilder.of(Material.FIREWORK_ROCKET).name("&#C4B5FD&lTeleport Animations").lore("&#94A3B8Customize your teleport particles & sound.", "&#94A3B8Current: &#4ADE80" + teleportAnimation.getDisplayName(), "", "&#86EFAC[Tap / Click to Customize]").hideAllFlags().build();
        inventory.setItem(16, itemStack5);
        customGuiHolder.setClickHandler(16, inventoryClickEvent -> {
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            AnimationGui.open(aestheticTPA, player);
        });
        Material material = bl ? Material.LIME_DYE : Material.GRAY_DYE;
        String string2 = bl ? "&#4ADE80&lENABLED" : "&#F87171&lDISABLED";
        String string3 = bl ? "&#FDA4AF[Tap to Disable]" : "&#86EFAC[Tap to Enable]";
        ItemStack itemStack6 = ItemBuilder.of(material).name("&#C4B5FDTPA Requests: " + string2).lore("&#94A3B8Allow players to request teleporting to you.", "", string3).hideAllFlags().build();
        inventory.setItem(19, itemStack6);
        customGuiHolder.setClickHandler(19, inventoryClickEvent -> {
            boolean bl = aestheticTPA.getPlayerSettingsManager().toggleTpa(player.getUniqueId());
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundToggleSetting());
            aestheticTPA.getMessageManager().send(player, bl ? "toggle.tpa-enabled" : "toggle.tpa-disabled", new String[0]);
            MainMenuGui.open(aestheticTPA, player);
        });
        ItemStack itemStack7 = ItemBuilder.of(Material.NETHER_STAR).name("&#C4B5FD&lAestheticTPA").lore("&#94A3B8Crafted by &#C4B5FDChota_Paglu", "&#64748BJava & Bedrock Compatible", "", "&#475569Click to close").hideAllFlags().build();
        inventory.setItem(22, itemStack7);
        customGuiHolder.setClickHandler(22, inventoryClickEvent -> player.closeInventory());
        Material material2 = bl2 ? Material.LIME_DYE : Material.GRAY_DYE;
        String string4 = bl2 ? "&#4ADE80&lENABLED" : "&#F87171&lDISABLED";
        String string5 = bl2 ? "&#FDA4AF[Tap to Disable]" : "&#86EFAC[Tap to Enable]";
        ItemStack itemStack8 = ItemBuilder.of(material2).name("&#C4B5FDTPA-HERE Requests: " + string4).lore("&#94A3B8Allow players to request you to teleport to them.", "", string5).hideAllFlags().build();
        inventory.setItem(25, itemStack8);
        customGuiHolder.setClickHandler(25, inventoryClickEvent -> {
            boolean bl = aestheticTPA.getPlayerSettingsManager().toggleTpaHere(player.getUniqueId());
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundToggleSetting());
            aestheticTPA.getMessageManager().send(player, bl ? "toggle.tpahere-enabled" : "toggle.tpahere-disabled", new String[0]);
            MainMenuGui.open(aestheticTPA, player);
        });
        player.openInventory(inventory);
    }
}

