/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.ComplexEntityPart
 *  org.bukkit.entity.ComplexLivingEntity
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityChangeBlockEvent
 *  org.bukkit.event.entity.EntityCombustEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityExplodeEvent
 *  org.bukkit.event.entity.EntityTargetEvent
 *  org.bukkit.event.entity.EntityTargetLivingEntityEvent
 */
package com.aesthetic.tpa.listener;

import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class AnimationEntityListener
implements Listener {
    private boolean isAnimationEntity(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity.hasMetadata("aesthetictpa_anim")) {
            return true;
        }
        if (entity instanceof ComplexEntityPart) {
            ComplexLivingEntity complexLivingEntity = ((ComplexEntityPart)entity).getParent();
            return complexLivingEntity != null && complexLivingEntity.hasMetadata("aesthetictpa_anim");
        }
        return false;
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onBlockChange(EntityChangeBlockEvent entityChangeBlockEvent) {
        if (this.isAnimationEntity(entityChangeBlockEvent.getEntity())) {
            entityChangeBlockEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onExplode(EntityExplodeEvent entityExplodeEvent) {
        if (this.isAnimationEntity(entityExplodeEvent.getEntity())) {
            entityExplodeEvent.setCancelled(true);
            entityExplodeEvent.blockList().clear();
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onDamage(EntityDamageEvent entityDamageEvent) {
        if (this.isAnimationEntity(entityDamageEvent.getEntity())) {
            entityDamageEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (this.isAnimationEntity(entityDamageByEntityEvent.getDamager())) {
            entityDamageByEntityEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onTarget(EntityTargetEvent entityTargetEvent) {
        if (this.isAnimationEntity(entityTargetEvent.getEntity())) {
            entityTargetEvent.setCancelled(true);
            entityTargetEvent.setTarget(null);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onTargetLiving(EntityTargetLivingEntityEvent entityTargetLivingEntityEvent) {
        if (this.isAnimationEntity(entityTargetLivingEntityEvent.getEntity())) {
            entityTargetLivingEntityEvent.setCancelled(true);
            entityTargetLivingEntityEvent.setTarget(null);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onCombust(EntityCombustEvent entityCombustEvent) {
        if (this.isAnimationEntity(entityCombustEvent.getEntity())) {
            entityCombustEvent.setCancelled(true);
        }
    }
}

