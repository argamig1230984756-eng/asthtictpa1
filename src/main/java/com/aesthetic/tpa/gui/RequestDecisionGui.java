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
import com.aesthetic.tpa.model.RequestType;
import com.aesthetic.tpa.model.TeleportRequest;
import com.aesthetic.tpa.util.ColorUtil;
import com.aesthetic.tpa.util.ItemBuilder;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class RequestDecisionGui {
    public static void open(AestheticTPA aestheticTPA, Player player, TeleportRequest teleportRequest) {
        CustomGuiHolder customGuiHolder = new CustomGuiHolder();
        String string = ColorUtil.colorize(aestheticTPA.getConfig().getString("gui.decision-menu.title", "&#C4B5FDTeleport Request"));
        Inventory inventory = Bukkit.createInventory((InventoryHolder)customGuiHolder, (int)27, (String)string);
        customGuiHolder.setInventory(inventory);
        ItemStack itemStack = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < 27; ++i) {
            inventory.setItem(i, itemStack);
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((UUID)teleportRequest.getSender());
        String string2 = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Player";
        String string3 = teleportRequest.getType() == RequestType.TPA ? "Wants to teleport to you" : "Wants you to teleport to them";
        ItemStack itemStack2 = ItemBuilder.of(Material.LIME_CONCRETE).name("&#4ADE80&lACCEPT REQUEST").lore("&#94A3B8Click or tap to accept teleportation.", "", "&#86EFAC[Tap / Click to Accept]").hideAllFlags().build();
        inventory.setItem(10, itemStack2);
        inventory.setItem(11, itemStack2);
        ItemStack itemStack3 = ItemBuilder.skullOf(offlinePlayer).name("&#C4B5FD&l" + string2).lore("&#94A3B8" + string3, "&#64748BExpires in: &#FDE047" + teleportRequest.getRemainingSeconds() + "s").build();
        inventory.setItem(13, itemStack3);
        ItemStack itemStack4 = ItemBuilder.of(Material.RED_CONCRETE).name("&#F87171&lREJECT REQUEST").lore("&#94A3B8Click or tap to decline this request.", "", "&#FDA4AF[Tap / Click to Reject]").hideAllFlags().build();
        inventory.setItem(15, itemStack4);
        inventory.setItem(16, itemStack4);
        ItemStack itemStack5 = ItemBuilder.of(Material.GRAY_DYE).name("&#64748BClose").lore("&#475569AestheticTPA by Chota_Paglu").hideAllFlags().build();
        inventory.setItem(22, itemStack5);
        customGuiHolder.setClickHandler(10, inventoryClickEvent -> {
            player.closeInventory();
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            aestheticTPA.getRequestManager().executeAccept(player, teleportRequest.getSender());
        });
        customGuiHolder.setClickHandler(11, inventoryClickEvent -> {
            player.closeInventory();
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            aestheticTPA.getRequestManager().executeAccept(player, teleportRequest.getSender());
        });
        customGuiHolder.setClickHandler(15, inventoryClickEvent -> {
            player.closeInventory();
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            aestheticTPA.getRequestManager().executeDeny(player, teleportRequest.getSender());
        });
        customGuiHolder.setClickHandler(16, inventoryClickEvent -> {
            player.closeInventory();
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            aestheticTPA.getRequestManager().executeDeny(player, teleportRequest.getSender());
        });
        customGuiHolder.setClickHandler(22, inventoryClickEvent -> player.closeInventory());
        player.openInventory(inventory);
    }
}

