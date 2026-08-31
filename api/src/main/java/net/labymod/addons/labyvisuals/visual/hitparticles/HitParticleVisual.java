package net.labymod.addons.labyvisuals.visual.hitparticles;

import net.labymod.api.client.entity.LivingEntity;
import net.labymod.api.util.math.vector.FloatVector3;

/**
 * Spawns hit particles at the position an entity was hit.
 */
public interface HitParticleVisual {

  /**
   * Spawns a burst of particles at the given position.
   */
  void spawnBurst(LivingEntity entity, FloatVector3 position);
}