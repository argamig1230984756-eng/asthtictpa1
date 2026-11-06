/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package com.aesthetic.tpa.gui;

import com.aesthetic.tpa.AestheticTPA;
import com.aesthetic.tpa.gui.CustomGuiHolder;
import com.aesthetic.tpa.gui.PlayerListGui;
import com.aesthetic.tpa.model.RequestType;
import com.aesthetic.tpa.util.ColorUtil;
import com.aesthetic.tpa.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SendRequestChoiceGui {
    public static void open(AestheticTPA aestheticTPA, Player player, Player player2) {
        CustomGuiHolder customGuiHolder = new CustomGuiHolder();
        String string = ColorUtil.colorize("&#C4B5FDSend Request \u00bb " + player2.getName());
        Inventory inventory = Bukkit.createInventory((InventoryHolder)customGuiHolder, (int)27, (String)string);
        customGuiHolder.setInventory(inventory);
        ItemStack itemStack = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
        ItemStack itemStack2 = ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < 27; ++i) {
            inventory.setItem(i, itemStack);
        }
        inventory.setItem(10, itemStack2);
        inventory.setItem(12, itemStack2);
        inventory.setItem(14, itemStack2);
        inventory.setItem(16, itemStack2);
        ItemStack itemStack3 = ItemBuilder.skullOf((OfflinePlayer)player2).name("&#C4B5FD&l" + player2.getName()).lore("&#94A3B8World: &#E0E7FF" + player2.getWorld().getName(), "&#94A3B8Ping: &#E0E7FF" + player2.getPing() + "ms").build();
        inventory.setItem(13, itemStack3);
        ItemStack itemStack4 = ItemBuilder.of(Material.ENDER_PEARL).name("&#4ADE80&lTeleport to " + player2.getName()).lore("&#94A3B8Send standard /tpa request.", "&#64748BYou will teleport to their location.", "", "&#86EFAC[Tap / Click to Send]").hideAllFlags().build();
        inventory.setItem(11, itemStack4);
        customGuiHolder.setClickHandler(11, inventoryClickEvent -> {
            player.closeInventory();
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            aestheticTPA.getRequestManager().sendRequest(player, player2, RequestType.TPA);
        });
        ItemStack itemStack5 = ItemBuilder.of(Material.BEACON).name("&#FDE047&lRequest " + player2.getName() + " Here").lore("&#94A3B8Send /tpahere request.", "&#64748BThey will teleport to your location.", "", "&#FDE68A[Tap / Click to Send]").hideAllFlags().build();
        inventory.setItem(15, itemStack5);
        customGuiHolder.setClickHandler(15, inventoryClickEvent -> {
            player.closeInventory();
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            aestheticTPA.getRequestManager().sendRequest(player, player2, RequestType.TPA_HERE);
        });
        ItemStack itemStack6 = ItemBuilder.of(Material.ARROW).name("&#C4B5FD\u00ab Back to Players").build();
        inventory.setItem(21, itemStack6);
        customGuiHolder.setClickHandler(21, inventoryClickEvent -> {
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            PlayerListGui.open(aestheticTPA, player, 0);
        });
        ItemStack itemStack7 = ItemBuilder.of(Material.BARRIER).name("&#F87171Close").build();
        inventory.setItem(23, itemStack7);
        customGuiHolder.setClickHandler(23, inventoryClickEvent -> player.closeInventory());
        player.openInventory(inventory);
    }
}

