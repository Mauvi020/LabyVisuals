package net.labymod.addons.labyvisuals.hud;

import net.labymod.addons.labyvisuals.LabyVisualsAddon;
import net.labymod.addons.labyvisuals.LabyVisualsConfiguration;
import net.labymod.api.Laby;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.gui.hud.hudwidget.HudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.SimpleHudWidget;
import net.labymod.api.client.gui.hud.position.HudSize;
import net.labymod.api.client.gui.screen.ScreenContext;
import net.labymod.api.client.render.draw.RectangleRenderer;
import net.labymod.api.client.render.matrix.Stack;
import net.labymod.api.util.Color;

/**
 * Low health vignette: a pulsing colored border around the widget that
 * appears while the player is below a configurable amount of hearts.
 * Resize/scale the widget in the HUD editor to cover the screen edges.
 */
public class LowHealthVignetteWidget extends SimpleHudWidget<HudWidgetConfig> {

  private static final float SIZE = 240.0F;
  private static final int STRIPS = 10;
  private static final float STRIP = 5.0F;

  private final LabyVisualsAddon addon;

  public LowHealthVignetteWidget(LabyVisualsAddon addon) {
    super("lowhealthvignette", HudWidgetConfig.class);
    this.addon = addon;
    this.bindCategory(addon.widgetCategory());
  }

  @Override
  public boolean isVisibleInGame() {
    return this.addon.isEnabled() && this.addon.config().lowHealthVignette().get();
  }

  @Override
  public void render(RenderPhase renderPhase, ScreenContext context, boolean isEditorContext,
      HudSize hudSize) {
    if (renderPhase == RenderPhase.UPDATE_SIZE) {
      hudSize.set(SIZE, SIZE);
      return;
    }

    LabyVisualsConfiguration configuration = this.addon.config();
    if (!isEditorContext
        && (!this.addon.isEnabled() || !configuration.lowHealthVignette().get())) {
      return;
    }

    float intensity;
    if (isEditorContext) {
      intensity = 0.6F;
    } else {
      ClientPlayer player = Laby.labyAPI().minecraft().getClientPlayer();
      if (player == null) {
        return;
      }
      float health = player.getHealth();
      float threshold = configuration.lowHealthThreshold().get() * 2.0F;
      if (health <= 0.0F || health > threshold) {
        return;
      }
      intensity = (threshold - health) / threshold;
    }

    long now = System.currentTimeMillis();
    float pulse = 0.55F + 0.45F * (float) Math.sin(now * 0.008);
    Color base = configuration.lowHealthColor().get();
    int baseAlpha = (int) (base.getAlpha() / 255.0F * 255.0F);

    RectangleRenderer rectangles = Laby.references().rectangleRenderer();
    Stack stack = context.stack();

    for (int i = 0; i < STRIPS; i++) {
      float stripAlpha = intensity * pulse * (1.0F - i / (float) STRIPS);
      int alpha = (int) (255.0F * Math.min(1.0F, stripAlpha) * (baseAlpha / 255.0F));
      if (alpha <= 0) {
        continue;
      }
      int argb = (alpha << 24) | (base.getValue() & 0xFFFFFF);

      float offset = i * STRIP;
      float inner = SIZE - 2.0F * offset;
      // top / bottom
      rectangles.renderRectangle(stack, offset, offset, inner, STRIP, argb);
      rectangles.renderRectangle(stack, offset, SIZE - offset - STRIP, inner, STRIP, argb);
      // left / right (between the horizontal strips)
      float sideHeight = SIZE - 2.0F * (offset + STRIP);
      rectangles.renderRectangle(stack, offset, offset + STRIP, STRIP, sideHeight, argb);
      rectangles.renderRectangle(stack, SIZE - offset - STRIP, offset + STRIP, STRIP, sideHeight,
          argb);
    }
  }
}