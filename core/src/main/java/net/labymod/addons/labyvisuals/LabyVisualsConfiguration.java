package net.labymod.addons.labyvisuals;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.widget.widgets.input.KeybindWidget.KeyBindSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget.ColorPickerSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.Color;

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

  @SettingSection("combat")

  @SwitchSetting
  private final ConfigProperty<Boolean> hitMarker = new ConfigProperty<>(true);

  @SliderSetting(steps = 0.1F, min = 0.5F, max = 3.0F)
  private final ConfigProperty<Float> hitMarkerScale = new ConfigProperty<>(1.0F);

  @ColorPickerSetting(alpha = false)
  private final ConfigProperty<Color> hitMarkerColor = new ConfigProperty<>(Color.WHITE);

  @ColorPickerSetting(alpha = false)
  private final ConfigProperty<Color> hitMarkerKillColor = new ConfigProperty<>(Color.RED);

  @SwitchSetting
  private final ConfigProperty<Boolean> comboCounter = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> damageStats = new ConfigProperty<>(true);

  @SliderSetting(steps = 1.0F, min = 3.0F, max = 10.0F)
  private final ConfigProperty<Float> dpsWindow = new ConfigProperty<>(5.0F);

  @SwitchSetting
  private final ConfigProperty<Boolean> statsShowTotal = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> lowHealthVignette = new ConfigProperty<>(true);

  @SliderSetting(steps = 1.0F, min = 1.0F, max = 10.0F)
  private final ConfigProperty<Float> lowHealthThreshold = new ConfigProperty<>(3.0F);

  @ColorPickerSetting
  private final ConfigProperty<Color> lowHealthColor = new ConfigProperty<>(Color.RED);

  @SettingSection("inventory")

  @SwitchSetting
  private final ConfigProperty<Boolean> inventoryHud = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> inventoryShowArmor = new ConfigProperty<>(true);

    @SwitchSetting
  private final ConfigProperty<Boolean> inventoryShowCounts = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> simplifiedEntityNames = new ConfigProperty<>(true);

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

  public ConfigProperty<Boolean> hitMarker() {
    return this.hitMarker;
  }

  public ConfigProperty<Float> hitMarkerScale() {
    return this.hitMarkerScale;
  }

  public ConfigProperty<Color> hitMarkerColor() {
    return this.hitMarkerColor;
  }

  public ConfigProperty<Color> hitMarkerKillColor() {
    return this.hitMarkerKillColor;
  }

  public ConfigProperty<Boolean> comboCounter() {
    return this.comboCounter;
  }

  public ConfigProperty<Boolean> damageStats() {
    return this.damageStats;
  }

  public ConfigProperty<Float> dpsWindow() {
    return this.dpsWindow;
  }

  public ConfigProperty<Boolean> statsShowTotal() {
    return this.statsShowTotal;
  }

  public ConfigProperty<Boolean> lowHealthVignette() {
    return this.lowHealthVignette;
  }

  public ConfigProperty<Float> lowHealthThreshold() {
    return this.lowHealthThreshold;
  }

  public ConfigProperty<Color> lowHealthColor() {
    return this.lowHealthColor;
  }

  public ConfigProperty<Boolean> inventoryHud() {
    return this.inventoryHud;
  }

  public ConfigProperty<Boolean> inventoryShowArmor() {
    return this.inventoryShowArmor;
  }

  public ConfigProperty<Boolean> inventoryShowCounts() {
    return this.inventoryShowCounts;
  }

  public ConfigProperty<Boolean> simplifiedEntityNames() {
    return this.simplifiedEntityNames;
  }

  public ConfigProperty<Key> menuKeybind() {
    return this.menuKeybind;
  }
}
