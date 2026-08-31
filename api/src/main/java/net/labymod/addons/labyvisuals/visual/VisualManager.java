package net.labymod.addons.labyvisuals.visual;

import net.labymod.addons.labyvisuals.visual.damage.DamageNumberVisual;
import net.labymod.addons.labyvisuals.visual.hitparticles.HitParticleVisual;
import net.labymod.addons.labyvisuals.visual.targethud.TargetHudVisual;
import net.labymod.addons.labyvisuals.visual.trajectory.TrajectoryVisual;

/**
 * Central manager holding the state of every visual of this addon.
 */
public interface VisualManager {

  TargetHudVisual targetHud();

  DamageNumberVisual damageNumbers();

  HitParticleVisual hitParticles();

  TrajectoryVisual trajectories();

  /**
   * Called once every client tick to update all visual states.
   */
  void tick();
}