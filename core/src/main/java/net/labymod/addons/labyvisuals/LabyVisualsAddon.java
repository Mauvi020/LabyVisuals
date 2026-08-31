package net.labymod.addons.labyvisuals;

import net.labymod.addons.labyvisuals.hud.ComboCounterHudWidget;
import net.labymod.addons.labyvisuals.hud.DamageNumberHudWidget;
import net.labymod.addons.labyvisuals.hud.DamageStatsHudWidget;
import net.labymod.addons.labyvisuals.hud.HitMarkerHudWidget;
import net.labymod.addons.labyvisuals.hud.InventoryHudWidget;
import net.labymod.addons.labyvisuals.hud.LowHealthVignetteWidget;
import net.labymod.addons.labyvisuals.hud.TargetHudWidget;
import net.labymod.addons.labyvisuals.listener.AttackListener;
import net.labymod.addons.labyvisuals.listener.KeybindListener;
import net.labymod.addons.labyvisuals.visual.DamageNumberTracker;
import net.labymod.addons.labyvisuals.visual.DamageStatsTracker;
import net.labymod.addons.labyvisuals.visual.HitFeedbackTracker;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.models.addon.annotation.AddonMain;

/**
 * Main addon class for LabyVisuals.
 *
 * <p>The class is annotated with {@link AddonMain} and extends {@link LabyAddon},
 * which is required by the LabyMod 4 addon system to recognize and load the addon.</p>
 */
@AddonMain
public class LabyVisualsAddon extends LabyAddon<LabyVisualsConfiguration> implements LabyVisualsApi {

  private static LabyVisualsAddon instance;

  private HudWidgetCategory widgetCategory;
  private DamageNumberTracker damageNumberTracker;
  private HitFeedbackTracker hitFeedbackTracker;
  private DamageStatsTracker damageStatsTracker;

  public LabyVisualsAddon() {
    instance = this;
  }

  public static LabyVisualsAddon get() {
    return instance;
  }

  @Override
  protected void enable() {
    this.registerSettingCategory();

    // HUD widgets (visible in the LabyMod widget editor)
    labyAPI().hudWidgetRegistry().categoryRegistry()
        .register(this.widgetCategory = new HudWidgetCategory("visuals"));
    labyAPI().hudWidgetRegistry().register(new TargetHudWidget(this));
    labyAPI().hudWidgetRegistry()
        .register(new DamageNumberHudWidget(this.damageNumberTracker()));
    labyAPI().hudWidgetRegistry().register(new InventoryHudWidget(this));
    labyAPI().hudWidgetRegistry().register(new HitMarkerHudWidget(this));
    labyAPI().hudWidgetRegistry().register(new ComboCounterHudWidget(this));
    labyAPI().hudWidgetRegistry().register(new DamageStatsHudWidget(this));
    labyAPI().hudWidgetRegistry().register(new LowHealthVignetteWidget(this));

    // Listeners: menu key bind + attack/damage detection
    this.registerListener(new KeybindListener(this));
    this.registerListener(new AttackListener(this.damageNumberTracker(),
        this.hitFeedbackTracker(), this.damageStatsTracker()));

    this.logger().info("LabyVisuals has been enabled!");
  }

  public HudWidgetCategory widgetCategory() {
    return this.widgetCategory;
  }

  public DamageNumberTracker damageNumberTracker() {
    if (this.damageNumberTracker == null) {
      this.damageNumberTracker = new DamageNumberTracker(this.configuration().damageNumbers());
    }
    return this.damageNumberTracker;
  }

  public HitFeedbackTracker hitFeedbackTracker() {
    if (this.hitFeedbackTracker == null) {
      this.hitFeedbackTracker = new HitFeedbackTracker();
    }
    return this.hitFeedbackTracker;
  }

  public DamageStatsTracker damageStatsTracker() {
    if (this.damageStatsTracker == null) {
      this.damageStatsTracker = new DamageStatsTracker();
    }
    return this.damageStatsTracker;
  }

  public LabyVisualsConfiguration config() {
    return this.configuration();
  }

  @Override
  protected Class<? extends LabyVisualsConfiguration> configurationClass() {
    return LabyVisualsConfiguration.class;
  }

  @Override
  public String getVersion() {
    return this.addonInfo() != null ? this.addonInfo().getVersion() : "1.0.0";
  }

  @Override
  public boolean isEnabled() {
    return this.configuration() != null && this.configuration().enabled().get();
  }
}
