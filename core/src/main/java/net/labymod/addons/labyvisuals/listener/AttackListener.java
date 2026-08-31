package net.labymod.addons.labyvisuals.listener;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.labymod.addons.labyvisuals.visual.DamageNumberTracker;
import net.labymod.api.Laby;
import net.labymod.api.client.entity.Entity;
import net.labymod.api.client.entity.LivingEntity;
import net.labymod.api.client.world.phys.hit.EntityHitResult;
import net.labymod.api.client.world.phys.hit.HitResult;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.input.MouseButtonEvent;
import net.labymod.api.event.client.lifecycle.GameTickEvent;

/**
 * Detects attacks (left click / attack key while aiming at a living entity)
 * and measures the dealt damage as health delta over the next ticks.
 * The measured damage is forwarded to the {@link DamageNumberTracker}.
 */
public class AttackListener {

  private static final int MAX_WAIT_TICKS = 20;

  private final DamageNumberTracker damageNumbers;
  private final Map<UUID, ArmedAttack> armed = new ConcurrentHashMap<>();

  public AttackListener(DamageNumberTracker damageNumbers) {
    this.damageNumbers = damageNumbers;
  }

  @Subscribe
  public void onKeyEvent(KeyEvent event) {
    if (event.state() != KeyEvent.State.PRESS || event.key() != KeyMappings.ATTACK_KEY) {
      return;
    }
    this.armTarget();
  }

  @Subscribe
  public void onMouseButton(MouseButtonEvent event) {
    if (event.action() != MouseButtonEvent.Action.CLICK || !event.button().isLeft()) {
      return;
    }
    this.armTarget();
  }

  @Subscribe
  public void onGameTick(GameTickEvent event) {
    if (this.armed.isEmpty()) {
      return;
    }
    Iterator<Map.Entry<UUID, ArmedAttack>> iterator = this.armed.entrySet().iterator();
    while (iterator.hasNext()) {
      ArmedAttack attack = iterator.next().getValue();
      attack.ticks++;

      Entity entity = Laby.labyAPI().minecraft().clientWorld().getEntity(attack.entityId)
          .orElse(null);
      if (entity instanceof LivingEntity) {
        float health = ((LivingEntity) entity).getHealth();
        float delta = attack.healthBefore - health;
        if (delta > 0.01F) {
          this.damageNumbers.showDamage(attack.entityId, delta, false);
          iterator.remove();
          continue;
        }
      }

      if (attack.ticks >= MAX_WAIT_TICKS) {
        iterator.remove();
      }
    }
  }

  private void armTarget() {
    HitResult hitResult = Laby.labyAPI().minecraft().getHitResult();
    if (!(hitResult instanceof EntityHitResult)) {
      return;
    }
    Entity entity = ((EntityHitResult) hitResult).getEntity();
    if (!(entity instanceof LivingEntity)) {
      return;
    }
    LivingEntity living = (LivingEntity) entity;
    this.armed.put(entity.getUniqueId(),
        new ArmedAttack(entity.getUniqueId(), living.getHealth()));
  }

  private static class ArmedAttack {

    private final UUID entityId;
    private final float healthBefore;
    private int ticks;

    private ArmedAttack(UUID entityId, float healthBefore) {
      this.entityId = entityId;
      this.healthBefore = healthBefore;
    }
  }
}

