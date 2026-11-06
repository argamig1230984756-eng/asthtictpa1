/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.SkullMeta
 */
package com.aesthetic.tpa.util;

import com.aesthetic.tpa.util.ColorUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int n) {
        this.item = new ItemStack(material, n);
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.item = itemStack.clone();
        this.meta = this.item.getItemMeta();
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder skullOf(OfflinePlayer offlinePlayer) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta)itemStack.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(offlinePlayer);
            itemStack.setItemMeta((ItemMeta)skullMeta);
        }
        return new ItemBuilder(itemStack);
    }

    public ItemBuilder name(String string) {
        if (this.meta != null) {
            this.meta.setDisplayName(ColorUtil.colorize(string));
        }
        return this;
    }

    public ItemBuilder lore(String ... stringArray) {
        return this.lore(Arrays.asList(stringArray));
    }

    public ItemBuilder lore(List<String> list) {
        if (this.meta != null && list != null) {
            this.meta.setLore(ColorUtil.colorize(list));
        }
        return this;
    }

    public ItemBuilder addLoreLine(String string) {
        if (this.meta != null) {
            List<String> list = this.meta.hasLore() ? this.meta.getLore() : new ArrayList();
            list.add(ColorUtil.colorize(string));
            this.meta.setLore(list);
        }
        return this;
    }

    public ItemBuilder glow(boolean bl) {
        if (this.meta != null && bl) {
            this.meta.addEnchant(Enchantment.LUCK, 1, true);
            this.meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
        }
        return this;
    }

    public ItemBuilder flags(ItemFlag ... itemFlagArray) {
        if (this.meta != null) {
            this.meta.addItemFlags(itemFlagArray);
        }
        return this;
    }

    public ItemBuilder hideAllFlags() {
        if (this.meta != null) {
            this.meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DYE});
        }
        return this;
    }

    public ItemStack build() {
        if (this.meta != null) {
            this.item.setItemMeta(this.meta);
        }
        return this.item;
    }
}

