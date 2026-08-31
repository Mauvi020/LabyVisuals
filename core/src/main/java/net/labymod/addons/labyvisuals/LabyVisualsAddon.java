package net.labymod.addons.labyvisuals;

import net.labymod.addons.labyvisuals.hud.DamageNumberHudWidget;
import net.labymod.addons.labyvisuals.hud.TargetHudWidget;
import net.labymod.addons.labyvisuals.listener.AttackListener;
import net.labymod.addons.labyvisuals.listener.KeybindListener;
import net.labymod.addons.labyvisuals.visual.DamageNumberTracker;
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
    labyAPI().hudWidgetRegistry().register(new DamageNumberHudWidget(this.damageNumberTracker()));

    // Listeners: menu key bind + attack/damage detection
    this.registerListener(new KeybindListener(this));
    this.registerListener(new AttackListener(this.damageNumberTracker()));

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
