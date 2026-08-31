package com.pulse.visuals.mixin;

import com.pulse.visuals.PulseVisualsClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttackEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity livingEntity) {
            // Determine if this is a critical hit
            // In Minecraft, critical hits happen when falling
            boolean isCritical = checkIfCritical(livingEntity);

            // Add hit particle at entity position
            PulseVisualsClient.getHitParticleRenderer()
                .addHitParticle(entity.getPos(), isCritical);

            // Add damage number
            // Note: Actual damage value would need to be intercepted from damage event
            float damage = 5.0f; // Default attack damage
            PulseVisualsClient.getDamageNumberRenderer()
                .addDamageNumber(entity.getPos().add(0, entity.getHeight(), 0), damage, isCritical);
        }
    }

    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        // Add block break particle effect
        Vec3d blockCenter = Vec3d.of(pos).add(0.5, 0.5, 0.5);
        PulseVisualsClient.getHitParticleRenderer()
            .addHitParticle(blockCenter, false);
    }

    private boolean checkIfCritical(LivingEntity entity) {
        // Placeholder for critical hit detection
        // In actual implementation, this would check player fall distance
        return false;
    }
}
