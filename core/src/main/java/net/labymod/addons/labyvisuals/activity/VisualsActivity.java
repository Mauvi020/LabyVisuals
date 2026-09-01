package net.labymod.addons.labyvisuals.activity;

import net.labymod.addons.labyvisuals.LabyVisualsAddon;
import net.labymod.addons.labyvisuals.LabyVisualsConfiguration;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.types.SimpleActivity;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.util.I18n;

/**
 * The LabyVisuals menu, opened with the configurable key bind
 * (LabyMod Settings -> LabyVisuals -> Menu -> "Visuals Menu").
 *
 * <p>Contains quick toggles for every feature; the full settings are
 * located in the LabyMod addon settings.</p>
 */
@AutoActivity
public class VisualsActivity extends SimpleActivity {

  private static final int ENABLED_COLOR = 0xFF63D66E;
  private static final int DISABLED_COLOR = 0xFFE06C5E;

  private VerticalListWidget<ButtonWidget> container;

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    ComponentWidget title = ComponentWidget.text("LabyVisuals");
    title.addId("title");

    ComponentWidget subtitle = ComponentWidget.text(
        I18n.translate("labyvisuals.activity.subtitle"));
    subtitle.addId("subtitle");

    this.container = new VerticalListWidget<>();
    this.container.addId("container");
    this.container.addChild(this.createToggle("targethud"));
    this.container.addChild(this.createToggle("damageNumbers"));
    this.container.addChild(this.createToggle("hitMarker"));
    this.container.addChild(this.createToggle("comboCounter"));
    this.container.addChild(this.createToggle("damageStats"));
    this.container.addChild(this.createToggle("lowHealthVignette"));
    this.container.addChild(this.createToggle("inventoryHud"));
    this.container.addChild(this.createToggle("hitParticles"));
    this.container.addChild(this.createToggle("trajectories"));
    this.container.addChild(this.createCloseButton());

    this.document().addChild(title);
    this.document().addChild(subtitle);
    this.document().addChild(this.container);
  }

  private ButtonWidget createToggle(String key) {
    boolean active = this.isActive(key);
    ButtonWidget button = new ButtonWidget();
    button.updateComponent(this.component(key, active));
    button.setPressable(() -> {
      boolean newState = !this.isActive(key);
      this.setActive(key, newState);
      button.updateComponent(this.component(key, newState));
    });
    button.addId("button");
    return button;
  }

  private ButtonWidget createCloseButton() {
    ButtonWidget button = new ButtonWidget();
    button.updateComponent(Component.text(I18n.translate("labyvisuals.activity.close")));
    button.setPressable(() -> Laby.references().activityController().removeOpenActivity(this));
    button.addId("button");
    return button;
  }

  private Component component(String key, boolean active) {
    String translationKey = "labyvisuals.activity." + key + (active ? ".enabled" : ".disabled");
    return Component.translatable(translationKey,
        TextColor.color(active ? ENABLED_COLOR : DISABLED_COLOR));
  }

  private boolean isActive(String key) {
    LabyVisualsConfiguration configuration = LabyVisualsAddon.get().config();
    switch (key) {
      case "targethud":
        return configuration.targetHud().get();
      case "damageNumbers":
        return configuration.damageNumbers().get();
      case "hitParticles":
        return configuration.hitParticles().get();
      case "trajectories":
        return configuration.trajectories().get();
      case "hitMarker":
        return configuration.hitMarker().get();
      case "comboCounter":
        return configuration.comboCounter().get();
      case "damageStats":
        return configuration.damageStats().get();
      case "lowHealthVignette":
        return configuration.lowHealthVignette().get();
      case "inventoryHud":
        return configuration.inventoryHud().get();
      default:
        return false;
    }
  }

  private void setActive(String key, boolean value) {
    LabyVisualsConfiguration configuration = LabyVisualsAddon.get().config();
    switch (key) {
      case "targethud":
        configuration.targetHud().set(value);
        break;
      case "damageNumbers":
        configuration.damageNumbers().set(value);
        break;
      case "hitParticles":
        configuration.hitParticles().set(value);
        break;
      case "trajectories":
        configuration.trajectories().set(value);
        break;
      case "hitMarker":
        configuration.hitMarker().set(value);
        break;
      case "comboCounter":
        configuration.comboCounter().set(value);
        break;
      case "damageStats":
        configuration.damageStats().set(value);
        break;
      case "lowHealthVignette":
        configuration.lowHealthVignette().set(value);
        break;
      case "inventoryHud":
        configuration.inventoryHud().set(value);
        break;
      default:
        break;
    }
  }
}
