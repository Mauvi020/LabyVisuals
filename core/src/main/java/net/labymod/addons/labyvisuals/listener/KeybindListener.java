package net.labymod.addons.labyvisuals.listener;

import net.labymod.addons.labyvisuals.LabyVisualsAddon;
import net.labymod.addons.labyvisuals.LabyVisualsConfiguration;
import net.labymod.addons.labyvisuals.activity.VisualsActivity;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;

/**
 * Opens the LabyVisuals menu when the configured key bind is pressed.
 *
 * <p>The key bind itself is configured in the LabyMod addon settings
 * (LabyMod Settings -> LabyVisuals -> Menu -> "Visuals Menu").</p>
 */
public class KeybindListener {

  private final LabyVisualsAddon addon;

  public KeybindListener(LabyVisualsAddon addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onKeyEvent(KeyEvent event) {
    if (event.state() != KeyEvent.State.PRESS) {
      return;
    }

    LabyVisualsConfiguration configuration = this.addon.config();
    if (configuration == null) {
      return;
    }

    Key configuredKey = configuration.menuKeybind().get();
    if (configuredKey == null || configuredKey == Key.NONE) {
      return;
    }

    if (!configuredKey.equals(event.key())) {
      return;
    }

    Laby.references().activityController().addOpenActivity(new VisualsActivity());
  }
}
