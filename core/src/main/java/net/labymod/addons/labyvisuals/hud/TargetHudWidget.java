package net.labymod.addons.labyvisuals.hud;

import net.labymod.addons.labyvisuals.LabyVisualsAddon;
import net.labymod.addons.labyvisuals.LabyVisualsConfiguration;
import net.labymod.api.Laby;
import net.labymod.api.client.entity.Entity;
import net.labymod.api.client.entity.LivingEntity;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.serializer.plain.PlainTextComponentSerializer;
import net.labymod.api.client.world.phys.hit.EntityHitResult;
import net.labymod.api.client.world.phys.hit.HitResult;

/**
 * Target HUD widget: shows the entity the player is currently looking at,
 * including its remaining health. Rendered via the LabyMod widget editor
 * (category "LabyVisuals").
 */
public class TargetHudWidget extends TextHudWidget<TextHudWidgetConfig> {

  private TextLine targetLine;

  public TargetHudWidget(LabyVisualsAddon addon) {
    super("targethud");
    this.bindCategory(addon.widgetCategory());
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);
    this.targetLine = this.createLine("Target:", "");
  }

  @Override
  public void onTick(boolean isEditorContext) {
    String target;
    if (isEditorContext) {
      // Preview value inside the widget editor
      target = "Steve";
    } else {
      target = this.resolveTarget();
    }

    if (target == null) {
      this.targetLine.setState(TextLine.State.HIDDEN);
      return;
    }

    this.targetLine.updateAndFlush(target);
    this.targetLine.setState(TextLine.State.VISIBLE);
  }

  private String resolveTarget() {
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
    if (entity == null || entity.equals(player)) {
      return null;
    }

    if (entity instanceof LivingEntity) {
      LivingEntity living = (LivingEntity) entity;
      int health = Math.max(0, (int) Math.ceil(living.getHealth()));
      int maxHealth = Math.max(health, (int) Math.ceil(living.getMaximalHealth()));
      return this.entityName(entity) + " " + health + "/" + maxHealth;
    }

    return this.entityName(entity);
  }

  private String entityName(Entity entity) {
    Component nameComponent = entity.nameComponent();
    if (nameComponent == null) {
      return "Unknown";
    }
    return PlainTextComponentSerializer.plainText().serialize(nameComponent);
  }
}
