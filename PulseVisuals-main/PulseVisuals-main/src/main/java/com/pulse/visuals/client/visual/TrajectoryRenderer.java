package com.pulse.visuals.client.visual;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class TrajectoryRenderer {
    private final List<Vec3d> trajectoryPoints = new ArrayList<>();
    private ProjectileEntity currentProjectile = null;
    private static final int PREDICTION_STEPS = 30;
    private static final double GRAVITY = 0.03; // Minecraft gravity

    public void update(World world, PlayerEntity player) {
        trajectoryPoints.clear();
        currentProjectile = null;

        // Find nearby projectiles (arrows, etc.)
        List<ProjectileEntity> projectiles = world.getEntitiesByClass(
            ProjectileEntity.class,
            player.getBoundingBox().expand(32),
            entity -> true
        );

        // Track the closest projectile
        if (!projectiles.isEmpty()) {
            double minDistance = Double.MAX_VALUE;
            for (ProjectileEntity projectile : projectiles) {
                double distance = player.distanceTo(projectile);
                if (distance < minDistance) {
                    minDistance = distance;
                    currentProjectile = projectile;
                }
            }

            if (currentProjectile != null) {
                predictTrajectory(currentProjectile);
            }
        }
    }

    private void predictTrajectory(ProjectileEntity projectile) {
        Vec3d pos = projectile.getPos();
        Vec3d velocity = projectile.getVelocity();

        for (int i = 0; i < PREDICTION_STEPS; i++) {
            trajectoryPoints.add(pos);
            // Apply gravity
            velocity = velocity.add(0, -GRAVITY, 0);
            // Apply drag (simplified)
            velocity = velocity.multiply(0.99);
            pos = pos.add(velocity);
        }
    }

    public void render(WorldRenderContext context) {
        if (trajectoryPoints.isEmpty()) {
            return;
        }

        // Rendering would be done here with line rendering
        // This is a placeholder for the trajectory visualization system
    }

    public List<Vec3d> getTrajectoryPoints() {
        return new ArrayList<>(trajectoryPoints);
    }

    public boolean hasTrajectory() {
        return !trajectoryPoints.isEmpty();
    }
}
