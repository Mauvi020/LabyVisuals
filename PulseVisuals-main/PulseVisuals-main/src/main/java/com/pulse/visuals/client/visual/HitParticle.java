package com.pulse.visuals.client.visual;

import net.minecraft.util.math.Vec3d;

public class HitParticle {
    private final Vec3d position;
    private final Vec3d velocity;
    private int lifetime;
    private final int maxLifetime;
    private final int color;
    private final float scale;
    private boolean isCritical;

    public HitParticle(Vec3d position, Vec3d velocity, int maxLifetime, int color, float scale, boolean isCritical) {
        this.position = position;
        this.velocity = velocity;
        this.lifetime = maxLifetime;
        this.maxLifetime = maxLifetime;
        this.color = color;
        this.scale = scale;
        this.isCritical = isCritical;
    }

    public void update() {
        if (lifetime > 0) {
            lifetime--;
        }
    }

    public Vec3d getPosition() {
        return position.add(velocity.multiply(1 - (float) lifetime / maxLifetime));
    }

    public float getAlpha() {
        return (float) lifetime / maxLifetime;
    }

    public int getColor() {
        return color;
    }

    public float getScale() {
        return scale * getAlpha();
    }

    public boolean isAlive() {
        return lifetime > 0;
    }

    public boolean isCritical() {
        return isCritical;
    }
}
