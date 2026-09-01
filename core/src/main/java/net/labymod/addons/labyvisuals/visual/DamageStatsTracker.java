package net.labymod.addons.labyvisuals.visual;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Aggregates dealt damage: a rolling DPS value over a configurable window
 * plus session totals. Fed by the {@code AttackListener}.
 */
public class DamageStatsTracker {

  private static final class Hit {

    private final long time;
    private final float damage;

    private Hit(long time, float damage) {
      this.time = time;
      this.damage = damage;
    }
  }

  private final Deque<Hit> window = new ArrayDeque<>();
  private float totalDamage;
  private int totalHits;

  /** Records dealt damage. */
  public void onDamage(float damage) {
    this.window.addLast(new Hit(System.currentTimeMillis(), damage));
    this.totalDamage += damage;
    this.totalHits++;
  }

  /** Damage per second over the given window (in milliseconds). */
  public float dps(long windowMillis) {
    this.prune(windowMillis);
    float sum = 0.0F;
    for (Hit hit : this.window) {
      sum += hit.damage;
    }
    return sum / (windowMillis / 1000.0F);
  }

  private void prune(long windowMillis) {
    long cutoff = System.currentTimeMillis() - windowMillis;
    Iterator<Hit> iterator = this.window.iterator();
    while (iterator.hasNext()) {
      if (iterator.next().time < cutoff) {
        iterator.remove();
      } else {
        break;
      }
    }
  }

  public float totalDamage() {
    return this.totalDamage;
  }

  public int totalHits() {
    return this.totalHits;
  }

  /** Clears the session totals. */
  public void reset() {
    this.window.clear();
    this.totalDamage = 0.0F;
    this.totalHits = 0;
  }
}