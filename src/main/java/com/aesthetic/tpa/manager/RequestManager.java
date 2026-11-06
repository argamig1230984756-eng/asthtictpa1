/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package com.aesthetic.tpa.manager;

import com.aesthetic.tpa.AestheticTPA;
import com.aesthetic.tpa.gui.RequestDecisionGui;
import com.aesthetic.tpa.gui.RequestsGui;
import com.aesthetic.tpa.model.RequestType;
import com.aesthetic.tpa.model.TeleportRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RequestManager {
    private final AestheticTPA plugin;
    private final Map<UUID, Map<UUID, TeleportRequest>> incomingRequests = new ConcurrentHashMap<UUID, Map<UUID, TeleportRequest>>();
    private final Map<UUID, Map<UUID, TeleportRequest>> outgoingRequests = new ConcurrentHashMap<UUID, Map<UUID, TeleportRequest>>();
    private BukkitTask cleanupTask;

    public RequestManager(AestheticTPA aestheticTPA) {
        this.plugin = aestheticTPA;
        this.startCleanupTask();
    }

    public void startCleanupTask() {
        if (this.cleanupTask != null) {
            this.cleanupTask.cancel();
        }
        int n = this.plugin.getConfigManager().getCleanupIntervalSeconds() * 20;
        this.cleanupTask = new BukkitRunnable(){

            public void run() {
                long l = System.currentTimeMillis();
                for (Map.Entry<UUID, Map<UUID, TeleportRequest>> entry2 : RequestManager.this.incomingRequests.entrySet()) {
                    UUID uUID = entry2.getKey();
                    Map<UUID, TeleportRequest> map = entry2.getValue();
                    map.entrySet().removeIf(entry -> {
                        TeleportRequest teleportRequest = (TeleportRequest)entry.getValue();
                        if (l >= teleportRequest.getExpiresAt()) {
                            Map<UUID, TeleportRequest> map = RequestManager.this.outgoingRequests.get(teleportRequest.getSender());
                            if (map != null) {
                                map.remove(teleportRequest.getTarget());
                            }
                            Player player = Bukkit.getPlayer((UUID)teleportRequest.getSender());
                            Player player2 = Bukkit.getPlayer((UUID)teleportRequest.getTarget());
                            if (player != null && player.isOnline() && player2 != null) {
                                RequestManager.this.plugin.getMessageManager().send(player, "request-expired-sender", "%player%", player2.getName());
                            }
                            if (player2 != null && player2.isOnline() && player != null) {
                                RequestManager.this.plugin.getMessageManager().send(player2, "request-expired-target", "%player%", player.getName());
                            }
                            return true;
                        }
                        return false;
                    });
                }
            }
        }.runTaskTimer((Plugin)this.plugin, (long)n, (long)n);
    }

    public boolean sendRequest(Player player, Player player2, RequestType requestType) {
        Map map;
        TeleportRequest teleportRequest;
        if (player.getUniqueId().equals(player2.getUniqueId())) {
            this.plugin.getMessageManager().send(player, "cannot-teleport-self", new String[0]);
            return false;
        }
        boolean bl = player.hasPermission("aesthetictpa.bypass.toggle");
        if (!bl) {
            if (requestType == RequestType.TPA && !this.plugin.getPlayerSettingsManager().isTpaEnabled(player2.getUniqueId())) {
                this.plugin.getMessageManager().send(player, "toggle.target-tpa-disabled", "%player%", player2.getName());
                return false;
            }
            if (requestType == RequestType.TPA_HERE && !this.plugin.getPlayerSettingsManager().isTpaHereEnabled(player2.getUniqueId())) {
                this.plugin.getMessageManager().send(player, "toggle.target-tpahere-disabled", "%player%", player2.getName());
                return false;
            }
        }
        if ((teleportRequest = (TeleportRequest)(map = this.incomingRequests.computeIfAbsent(player2.getUniqueId(), uUID -> new ConcurrentHashMap())).get(player.getUniqueId())) != null && !teleportRequest.isExpired()) {
            this.plugin.getMessageManager().send(player, "already-pending", "%player%", player2.getName());
            return false;
        }
        long l = this.plugin.getConfigManager().getTimeoutSeconds();
        TeleportRequest teleportRequest2 = new TeleportRequest(player.getUniqueId(), player2.getUniqueId(), requestType, l);
        map.put(player.getUniqueId(), teleportRequest2);
        this.outgoingRequests.computeIfAbsent(player.getUniqueId(), uUID -> new ConcurrentHashMap()).put(player2.getUniqueId(), teleportRequest2);
        this.plugin.getMessageManager().sendIncomingRequestCard(player2, player, requestType, l);
        this.plugin.getMessageManager().sendSentRequestMessage(player, player2, requestType);
        return true;
    }

    public void openDecisionOrList(Player player, UUID uUID) {
        Map<UUID, TeleportRequest> map = this.incomingRequests.get(player.getUniqueId());
        if (map == null || map.isEmpty()) {
            this.plugin.getMessageManager().send(player, "no-pending-requests", new String[0]);
            return;
        }
        if (uUID != null) {
            TeleportRequest teleportRequest = map.get(uUID);
            if (teleportRequest != null && !teleportRequest.isExpired()) {
                RequestDecisionGui.open(this.plugin, player, teleportRequest);
            } else {
                this.plugin.getMessageManager().send(player, "no-pending-requests", new String[0]);
            }
            return;
        }
        if (map.size() == 1) {
            RequestDecisionGui.open(this.plugin, player, map.values().iterator().next());
        } else {
            RequestsGui.open(this.plugin, player);
        }
    }

    public void executeAccept(Player player, UUID uUID) {
        Player player2;
        Map<UUID, TeleportRequest> map = this.incomingRequests.get(player.getUniqueId());
        if (map == null || map.isEmpty()) {
            this.plugin.getMessageManager().send(player, "no-pending-requests", new String[0]);
            return;
        }
        TeleportRequest teleportRequest = map.remove(uUID);
        if (teleportRequest == null || teleportRequest.isExpired()) {
            this.plugin.getMessageManager().send(player, "no-pending-requests", new String[0]);
            return;
        }
        Map<UUID, TeleportRequest> map2 = this.outgoingRequests.get(teleportRequest.getSender());
        if (map2 != null) {
            map2.remove(teleportRequest.getTarget());
        }
        if ((player2 = Bukkit.getPlayer((UUID)teleportRequest.getSender())) == null || !player2.isOnline()) {
            this.plugin.getMessageManager().send(player, "player-not-found", "%player%", "Sender");
            return;
        }
        this.plugin.getMessageManager().send(player, "request-accepted-target", "%player%", player2.getName());
        this.plugin.getMessageManager().send(player2, "request-accepted-sender", "%player%", player.getName());
        Player player3 = teleportRequest.getType() == RequestType.TPA ? player2 : player;
        Player player4 = teleportRequest.getType() == RequestType.TPA ? player : player2;
        this.plugin.getTeleportWarmupManager().startWarmup(player3, player4, teleportRequest.getType());
    }

    public void executeDeny(Player player, UUID uUID) {
        Player player2;
        Map<UUID, TeleportRequest> map = this.incomingRequests.get(player.getUniqueId());
        if (map == null || map.isEmpty()) {
            this.plugin.getMessageManager().send(player, "no-pending-requests", new String[0]);
            return;
        }
        TeleportRequest teleportRequest = map.remove(uUID);
        if (teleportRequest == null || teleportRequest.isExpired()) {
            this.plugin.getMessageManager().send(player, "no-pending-requests", new String[0]);
            return;
        }
        Map<UUID, TeleportRequest> map2 = this.outgoingRequests.get(teleportRequest.getSender());
        if (map2 != null) {
            map2.remove(teleportRequest.getTarget());
        }
        if ((player2 = Bukkit.getPlayer((UUID)teleportRequest.getSender())) != null && player2.isOnline()) {
            this.plugin.getMessageManager().send(player2, "request-denied-sender", "%player%", player.getName());
            this.plugin.getMessageManager().playSound(player2, this.plugin.getConfigManager().getSoundRequestDenied());
        }
        this.plugin.getMessageManager().send(player, "request-denied-target", "%player%", player2 != null ? player2.getName() : "Player");
        this.plugin.getMessageManager().playSound(player, this.plugin.getConfigManager().getSoundRequestDenied());
    }

    public void cancelRequest(Player player, UUID uUID) {
        Player player2;
        TeleportRequest teleportRequest;
        Map<UUID, TeleportRequest> map = this.outgoingRequests.get(player.getUniqueId());
        if (map == null || map.isEmpty()) {
            this.plugin.getMessageManager().send(player, "no-outgoing-requests", new String[0]);
            return;
        }
        if (uUID == null) {
            if (map.size() == 1) {
                uUID = map.keySet().iterator().next();
            } else {
                RequestsGui.open(this.plugin, player);
                return;
            }
        }
        if ((teleportRequest = map.remove(uUID)) == null || teleportRequest.isExpired()) {
            this.plugin.getMessageManager().send(player, "no-outgoing-requests", new String[0]);
            return;
        }
        Map<UUID, TeleportRequest> map2 = this.incomingRequests.get(teleportRequest.getTarget());
        if (map2 != null) {
            map2.remove(teleportRequest.getSender());
        }
        if ((player2 = Bukkit.getPlayer((UUID)teleportRequest.getTarget())) != null && player2.isOnline()) {
            this.plugin.getMessageManager().send(player2, "request-cancelled-target", "%player%", player.getName());
        }
        this.plugin.getMessageManager().send(player, "request-cancelled-sender", "%player%", player2 != null ? player2.getName() : "Player");
        this.plugin.getMessageManager().playSound(player, this.plugin.getConfigManager().getSoundTeleportCancelled());
    }

    public Collection<TeleportRequest> getIncoming(UUID uUID) {
        Map<UUID, TeleportRequest> map = this.incomingRequests.get(uUID);
        if (map == null) {
            return Collections.emptyList();
        }
        return new ArrayList<TeleportRequest>(map.values());
    }

    public Collection<TeleportRequest> getOutgoing(UUID uUID) {
        Map<UUID, TeleportRequest> map = this.outgoingRequests.get(uUID);
        if (map == null) {
            return Collections.emptyList();
        }
        return new ArrayList<TeleportRequest>(map.values());
    }

    public void clearPlayer(UUID uUID) {
        this.incomingRequests.remove(uUID);
        this.outgoingRequests.remove(uUID);
        for (Map<UUID, TeleportRequest> map : this.incomingRequests.values()) {
            map.remove(uUID);
        }
        for (Map<UUID, TeleportRequest> map : this.outgoingRequests.values()) {
            map.remove(uUID);
        }
    }

    public void cleanup() {
        if (this.cleanupTask != null) {
            this.cleanupTask.cancel();
        }
        this.incomingRequests.clear();
        this.outgoingRequests.clear();
    }
}

