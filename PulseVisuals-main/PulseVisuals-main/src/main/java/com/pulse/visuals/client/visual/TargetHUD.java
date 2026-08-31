package com.pulse.visuals.client.visual;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class TargetHUD {
    private LivingEntity targetEntity = null;
    private int targetUpdateTicks = 0;
    private static final int TARGET_UPDATE_INTERVAL = 2; // Update raycast every 2 ticks

    public void updateTarget(PlayerEntity player) {
        targetUpdateTicks++;

        // Update target every few ticks
        if (targetUpdateTicks >= TARGET_UPDATE_INTERVAL) {
            targetUpdateTicks = 0;
            performRaycast(player);
        }
    }

    private void performRaycast(PlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.crosshairTarget == null) {
            targetEntity = null;
            return;
        }

        if (client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            if (client.crosshairTarget instanceof EntityHitResult entityHit) {
                Entity entity = entityHit.getEntity();
                if (entity instanceof LivingEntity && entity != player) {
                    targetEntity = (LivingEntity) entity;
                    return;
                }
            }
        }
        targetEntity = null;
    }

    public LivingEntity getTarget() {
        return targetEntity;
    }

    public boolean hasTarget() {
        return targetEntity != null && targetEntity.isAlive();
    }

    public String getTargetName() {
        if (!hasTarget()) return "";
        return targetEntity.getDisplayName().getString();
    }

    public float getTargetHealth() {
        if (!hasTarget()) return 0;
        return targetEntity.getHealth();
    }

    public float getTargetMaxHealth() {
        if (!hasTarget()) return 0;
        return targetEntity.getMaxHealth();
    }

    public int getTargetArmor() {
        if (!hasTarget()) return 0;
        return targetEntity.getArmor();
    }

    public double getTargetDistance() {
        if (!hasTarget()) return 0;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return 0;
        return client.player.distanceTo(targetEntity);
    }
}
