package net.labymod.addons.labyvisuals.hud;

import net.labymod.addons.labyvisuals.visual.DamageNumber;
import net.labymod.addons.labyvisuals.visual.DamageNumberTracker;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.util.I18n;

/**
 * Displays the most recent measured damage as a HUD widget line
 * ("Last Hit: <entity> - <damage>"), shown in the LabyVisuals
 * widget editor category.
 */
public class DamageNumberHudWidget extends TextHudWidget<TextHudWidgetConfig> {

  private final DamageNumberTracker tracker;
  private TextLine lastHitLine;

  public DamageNumberHudWidget(DamageNumberTracker tracker) {
    super("damagehud");
    this.tracker = tracker;
    this.bindCategory(net.labymod.addons.labyvisuals.LabyVisualsAddon.get().widgetCategory());
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    this.lastHitLine = this.createLine(
        I18n.translate("labyvisuals.hud.damagehud.lastHit"), "");
  }

  @Override
  public void onTick(boolean isEditorContext) {
    String value = null;
    DamageNumber latest = null;

    if (!isEditorContext && this.tracker.enabled()) {
      java.util.List<DamageNumber> active = this.tracker.active();
      if (!active.isEmpty()) {
        latest = active.get(0);
        float health = this.tracker.resolveHealth(latest.entityId());
        String target = health >= 0.0F
            ? String.format("%.0f \u2764", health / 2.0F)
            : "?";
        value = String.format("%s: %.1f", target, latest.damage());
      }
    } else if (isEditorContext) {
      value = "Steve: 3.5";
    }

    this.lastHitLine.updateAndFlush(value);
    this.lastHitLine.setState(value != null
        ? TextLine.State.VISIBLE : TextLine.State.HIDDEN);
  }
}
