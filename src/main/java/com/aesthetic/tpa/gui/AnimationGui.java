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
import com.aesthetic.tpa.gui.CustomGuiHolder;
import com.aesthetic.tpa.gui.MainMenuGui;
import com.aesthetic.tpa.model.TeleportAnimation;
import com.aesthetic.tpa.util.ColorUtil;
import com.aesthetic.tpa.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class AnimationGui {
    private static final int[] SLOTS = new int[]{11, 12, 13, 14, 15};

    public static void open(AestheticTPA aestheticTPA, Player player) {
        TeleportAnimation teleportAnimation;
        CustomGuiHolder customGuiHolder = new CustomGuiHolder();
        String string = ColorUtil.colorize("&#C4B5FDTeleport Animations");
        int n = 27;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)customGuiHolder, (int)n, (String)string);
        customGuiHolder.setInventory(inventory);
        ItemStack itemStack = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < n; ++i) {
            inventory.setItem(i, itemStack);
        }
        TeleportAnimation teleportAnimation2 = aestheticTPA.getPlayerSettingsManager().getEquippedAnimation(player.getUniqueId());
        TeleportAnimation[] teleportAnimationArray = TeleportAnimation.values();
        for (int i = 0; i < teleportAnimationArray.length && i < SLOTS.length; ++i) {
            Object object;
            String string2;
            teleportAnimation = teleportAnimationArray[i];
            int n2 = SLOTS[i];
            boolean bl = teleportAnimation == teleportAnimation2;
            boolean bl2 = teleportAnimation.hasPermission(player, aestheticTPA);
            if (bl) {
                string2 = "&#4ADE80&l" + teleportAnimation.getDisplayName() + " &#86EFAC[EQUIPPED]";
                object = "&#4ADE80\u2714 Currently Active";
            } else if (bl2) {
                string2 = "&#C4B5FD&l" + teleportAnimation.getDisplayName();
                object = "&#86EFAC[Tap / Click to Equip]";
            } else {
                string2 = "&#64748B" + teleportAnimation.getDisplayName() + " &#F87171[LOCKED]";
                object = "&#F87171Requires: &#64748B" + teleportAnimation.getPermission();
            }
            ItemStack itemStack2 = ItemBuilder.of(teleportAnimation.getIcon()).name(string2).lore(new String[]{"&#94A3B8" + teleportAnimation.getDescription(), "", object}).glow(bl).hideAllFlags().build();
            inventory.setItem(n2, itemStack2);
            customGuiHolder.setClickHandler(n2, inventoryClickEvent -> {
                if (bl) {
                    aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
                    return;
                }
                if (!bl2) {
                    aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundRequestDenied());
                    aestheticTPA.getMessageManager().send(player, "animation-locked", "%permission%", teleportAnimation.getPermission());
                    return;
                }
                aestheticTPA.getPlayerSettingsManager().setEquippedAnimation(player.getUniqueId(), teleportAnimation);
                aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundToggleSetting());
                aestheticTPA.getMessageManager().send(player, "animation-equipped", "%animation%", teleportAnimation.getDisplayName());
                AnimationGui.open(aestheticTPA, player);
            });
        }
        ItemStack itemStack3 = ItemBuilder.of(Material.ARROW).name("&#C4B5FD\u00ab Back to Menu").build();
        inventory.setItem(21, itemStack3);
        customGuiHolder.setClickHandler(21, inventoryClickEvent -> {
            aestheticTPA.getMessageManager().playSound(player, aestheticTPA.getConfigManager().getSoundGuiClick());
            MainMenuGui.open(aestheticTPA, player);
        });
        teleportAnimation = ItemBuilder.of(Material.BARRIER).name("&#F87171Close").build();
        inventory.setItem(23, (ItemStack)teleportAnimation);
        customGuiHolder.setClickHandler(23, inventoryClickEvent -> player.closeInventory());
        player.openInventory(inventory);
    }
}

