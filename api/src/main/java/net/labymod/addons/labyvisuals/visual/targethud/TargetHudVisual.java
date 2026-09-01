package net.labymod.addons.labyvisuals.visual.targethud;

import net.labymod.api.client.entity.LivingEntity;

/**
 * Tracks the entity the player is currently looking at (crosshair target).
 */
public interface TargetHudVisual {

  LivingEntity target();

  /**
   * @return the currently displayed target hud text lines or null if no target.
   */
  String targetName();
}