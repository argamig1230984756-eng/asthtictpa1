/*
 * Decompiled with CFR 0.152.
 */
package com.aesthetic.tpa.model;

public enum RequestType {
    TPA("Teleport to Target"),
    TPA_HERE("Teleport Target to You");

    private final String description;

    private RequestType(String string2) {
        this.description = string2;
    }

    public String getDescription() {
        return this.description;
    }
}

