/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.scheduler.BukkitTask
 */
package com.aesthetic.tpa.model;

import com.aesthetic.tpa.model.RequestType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

public class WarmupTask {
    private final UUID playerToTeleport;
    private final UUID destinationPlayer;
    private final Location initialLocation;
    private final RequestType type;
    private int remainingSeconds;
    private BukkitTask task;
    private final List<Entity> spawnedEntities = new ArrayList<Entity>();

    public WarmupTask(UUID uUID, UUID uUID2, Location location, RequestType requestType, int n) {
        this.playerToTeleport = uUID;
        this.destinationPlayer = uUID2;
        this.initialLocation = location.clone();
        this.type = requestType;
        this.remainingSeconds = n;
    }

    public UUID getPlayerToTeleport() {
        return this.playerToTeleport;
    }

    public UUID getDestinationPlayer() {
        return this.destinationPlayer;
    }

    public Location getInitialLocation() {
        return this.initialLocation;
    }

    public RequestType getType() {
        return this.type;
    }

    public int getRemainingSeconds() {
        return this.remainingSeconds;
    }

    public void decrementRemaining() {
        --this.remainingSeconds;
    }

    public BukkitTask getTask() {
        return this.task;
    }

    public void setTask(BukkitTask bukkitTask) {
        this.task = bukkitTask;
    }

    public List<Entity> getSpawnedEntities() {
        return this.spawnedEntities;
    }

    public void addSpawnedEntity(Entity entity) {
        if (entity != null) {
            this.spawnedEntities.add(entity);
        }
    }

    public void cleanupEntities() {
        for (Entity entity : this.spawnedEntities) {
            if (entity == null || !entity.isValid()) continue;
            entity.remove();
        }
        this.spawnedEntities.clear();
    }

    public void cancel() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        this.cleanupEntities();
    }
}

