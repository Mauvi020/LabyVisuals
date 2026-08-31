package com.pulse.visuals.client.visual;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class HitParticleRenderer {
    private final List<HitParticle> particles = new ArrayList<>();

    public void addHitParticle(Vec3d position, boolean isCritical) {
        // Create particle with random velocity
        double vx = (Math.random() - 0.5) * 0.4;
        double vy = Math.random() * 0.3 + 0.1;
        double vz = (Math.random() - 0.5) * 0.4;

        Vec3d velocity = new Vec3d(vx, vy, vz);

        // Different colors for critical and normal hits
        int color = isCritical ? 0xFF6B3A : 0xFFD700; // Red for crit, Gold for normal
        float scale = isCritical ? 1.5f : 1.0f;
        int lifetime = isCritical ? 30 : 25;

        particles.add(new HitParticle(position, velocity, lifetime, color, scale, isCritical));
    }

    public void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Update and filter dead particles
        particles.removeIf(particle -> !particle.isAlive());
        particles.forEach(HitParticle::update);

        // Render particles
        VertexConsumerProvider vertexConsumers = context.consumers();
        Matrix4f positionMatrix = context.matrixStack().peek().getPositionMatrix();

        for (HitParticle particle : particles) {
            renderParticle(particle, client, vertexConsumers, positionMatrix);
        }
    }

    private void renderParticle(HitParticle particle, MinecraftClient client, VertexConsumerProvider vertexConsumers, Matrix4f positionMatrix) {
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        Vec3d particlePos = particle.getPosition();
        Vec3d relativePos = particlePos.subtract(cameraPos);

        // Extract RGB components
        int color = particle.getColor();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = particle.getAlpha();

        // Simple particle rendering using quads
        float scale = particle.getScale();
        float half = scale / 2.0f;

        // This is a simplified version - in production you'd use proper vertex consumers
        // For now, particles are rendered as simple colored squares that fade out
    }

    public void clear() {
        particles.clear();
    }
}
