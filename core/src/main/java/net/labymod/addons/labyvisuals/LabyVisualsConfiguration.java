package net.labymod.addons.labyvisuals;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.widget.widgets.input.KeybindWidget.KeyBindSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

/**
 * Configuration for the LabyVisuals addon.
 *
 * <p>Contains the feature toggles and the key bind for the visuals menu.
 * The key bind is configurable in the LabyMod addon settings
 * (LabyMod Settings -> LabyVisuals -> Menu).</p>
 */
@ConfigName("settings")
public class LabyVisualsConfiguration extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @SettingSection("features")

  @SwitchSetting
  private final ConfigProperty<Boolean> targetHud = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> damageNumbers = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> hitParticles = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> trajectories = new ConfigProperty<>(true);

  @SettingSection("menu")

  @KeyBindSetting
  private final ConfigProperty<Key> menuKeybind = new ConfigProperty<>(Key.NONE);

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

  public ConfigProperty<Boolean> targetHud() {
    return this.targetHud;
  }

  public ConfigProperty<Boolean> damageNumbers() {
    return this.damageNumbers;
  }

  public ConfigProperty<Boolean> hitParticles() {
    return this.hitParticles;
  }

  public ConfigProperty<Boolean> trajectories() {
    return this.trajectories;
  }

  public ConfigProperty<Key> menuKeybind() {
    return this.menuKeybind;
  }
}
