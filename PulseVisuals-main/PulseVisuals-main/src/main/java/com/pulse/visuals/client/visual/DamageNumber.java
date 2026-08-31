package com.pulse.visuals.client.visual;

import net.minecraft.util.math.Vec3d;

public class DamageNumber {
    private final Vec3d position;
    private final float damage;
    private int lifetime;
    private final int maxLifetime;
    private final DamageType damageType;

    public enum DamageType {
        NORMAL(0xFFD700),      // Gold
        CRITICAL(0xFF3A3A),    // Red
        HEAL(0x00FF00);        // Green

        public final int color;

        DamageType(int color) {
            this.color = color;
        }
    }

    public DamageNumber(Vec3d position, float damage, DamageType damageType) {
        this.position = position.add(0, 1.5, 0); // Offset above the entity
        this.damage = damage;
        this.damageType = damageType;
        this.maxLifetime = 40; // 2 seconds at 20 ticks/sec
        this.lifetime = maxLifetime;
    }

    public void update() {
        if (lifetime > 0) {
            lifetime--;
        }
    }

    public Vec3d getPosition() {
        // Move upward over time
        float progress = 1.0f - (float) lifetime / maxLifetime;
        return position.add(0, progress * 1.5, 0);
    }

    public float getAlpha() {
        return (float) lifetime / maxLifetime;
    }

    public int getColor() {
        return damageType.color;
    }

    public float getScale() {
        // Larger at the start, smaller as it fades
        return 1.0f + (0.5f * (1.0f - getAlpha()));
    }

    public String getDamageText() {
        return String.format("%.1f", damage);
    }

    public boolean isAlive() {
        return lifetime > 0;
    }

    public DamageType getDamageType() {
        return damageType;
    }
}
