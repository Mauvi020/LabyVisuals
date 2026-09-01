package net.labymod.addons.labyvisuals;

/**
 * API interface for LabyVisuals addon.
 * This interface can be used by other addons to interact with LabyVisuals.
 */
public interface LabyVisualsApi {
    /**
     * Gets the current version of LabyVisuals.
     * @return the version string
     */
    String getVersion();
    
    /**
     * Checks if LabyVisuals is enabled.
     * @return true if enabled, false otherwise
     */
    boolean isEnabled();
}