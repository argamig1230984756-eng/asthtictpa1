/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 */
package com.aesthetic.tpa.listener;

import com.aesthetic.tpa.gui.CustomGuiHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GuiClickListener
implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getInventory().getHolder() instanceof CustomGuiHolder) {
            CustomGuiHolder customGuiHolder = (CustomGuiHolder)inventoryClickEvent.getInventory().getHolder();
            customGuiHolder.handleClick(inventoryClickEvent);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent inventoryDragEvent) {
        if (inventoryDragEvent.getInventory().getHolder() instanceof CustomGuiHolder) {
            inventoryDragEvent.setCancelled(true);
        }
    }
}

