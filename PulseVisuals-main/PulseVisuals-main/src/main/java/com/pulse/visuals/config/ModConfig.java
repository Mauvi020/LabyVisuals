package com.pulse.visuals.config;

public class ModConfig {
    // Hit Particles settings
    public static boolean ENABLE_HIT_PARTICLES = true;
    public static float HIT_PARTICLE_SCALE = 1.0f;
    public static int HIT_PARTICLE_LIFETIME = 25;
    public static int HIT_PARTICLE_CRITICAL_LIFETIME = 30;

    // Target HUD settings
    public static boolean ENABLE_TARGET_HUD = true;
    public static boolean SHOW_TARGET_HEALTH = true;
    public static boolean SHOW_TARGET_ARMOR = true;
    public static boolean SHOW_TARGET_NAME = true;
    public static int TARGET_HUD_X = 10;
    public static int TARGET_HUD_Y = 10;

    // Damage Numbers settings
    public static boolean ENABLE_DAMAGE_NUMBERS = true;
    public static float DAMAGE_NUMBER_SCALE = 1.0f;
    public static int DAMAGE_NUMBER_LIFETIME = 40;
    public static boolean SHOW_CRITICAL_INDICATOR = true;

    // Trajectory settings
    public static boolean ENABLE_TRAJECTORY_PREDICTION = true;
    public static int TRAJECTORY_PREDICTION_STEPS = 30;
    public static int TRAJECTORY_COLOR = 0xFF00FF00; // Green
    public static float TRAJECTORY_LINE_WIDTH = 1.5f;

    // Critical Hit Effect settings
    public static boolean ENABLE_CRITICAL_EFFECT = true;
    public static int CRITICAL_EFFECT_DURATION = 10;
    public static int CRITICAL_EFFECT_COLOR = 0xFFFF3A3A; // Red

    // General settings
    public static boolean ENABLE_MOD = true;
    public static boolean ENABLE_DEBUG_MODE = false;

    public static void reload() {
        // This method would load settings from a config file
        // For now, uses default values
    }
}
