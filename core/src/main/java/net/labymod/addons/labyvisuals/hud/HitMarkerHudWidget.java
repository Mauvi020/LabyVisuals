package net.labymod.addons.labyvisuals.hud;

import net.labymod.addons.labyvisuals.LabyVisualsAddon;
import net.labymod.addons.labyvisuals.LabyVisualsConfiguration;
import net.labymod.addons.labyvisuals.visual.HitFeedbackTracker;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.hud.hudwidget.HudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.SimpleHudWidget;
import net.labymod.api.client.gui.hud.position.HudSize;
import net.labymod.api.client.gui.screen.ScreenContext;
import net.labymod.api.client.render.draw.RectangleRenderer;
import net.labymod.api.client.render.matrix.Stack;
import net.labymod.api.util.Color;

/**
 * Shooter-style hit marker: a small cross that flashes at the widget position
 * whenever a hit lands. Turns into the kill color when the hit was lethal.
 * Place the widget near the crosshair for the best effect.
 */
public class HitMarkerHudWidget extends SimpleHudWidget<HudWidgetConfig> {

  private static final float SIZE = 48.0F;

  private final LabyVisualsAddon addon;

  public HitMarkerHudWidget(LabyVisualsAddon addon) {
    super("hitmarker", HudWidgetConfig.class);
    this.addon = addon;
    this.bindCategory(addon.widgetCategory());
  }

  @Override
  public boolean isVisibleInGame() {
    return this.addon.isEnabled() && this.addon.config().hitMarker().get();
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
        && (!this.addon.isEnabled() || !configuration.hitMarker().get())) {
      return;
    }

    HitFeedbackTracker tracker = this.addon.hitFeedbackTracker();
    float progress = isEditorContext ? 0.8F : tracker.hitMarkerProgress();
    if (progress <= 0.0F) {
      return;
    }

    boolean kill = !isEditorContext && tracker.isKillMarker();
    Color color = kill
        ? configuration.hitMarkerKillColor().get()
        : configuration.hitMarkerColor().get();
    float scale = configuration.hitMarkerScale().get();

    int alpha = (int) (255.0F * progress);
    int argb = (alpha << 24) | (color.getValue() & 0xFFFFFF);

    float length = 5.0F * scale + (kill ? 2.0F : 0.0F);
    float thickness = 1.5F * scale + (kill ? 0.5F : 0.0F);

    RectangleRenderer rectangles = Laby.references().rectangleRenderer();
    Stack stack = context.stack();

    context.pushStack();
    context.translate(SIZE / 2.0F, SIZE / 2.0F, 0.0F);
    context.rotateRadiansZ((float) (Math.PI / 4.0));
    rectangles.renderRectangle(stack, -length, -thickness / 2.0F, length * 2.0F, thickness, argb);
    context.rotateRadiansZ((float) (Math.PI / 2.0));
    rectangles.renderRectangle(stack, -length, -thickness / 2.0F, length * 2.0F, thickness, argb);
    context.popStack();
  }
}