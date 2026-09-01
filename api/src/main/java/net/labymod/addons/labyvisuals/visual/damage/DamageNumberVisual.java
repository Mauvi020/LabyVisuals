package net.labymod.addons.labyvisuals.visual.damage;

import net.labymod.api.client.entity.LivingEntity;

/**
 * Tracks damage dealt to entities and displays damage numbers.
 */
public interface DamageNumberVisual {

  /**
   * @return the last measured damage, or -1 if none within the display window.
   */
  double lastDamage();

  LivingEntity damagedEntity();

  /**
   * @return true while a damage number is currently being displayed.
   */
  boolean isDisplaying();
}