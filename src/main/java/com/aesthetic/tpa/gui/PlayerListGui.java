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
import com.aesthetic.tpa.gui.MainMenuGui;
import com.aesthetic.tpa.gui.SendRequestChoiceGui;
import com.aesthetic.tpa.util.ColorUtil;
import com.aesthetic.tpa.util.ItemBuilder;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class PlayerListGui {
    private static final int[] SLOTS = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};

    public static void open(AestheticTPA aestheticTPA, Player player, int n) {
        Player player3;
        ItemStack itemStack;
        CustomGuiHolder customGuiHolder = new CustomGuiHolder();
        String string = aestheticTPA.getConfig().getString("gui.players-menu.title", "&#C4B5FDOnline Players");
        int n2 = aestheticTPA.getConfig().getInt("gui.players-menu.size", 36);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)customGuiHolder, (int)n2, (String)ColorUtil.colorize(string));
        customGuiHolder.setInventory(inventory);
        ItemStack itemStack2 = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < n2; ++i) {
            inventory.setItem(i, itemStack2);
        }
        List list = Bukkit.getOnlinePlayers().stream().filter(player2 -> !player2.getUniqueId().equals(player.getUniqueId())).collect(Collectors.toList());
        int n3 = SLOTS.length;
        int n4 = Math.max(1, (int)Math.ceil((double)list.size() / (double)n3));
        int n5 = Math.min(Math.max(0, n), n4 - 1);
        int n6 = n5 * n3;
        int n7 = Math.min(n6 + n3, list.size());
        if (list.isEmpty()) {
            itemStack = ItemBuilder.of(Material.GRAY_DYE).name("&#64748BNo other players online").build();
            inventory.setItem(13, itemStack);
        } else {
            for (int i = n6; i < n7; ++i) {
                player3 = (Player)list.get(i);
                int n8 = SLOTS[i - n6];
                ItemStack itemStack3 = ItemBuilder.skullOf((OfflinePlayer)player3).name("&#C4B5FD&l" + player3.getName()).lore("&#94A3B8Ping: &#E0E7FF" + player3.getPing() + "ms", "", "&#86EFAC[Tap / Click to Send Request]").build();
                inventory.setItem(n8, itemStack3);
                customGuiHolder.setClickHandler(n8, inventoryClickEvent -> {
                    aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
                    SendRequestChoiceGui.open(aestheticTPA, player, player3);
                });
            }
        }
        if (n5 > 0) {
            itemStack = ItemBuilder.of(Material.ARROW).name("&#C4B5FD\u00ab Previous Page").build();
            inventory.setItem(27, itemStack);
            customGuiHolder.setClickHandler(27, inventoryClickEvent -> {
                aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
                PlayerListGui.open(aestheticTPA, player, n5 - 1);
            });
        }
        itemStack = ItemBuilder.of(Material.BARRIER).name("&#F87171\u00ab Back to Menu").build();
        inventory.setItem(31, itemStack);
        customGuiHolder.setClickHandler(31, inventoryClickEvent -> {
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            MainMenuGui.open(aestheticTPA, player);
        });
        if (n5 < n4 - 1) {
            player3 = ItemBuilder.of(Material.ARROW).name("&#C4B5FDNext Page \u00bb").build();
            inventory.setItem(35, (ItemStack)player3);
            customGuiHolder.setClickHandler(35, inventoryClickEvent -> {
                aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
                PlayerListGui.open(aestheticTPA, player, n5 + 1);
            });
        }
        player.openInventory(inventory);
    }
}

