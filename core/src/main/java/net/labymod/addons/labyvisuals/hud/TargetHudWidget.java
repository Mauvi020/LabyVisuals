package net.labymod.addons.labyvisuals.hud;

import net.labymod.addons.labyvisuals.LabyVisualsAddon;
import net.labymod.addons.labyvisuals.LabyVisualsConfiguration;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.component.serializer.plain.PlainTextComponentSerializer;
import net.labymod.api.client.entity.Entity;
import net.labymod.api.client.entity.LivingEntity;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.world.phys.hit.EntityHitResult;
import net.labymod.api.client.world.phys.hit.HitResult;

/**
 * Target HUD widget: shows the entity the player is currently looking at
 * with a health-based colored name and its remaining health.
 * Rendered via the LabyMod widget editor (category "LabyVisuals").
 */
public class TargetHudWidget extends TextHudWidget<TextHudWidgetConfig> {

  private static final int COLOR_HEALTHY = 0xFF63D66E;
  private static final int COLOR_HURT = 0xFFFFD35E;
  private static final int COLOR_CRITICAL = 0xFFE06C5E;
  private static final int COLOR_TEXT = 0xFFF2F2F2;

  private TextLine nameLine;
  private TextLine healthLine;

  public TargetHudWidget(LabyVisualsAddon addon) {
    super("targethud");
    this.bindCategory(addon.widgetCategory());
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    this.nameLine = this.createLine("Target:", "");
    this.healthLine = this.createLine("\u2764", "");
  }

  @Override
  public void onTick(boolean isEditorContext) {
    String name;
    float health;
    float maxHealth;

        if (isEditorContext) {
      // Preview value inside the widget editor: show a hostile mob example
      name = "Slime";
      health = 7.0F;
      maxHealth = 20.0F;
    } else {
      LivingEntity target = this.resolveTarget();
      if (target == null) {
        this.nameLine.setState(TextLine.State.HIDDEN);
        this.healthLine.setState(TextLine.State.HIDDEN);
        return;
      }
      name = this.entityName(target);
      health = Math.max(0.0F, target.getHealth());
      maxHealth = Math.max(health, target.getMaximalHealth());
    }

    float ratio = maxHealth > 0.0F ? health / maxHealth : 1.0F;
    int nameColor = ratio > 0.7F ? COLOR_HEALTHY : ratio > 0.3F ? COLOR_HURT : COLOR_CRITICAL;

    this.nameLine.updateAndFlush(Component.text(name, TextColor.color(nameColor)));
    this.nameLine.setState(TextLine.State.VISIBLE);
    this.healthLine.updateAndFlush(Component.text(
        String.format("%.1f/%.0f", health, maxHealth), TextColor.color(COLOR_TEXT)));
    this.healthLine.setState(TextLine.State.VISIBLE);
  }

  private LivingEntity resolveTarget() {
    LabyVisualsAddon addon = LabyVisualsAddon.get();
    LabyVisualsConfiguration configuration = addon != null ? addon.config() : null;
    if (configuration == null || !configuration.targetHud().get()) {
      return null;
    }

    ClientPlayer player = Laby.labyAPI().minecraft().getClientPlayer();
    if (player == null) {
      return null;
    }

    HitResult hitResult = Laby.labyAPI().minecraft().getHitResult();
    if (hitResult == null
        || hitResult.type() != HitResult.HitType.ENTITY
        || !(hitResult instanceof EntityHitResult)) {
      return null;
    }

    Entity entity = ((EntityHitResult) hitResult).getEntity();
    if (entity == null || entity.equals(player) || !(entity instanceof LivingEntity)) {
      return null;
    }

    return (LivingEntity) entity;
  }

  private String entityName(Entity entity) {
    Component nameComponent = entity.nameComponent();
    if (nameComponent == null) {
      return "Unknown";
    }
    String name = PlainTextComponentSerializer.plainText().serialize(nameComponent);

    // Simplify technical names like "entity.minecraft.slime" to "slime"
    LabyVisualsConfiguration config = LabyVisualsAddon.get().config();
    if (config != null && config.simplifiedEntityNames().get()) {
      if (name.contains("entity.minecraft.")) {
        int lastDot = name.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < name.length() - 1) {
          name = name.substring(lastDot + 1);
        }
      }
    }
    return name;
  }
}
