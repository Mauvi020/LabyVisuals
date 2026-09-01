package net.labymod.addons.labyvisuals.hud;

import net.labymod.addons.labyvisuals.LabyVisualsAddon;
import net.labymod.addons.labyvisuals.LabyVisualsConfiguration;
import net.labymod.addons.labyvisuals.visual.DamageStatsTracker;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.gui.hud.hudwidget.HudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.SimpleHudWidget;
import net.labymod.api.client.gui.hud.position.HudSize;
import net.labymod.api.client.gui.screen.ScreenContext;
import net.labymod.api.client.render.draw.RectangleRenderer;
import net.labymod.api.client.render.font.ComponentRenderer;
import net.labymod.api.client.render.font.RenderableComponent;
import net.labymod.api.client.render.matrix.Stack;

/**
 * Damage statistics: damage per second over a configurable window with an
 * activity bar, plus optional session totals.
 */
public class DamageStatsHudWidget extends SimpleHudWidget<HudWidgetConfig> {

  private static final float WIDTH = 132.0F;
  private static final int COLOR_LABEL = 0xFFA6A6B0;
  private static final int COLOR_VALUE = 0xFFF2F2F2;
  private static final int COLOR_BAR_BACKGROUND = 0x80000000;
  private static final int COLOR_BAR_FILL = 0xFFE06C5E;

  private final LabyVisualsAddon addon;

  public DamageStatsHudWidget(LabyVisualsAddon addon) {
    super("damagestats", HudWidgetConfig.class);
    this.addon = addon;
    this.bindCategory(addon.widgetCategory());
  }

  @Override
  public boolean isVisibleInGame() {
    return this.addon.isEnabled() && this.addon.config().damageStats().get();
  }

  @Override
  public void render(RenderPhase renderPhase, ScreenContext context, boolean isEditorContext,
      HudSize hudSize) {
    LabyVisualsConfiguration configuration = this.addon.config();
    boolean showTotal = configuration.statsShowTotal().get();

    if (renderPhase == RenderPhase.UPDATE_SIZE) {
      hudSize.set(WIDTH, showTotal ? 40.0F : 26.0F);
      return;
    }

    if (!isEditorContext
        && (!this.addon.isEnabled() || !configuration.damageStats().get())) {
      return;
    }

    DamageStatsTracker tracker = this.addon.damageStatsTracker();
    long window = (long) (configuration.dpsWindow().get() * 1000.0F);
    float dps = isEditorContext ? 7.3F : tracker.dps(window);
    float total = isEditorContext ? 214.6F : tracker.totalDamage();

    Stack stack = context.stack();
    ComponentRenderer renderer = Laby.references().renderPipeline().componentRenderer();
    RectangleRenderer rectangles = Laby.references().rectangleRenderer();

    Component dpsLine = Component.text("DPS ", TextColor.color(COLOR_LABEL))
        .append(Component.text(String.format("%.1f", dps), TextColor.color(COLOR_VALUE)));
    renderer.builder()
        .text(RenderableComponent.of(dpsLine))
        .pos(6.0F, 5.0F)
        .shadow(true)
        .render(stack);

    float fill = Math.min(1.0F, dps / 10.0F);
    float barWidth = WIDTH - 12.0F;
    rectangles.renderRectangle(stack, 6.0F, 17.0F, barWidth, 3.0F, COLOR_BAR_BACKGROUND);
    rectangles.renderRectangle(stack, 6.0F, 17.0F, barWidth * fill, 3.0F, COLOR_BAR_FILL);

    if (showTotal) {
      Component totalLine = Component.text("Total ", TextColor.color(COLOR_LABEL))
          .append(Component.text(String.format("%.1f", total), TextColor.color(COLOR_VALUE)));
      renderer.builder()
          .text(RenderableComponent.of(totalLine))
          .pos(6.0F, 26.0F)
          .shadow(true)
          .render(stack);
    }
  }
}