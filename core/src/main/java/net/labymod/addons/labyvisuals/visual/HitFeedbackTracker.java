package net.labymod.addons.labyvisuals.visual;

/**
 * Central combat state used by the hit marker and the combo counter widgets.
 * Fed by the {@code AttackListener} whenever a hit lands or the player takes damage.
 */
public class HitFeedbackTracker {

  private static final long HIT_MARKER_DURATION = 500L;
  private static final long KILL_MARKER_DURATION = 800L;
  private static final long COMBO_FADE_AFTER = 2000L;
  private static final long COMBO_FADE_DURATION = 500L;
  private static final long COMBO_POP_DURATION = 250L;
  private static final long COMBO_RESET_AFTER = 4000L;

  private long lastHit = -1L;
  private long lastKill = -1L;
  private long lastPlayerDamage = -1L;
  private float lastDamage;
  private int combo;

  /**
   * Called when a hit landed and damage was measured.
   *
   * @param damage the measured health delta
   * @param killed whether the target died from this hit
   */
  public void onHit(float damage, boolean killed) {
    long now = System.currentTimeMillis();
    this.lastHit = now;
    this.lastDamage = damage;
    if (killed) {
      this.lastKill = now;
    }
    this.combo++;
  }

  /** Called when the player itself took damage; resets the combo. */
  public void onPlayerDamaged() {
    this.lastPlayerDamage = System.currentTimeMillis();
    this.combo = 0;
  }

  /** Expires the combo after a period without hits. */
  public void tick() {
    if (this.combo > 0 && System.currentTimeMillis() - this.lastHit > COMBO_RESET_AFTER) {
      this.combo = 0;
    }
  }

  /** 1.0 right after a hit, fading to 0.0 over the marker duration. */
  public float hitMarkerProgress() {
    if (this.lastHit < 0L) {
      return 0.0F;
    }
    long duration = this.lastHit == this.lastKill ? KILL_MARKER_DURATION : HIT_MARKER_DURATION;
    float progress = 1.0F - (System.currentTimeMillis() - this.lastHit) / (float) duration;
    return Math.max(0.0F, Math.min(1.0F, progress));
  }

  /** Whether the most recent hit killed the target. */
  public boolean isKillMarker() {
    return this.lastKill > 0L && this.lastKill == this.lastHit;
  }

  /** Whether the player took damage within the given milliseconds. */
  public boolean playerRecentlyDamaged(long withinMillis) {
    return this.lastPlayerDamage > 0L
        && System.currentTimeMillis() - this.lastPlayerDamage < withinMillis;
  }

  public int combo() {
    return this.combo;
  }

  public float lastDamage() {
    return this.lastDamage;
  }

  /** 1.0 while hits keep coming, then fades out. */
  public float comboAlpha() {
    if (this.combo <= 0 || this.lastHit < 0L) {
      return 0.0F;
    }
    float since = System.currentTimeMillis() - this.lastHit;
    if (since <= COMBO_FADE_AFTER) {
      return 1.0F;
    }
    float progress = 1.0F - (since - COMBO_FADE_AFTER) / (float) COMBO_FADE_DURATION;
    return Math.max(0.0F, Math.min(1.0F, progress));
  }

  /** Small pop animation right after a hit lands. */
  public float comboScale() {
    if (this.lastHit < 0L) {
      return 1.0F;
    }
    float since = System.currentTimeMillis() - this.lastHit;
    if (since >= COMBO_POP_DURATION) {
      return 1.0F;
    }
    return 1.0F + 0.6F * (1.0F - since / (float) COMBO_POP_DURATION);
  }
}