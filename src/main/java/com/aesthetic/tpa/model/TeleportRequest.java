/*
 * Decompiled with CFR 0.152.
 */
package com.aesthetic.tpa.model;

import com.aesthetic.tpa.model.RequestType;
import java.util.UUID;

public class TeleportRequest {
    private final UUID sender;
    private final UUID target;
    private final RequestType type;
    private final long createdAt;
    private final long expiresAt;

    public TeleportRequest(UUID uUID, UUID uUID2, RequestType requestType, long l) {
        this.sender = uUID;
        this.target = uUID2;
        this.type = requestType;
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = this.createdAt + l * 1000L;
    }

    public UUID getSender() {
        return this.sender;
    }

    public UUID getTarget() {
        return this.target;
    }

    public RequestType getType() {
        return this.type;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public long getExpiresAt() {
        return this.expiresAt;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= this.expiresAt;
    }

    public long getRemainingSeconds() {
        long l = (this.expiresAt - System.currentTimeMillis()) / 1000L;
        return Math.max(0L, l);
    }
}

