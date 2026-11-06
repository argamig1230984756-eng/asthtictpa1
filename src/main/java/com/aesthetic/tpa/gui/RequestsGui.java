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
import com.aesthetic.tpa.gui.RequestDecisionGui;
import com.aesthetic.tpa.model.RequestType;
import com.aesthetic.tpa.model.TeleportRequest;
import com.aesthetic.tpa.util.ColorUtil;
import com.aesthetic.tpa.util.ItemBuilder;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class RequestsGui {
    public static void open(AestheticTPA aestheticTPA, Player player) {
        String string;
        String string2;
        Object object2;
        Object object3;
        CustomGuiHolder customGuiHolder = new CustomGuiHolder();
        String string3 = aestheticTPA.getConfig().getString("gui.requests-menu.title", "&#C4B5FDPending Requests");
        int n = aestheticTPA.getConfig().getInt("gui.requests-menu.size", 27);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)customGuiHolder, (int)n, (String)ColorUtil.colorize(string3));
        customGuiHolder.setInventory(inventory);
        ItemStack itemStack = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < n; ++i) {
            inventory.setItem(i, itemStack);
        }
        Collection<TeleportRequest> collection = aestheticTPA.getRequestManager().getIncoming(player.getUniqueId());
        Collection<TeleportRequest> collection2 = aestheticTPA.getRequestManager().getOutgoing(player.getUniqueId());
        int[] nArray = new int[]{10, 11, 12, 13};
        int n2 = 0;
        if (collection.isEmpty()) {
            object3 = ItemBuilder.of(Material.GRAY_DYE).name("&#64748BNo incoming requests").build();
            inventory.setItem(11, object3);
        } else {
            for (TeleportRequest teleportRequest : collection) {
                if (n2 >= nArray.length) break;
                object2 = Bukkit.getOfflinePlayer((UUID)teleportRequest.getSender());
                String object4 = object2.getName() != null ? object2.getName() : "Player";
                string2 = teleportRequest.getType() == RequestType.TPA ? "Wants to teleport to you" : "Wants you to teleport to them";
                string = ItemBuilder.skullOf((OfflinePlayer)object2).name("&#C4B5FD&l" + object4).lore("&#94A3B8Type: &#E0E7FF" + string2, "&#94A3B8Expires in: &#FDE047" + teleportRequest.getRemainingSeconds() + "s", "", "&#86EFAC[Tap / Click to Review]").build();
                int n3 = nArray[n2++];
                inventory.setItem(n3, (ItemStack)string);
                customGuiHolder.setClickHandler(n3, inventoryClickEvent -> {
                    aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
                    RequestDecisionGui.open(aestheticTPA, player, teleportRequest);
                });
            }
        }
        object3 = new int[]{14, 15, 16};
        int n4 = 0;
        if (collection2.isEmpty()) {
            object2 = ItemBuilder.of(Material.GRAY_DYE).name("&#64748BNo outgoing requests").build();
            inventory.setItem(15, (ItemStack)object2);
        } else {
            for (TeleportRequest teleportRequest : collection2) {
                if (n4 >= ((ItemStack)object3).length) break;
                string2 = Bukkit.getOfflinePlayer((UUID)teleportRequest.getTarget());
                string = string2.getName() != null ? string2.getName() : "Player";
                ItemStack itemStack2 = ItemBuilder.skullOf((OfflinePlayer)string2).name("&#F87171" + string + " &#64748B(Outgoing)").lore("&#94A3B8Expires in: &#FDE047" + teleportRequest.getRemainingSeconds() + "s", "", "&#FDA4AF[Tap / Click to Cancel]").build();
                ItemStack itemStack3 = object3[n4++];
                inventory.setItem((int)itemStack3, itemStack2);
                customGuiHolder.setClickHandler((int)itemStack3, inventoryClickEvent -> {
                    aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
                    aestheticTPA.getRequestManager().cancelRequest(player, teleportRequest.getTarget());
                    RequestsGui.open(aestheticTPA, player);
                });
            }
        }
        object2 = ItemBuilder.of(Material.ARROW).name("&#C4B5FD\u00ab Back to Menu").build();
        inventory.setItem(18, (ItemStack)object2);
        customGuiHolder.setClickHandler(18, inventoryClickEvent -> {
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            MainMenuGui.open(aestheticTPA, player);
        });
        ItemStack itemStack4 = ItemBuilder.of(Material.GRAY_DYE).name("&#64748BAestheticTPA \u2022 Created by &#C4B5FDChota_Paglu").hideAllFlags().build();
        inventory.setItem(22, itemStack4);
        customGuiHolder.setClickHandler(22, inventoryClickEvent -> player.closeInventory());
        player.openInventory(inventory);
    }
}

