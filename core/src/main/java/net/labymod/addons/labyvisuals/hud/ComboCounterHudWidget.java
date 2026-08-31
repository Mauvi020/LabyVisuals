package net.labymod.addons.labyvisuals.hud;

import net.labymod.addons.labyvisuals.LabyVisualsAddon;
import net.labymod.addons.labyvisuals.LabyVisualsConfiguration;
import net.labymod.addons.labyvisuals.visual.HitFeedbackTracker;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.gui.hud.hudwidget.HudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.SimpleHudWidget;
import net.labymod.api.client.gui.hud.position.HudSize;
import net.labymod.api.client.gui.screen.ScreenContext;
import net.labymod.api.client.render.RenderPipeline;
import net.labymod.api.client.render.font.ComponentRenderer;
import net.labymod.api.client.render.font.RenderableComponent;
import net.labymod.api.client.render.matrix.Stack;
import net.labymod.api.util.I18n;

/**
 * Combo counter: shows how many hits were landed in a row without taking
 * damage. Pops on every hit, fades out after a moment and resets when the
 * player takes damage.
 */
public class ComboCounterHudWidget extends SimpleHudWidget<HudWidgetConfig> {

  private static final float WIDTH = 120.0F;
  private static final float HEIGHT = 48.0F;

  private static final int COLOR_LOW = 0xFF63D66E;
  private static final int COLOR_MID = 0xFFFFD35E;
  private static final int COLOR_HIGH = 0xFFFF9D42;
  private static final int COLOR_INSANE = 0xFFE06C5E;
  private static final int COLOR_LABEL = 0xFFA6A6B0;

  private final LabyVisualsAddon addon;

  public ComboCounterHudWidget(LabyVisualsAddon addon) {
    super("combocounter", HudWidgetConfig.class);
    this.addon = addon;
    this.bindCategory(addon.widgetCategory());
  }

  @Override
  public boolean isVisibleInGame() {
    return this.addon.isEnabled() && this.addon.config().comboCounter().get();
  }

  @Override
  public void render(RenderPhase renderPhase, ScreenContext context, boolean isEditorContext,
      HudSize hudSize) {
    if (renderPhase == RenderPhase.UPDATE_SIZE) {
      hudSize.set(WIDTH, HEIGHT);
      return;
    }

    LabyVisualsConfiguration configuration = this.addon.config();
    HitFeedbackTracker tracker = this.addon.hitFeedbackTracker();
    int combo = isEditorContext ? 12 : tracker.combo();
    float alpha = isEditorContext ? 1.0F : tracker.comboAlpha();
    if (!isEditorContext && combo <= 0) {
      return;
    }
    if (!isEditorContext
        && (!this.addon.isEnabled() || !configuration.comboCounter().get())) {
      return;
    }
    if (alpha <= 0.0F) {
      return;
    }

    float scale = isEditorContext ? 1.0F : tracker.comboScale();
    Stack stack = context.stack();
    RenderPipeline pipeline = Laby.references().renderPipeline();
    ComponentRenderer renderer = pipeline.componentRenderer();

    Component number = Component.text(String.valueOf(combo),
        TextColor.color(comboColor(combo)));
    float numberWidth = renderer.width(number);

    Component label = Component.translatable("labyvisuals.hud.combocounter.label",
        TextColor.color(COLOR_LABEL));
    float labelWidth = renderer.width(label);

    float numberX = WIDTH / 2.0F - numberWidth * scale / 2.0F;
    float labelX = WIDTH / 2.0F - labelWidth * 0.8F / 2.0F;

    pipeline.setAlpha(alpha, () -> {
      renderer.builder()
          .text(RenderableComponent.of(number))
          .pos(numberX, 4.0F)
          .scale(scale)
          .shadow(true)
          .render(stack);
      renderer.builder()
          .text(RenderableComponent.of(label))
          .pos(labelX, 30.0F)
          .scale(0.8F)
          .shadow(true)
          .render(stack);
    });
  }

  private static int comboColor(int combo) {
    if (combo >= 20) {
      return COLOR_INSANE;
    }
    if (combo >= 10) {
      return COLOR_HIGH;
    }
    if (combo >= 5) {
      return COLOR_MID;
    }
    return COLOR_LOW;
  }
}