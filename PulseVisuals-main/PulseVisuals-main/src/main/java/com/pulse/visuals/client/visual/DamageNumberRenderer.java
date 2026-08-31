package com.pulse.visuals.client.visual;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class DamageNumberRenderer {
    private final List<DamageNumber> damageNumbers = new ArrayList<>();

    public void addDamageNumber(Vec3d position, float damage, boolean isCritical) {
        DamageNumber.DamageType type = isCritical ? 
            DamageNumber.DamageType.CRITICAL : 
            DamageNumber.DamageType.NORMAL;
        damageNumbers.add(new DamageNumber(position, damage, type));
    }

    public void addHealNumber(Vec3d position, float amount) {
        damageNumbers.add(new DamageNumber(position, amount, DamageNumber.DamageType.HEAL));
    }

    public void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        // Update and remove dead damage numbers
        damageNumbers.removeIf(num -> !num.isAlive());
        damageNumbers.forEach(DamageNumber::update);

        Camera camera = client.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();

        // Render each damage number
        for (DamageNumber damageNum : damageNumbers) {
            renderDamageNumber(damageNum, client, cameraPos, context.consumers());
        }
    }

    private void renderDamageNumber(DamageNumber damageNum, MinecraftClient client, Vec3d cameraPos, VertexConsumerProvider consumers) {
        Vec3d position = damageNum.getPosition();
        Vec3d screenPos = position.subtract(cameraPos);

        // Calculate screen coordinates
        // This is simplified - in production you'd use proper projection matrix
        float alpha = damageNum.getAlpha();
        int color = damageNum.getColor();
        
        // Extract RGBA
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (alpha * 255);

        // Text would be rendered here in the actual implementation
        // For now, this is a placeholder for the damage number rendering system
    }

    public void clear() {
        damageNumbers.clear();
    }
}
