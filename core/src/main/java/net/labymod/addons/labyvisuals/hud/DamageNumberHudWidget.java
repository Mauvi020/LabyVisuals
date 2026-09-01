package net.labymod.addons.labyvisuals.hud;

import net.labymod.addons.labyvisuals.LabyVisualsAddon;
import net.labymod.addons.labyvisuals.visual.DamageNumber;
import net.labymod.addons.labyvisuals.visual.DamageNumberTracker;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.util.I18n;

/**
 * Displays the most recent measured damage as a HUD widget line
 * ("Last Hit: <hearts>: <damage> <heart>"), color coded, shown in the
 * LabyVisuals widget editor category.
 */
public class DamageNumberHudWidget extends TextHudWidget<TextHudWidgetConfig> {

  private static final int COLOR_LABEL = 0xFFA6A6B0;
  private static final int COLOR_DAMAGE = 0xFFE06C5E;

  private final DamageNumberTracker tracker;
  private TextLine lastHitLine;

  public DamageNumberHudWidget(DamageNumberTracker tracker) {
    super("damagehud");
    this.tracker = tracker;
    this.bindCategory(LabyVisualsAddon.get().widgetCategory());
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    this.lastHitLine = this.createLine(
        I18n.translate("labyvisuals.hud.damagehud.lastHit"), "");
  }

  @Override
  public void onTick(boolean isEditorContext) {
    Component value = null;

    if (isEditorContext) {
      value = this.buildValue("Steve", 3.5F);
    } else if (this.tracker.enabled()) {
      java.util.List<DamageNumber> active = this.tracker.active();
      if (!active.isEmpty()) {
        DamageNumber latest = active.get(0);
        float health = this.tracker.resolveHealth(latest.entityId());
        String target = health >= 0.0F
            ? String.format("%.0f", health / 2.0F)
            : "?";
        value = this.buildValue(target, latest.damage());
      }
    }

    this.lastHitLine.updateAndFlush(value);
    this.lastHitLine.setState(value != null
        ? TextLine.State.VISIBLE : TextLine.State.HIDDEN);
  }

  private Component buildValue(String target, float damage) {
    return Component.text(target + ": ", TextColor.color(COLOR_LABEL))
        .append(Component.text(String.format("%.1f \u2764", damage),
            TextColor.color(COLOR_DAMAGE)));
  }
}
