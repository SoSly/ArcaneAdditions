package org.sosly.arcaneadditions.entities.ai;

public final class Constants {
    private Constants() {
    }

    public static final int TICKS_PER_SECOND = 20;
    public static final float SPELL_FREQUENCY_DIVISOR = 4.0f;

    public static final float WATER_PATH_COST_FOLLOWING = 0.0F;

    public static final int DEFAULT_MIN_ATTRIBUTE_RANGE = 4;

    public static final double BLOCK_CENTER_OFFSET = 0.5;

    public static final double MIN_TELEPORT_DISTANCE_FROM_CASTER = 2.0;

    public static final float STRAFE_BACKWARD_SPEED = -0.5F;
    public static final float STRAFE_FORWARD_SPEED = 0.5F;
    public static final float STRAFE_LEFT_SPEED = -0.5F;
    public static final float STRAFE_RIGHT_SPEED = 0.5F;

    public static final double WANDER_VERTICAL_OFFSET = 0.0D;
    public static final float WANDER_PATH_COST_THRESHOLD = 0.0F;

    public static final float CAST_DISTANCE_SQUARED = 256.0F;

    public static final int MAGIC_LEVEL_DIVISOR = 5;

    public static final long MAINTENANCE_TICK_INTERVAL = 40L;

    public static final int TRANSFUSE_EFFICIENCY_DIVISOR = 5;
}
