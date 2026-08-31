package net.labymod.addons.labyvisuals.visual;

import java.util.UUID;

/**
 * A single floating damage number above an entity.
 */
public class DamageNumber {

  private final UUID entityId;
  private final float damage;
  private final boolean critical;
  private final long spawnTime;
  private boolean removed;

  public DamageNumber(UUID entityId, float damage, boolean critical) {
    this.entityId = entityId;
    this.damage = damage;
    this.critical = critical;
    this.spawnTime = System.currentTimeMillis();
  }

  public UUID entityId() {
    return this.entityId;
  }

  public float damage() {
    return this.damage;
  }

  public boolean critical() {
    return this.critical;
  }

  public long spawnTime() {
    return this.spawnTime;
  }

  public boolean isExpired(long lifetime) {
    return this.removed || System.currentTimeMillis() - this.spawnTime > lifetime;
  }

  public void remove() {
    this.removed = true;
  }
}
