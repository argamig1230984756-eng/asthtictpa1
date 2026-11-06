/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package com.aesthetic.tpa.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CustomGuiHolder
implements InventoryHolder {
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> clickHandlers = new HashMap<Integer, Consumer<InventoryClickEvent>>();
    private Consumer<InventoryClickEvent> defaultClickHandler;

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setClickHandler(int n, Consumer<InventoryClickEvent> consumer) {
        this.clickHandlers.put(n, consumer);
    }

    public void setDefaultClickHandler(Consumer<InventoryClickEvent> consumer) {
        this.defaultClickHandler = consumer;
    }

    public void handleClick(InventoryClickEvent inventoryClickEvent) {
        inventoryClickEvent.setCancelled(true);
        Consumer<InventoryClickEvent> consumer = this.clickHandlers.get(inventoryClickEvent.getRawSlot());
        if (consumer != null) {
            consumer.accept(inventoryClickEvent);
        } else if (this.defaultClickHandler != null) {
            this.defaultClickHandler.accept(inventoryClickEvent);
        }
    }
}

