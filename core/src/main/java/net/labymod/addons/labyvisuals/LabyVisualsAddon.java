package net.labymod.addons.labyvisuals;

/**
 * Main addon class for LabyVisuals.
 * This class will be loaded by LabyMod's addon system.
 */
public class LabyVisualsAddon implements LabyVisualsApi {

    private boolean enabled = true;

    public LabyVisualsAddon() {
        System.out.println("LabyVisuals has been initialized!");
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}