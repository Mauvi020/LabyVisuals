package net.labymod.addons.labyvisuals.visual;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.labymod.api.Laby;
import net.labymod.api.client.entity.Entity;
import net.labymod.api.client.entity.LivingEntity;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameTickEvent;

/**
 * Tracks damage numbers. Displayed via {@link DamageNumberHudWidget}.
 */
public class DamageNumberTracker {

  private static final long LIFETIME = 1000L;

  private final Map<UUID, DamageNumber> numbers = new ConcurrentHashMap<>();
  private final ConfigProperty<Boolean> enabled;

  public DamageNumberTracker(ConfigProperty<Boolean> enabled) {
    this.enabled = enabled;
  }

  public void showDamage(UUID entityId, float damage, boolean critical) {
    if (!this.enabled.get() || damage <= 0.0F) {
      return;
    }
    DamageNumber previous = this.numbers.get(entityId);
    if (previous != null && !previous.isExpired(LIFETIME)) {
      return; // Show only one number per hit series
    }
    this.numbers.put(entityId, new DamageNumber(entityId, damage, critical));
  }

  @Subscribe
  public void onGameTick(GameTickEvent event) {
    if (this.numbers.isEmpty()) {
      return;
    }
    Iterator<DamageNumber> iterator = this.numbers.values().iterator();
    while (iterator.hasNext()) {
      DamageNumber number = iterator.next();
      if (number.isExpired(LIFETIME)) {
        iterator.remove();
        continue;
      }
      Entity entity = Laby.labyAPI().minecraft().clientWorld().getEntity(number.entityId()).orElse(null);
      if (entity == null) {
        iterator.remove();
      }
    }
  }

  public DamageNumber getNumber(UUID entityId) {
    DamageNumber number = this.numbers.get(entityId);
    if (number == null || number.isExpired(LIFETIME)) {
      return null;
    }
    return number;
  }

  public float getProgress(DamageNumber number) {
    return Math.min(1.0F, (System.currentTimeMillis() - number.spawnTime()) / (float) LIFETIME);
  }

  public boolean enabled() {
    return this.enabled.get();
  }

  public float resolveHealth(UUID entityId) {
    Entity entity = Laby.labyAPI().minecraft().clientWorld().getEntity(entityId).orElse(null);
    if (entity instanceof LivingEntity) {
      return ((LivingEntity) entity).getHealth();
    }
    return -1.0F;
  }

  /**
   * Returns all currently active damage numbers, newest first.
   */
  public java.util.List<DamageNumber> active() {
    java.util.List<DamageNumber> active = new java.util.ArrayList<>(this.numbers.values());
    active.sort((a, b) -> Long.compare(b.spawnTime(), a.spawnTime()));
    return java.util.Collections.unmodifiableList(active);
  }
}
