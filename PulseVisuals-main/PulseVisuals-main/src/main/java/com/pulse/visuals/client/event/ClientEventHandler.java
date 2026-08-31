package com.pulse.visuals.client.event;

import com.pulse.visuals.PulseVisualsClient;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;

public class ClientEventHandler {
    private final MinecraftClient client;

    public ClientEventHandler() {
        this.client = MinecraftClient.getInstance();
    }

    public void onClientTick(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return;
        }

        // Update target HUD every tick
        PulseVisualsClient.getTargetHUD().updateTarget(client.player);

        // Update trajectory prediction for projectiles
        PulseVisualsClient.getTrajectoryRenderer().update(client.world, client.player);
    }

    public void onWorldRenderEnd(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) {
            return;
        }

        // Render all visual effects
        PulseVisualsClient.getHitParticleRenderer().render(context);
        PulseVisualsClient.getDamageNumberRenderer().render(context);
        PulseVisualsClient.getTrajectoryRenderer().render(context);
    }
}
