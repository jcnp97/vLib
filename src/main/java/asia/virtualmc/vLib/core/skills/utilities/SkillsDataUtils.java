package asia.virtualmc.vLib.core.skills.utilities;

import asia.virtualmc.vLib.utilities.digit.DecimalUtils;
import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import org.jetbrains.annotations.NotNull;

public class SkillsDataUtils {
    private static final int MAX_LEVEL = Integer.MAX_VALUE;
    private static final double MAX_EXP = 9999999999.0;

    /**
     * Updates the player's experience (EXP) based on the update type and given value.
     *
     * @param type       The type of update (ADD, SUBTRACT, SET).
     * @param currentEXP The current EXP value.
     * @param value      The amount to add, subtract, or set.
     * @return The updated EXP value, clamped between 0 and {@code MAX_EXP}.
     */
    public static double getEXP(@NotNull EnumsLib.UpdateType type, double currentEXP, double value) {
        if (value <= 0) return currentEXP;

        value = DecimalUtils.precise(value, 2);
        switch (type) {
            case ADD -> { return Math.min(MAX_EXP, DecimalUtils.format(currentEXP + value)); }
            case SUBTRACT -> { return Math.max(0, DecimalUtils.format((currentEXP - value))); }
            case SET -> { return Math.max(0, Math.min(DecimalUtils.format((value)), MAX_EXP)); }
            default -> { return currentEXP; }
        }
    }

    /**
     * Updates the player's level based on the update type and given value.
     *
     * @param type         The type of update (ADD, SUBTRACT, SET).
     * @param currentLevel The current level.
     * @param value        The amount to add, subtract, or set.
     * @return The updated level, clamped between {@code MIN_LEVEL} and {@code MAX_LEVEL}.
     */
    public static int getLevel(@NotNull EnumsLib.UpdateType type, int currentLevel, int value) {
        if (value <= 0) return currentLevel;

        switch (type) {
            case ADD -> { return Math.min(MAX_LEVEL, currentLevel + value); }
            case SUBTRACT -> { return Math.max(1, currentLevel - value); }
            case SET -> { return Math.max(1, value); }
            default -> { return currentLevel; }
        }
    }

    /**
     * Updates the player's XP multiplier (XPM) based on the update type and given value.
     *
     * @param type        The type of update (ADD, SUBTRACT, SET).
     * @param currentXPM  The current XPM value.
     * @param value       The amount to add, subtract, or set.
     * @return The updated XPM value, never below 0.
     */
    public static double getXPM(@NotNull EnumsLib.UpdateType type, double currentXPM, double value) {
        if (value < 0) return currentXPM;

        value = DecimalUtils.precise(value, 2);
        switch (type) {
            case ADD -> { return currentXPM + value; }
            case SUBTRACT -> { return Math.max(0, currentXPM - value); }
            case SET -> { return Math.max(0, value); }
            default -> { return currentXPM; }
        }
    }

    /**
     * Updates the player's bonus experience (BXP) based on the update type and given value.
     *
     * @param type         The type of update (ADD, SUBTRACT, SET).
     * @param currentBXP   The current BXP value.
     * @param value        The amount to add, subtract, or set.
     * @return The updated BXP value, never below 0.
     */
    public static double getBXP(@NotNull EnumsLib.UpdateType type, double currentBXP, double value) {
        if (value <= 0) return currentBXP;

        value = DecimalUtils.precise(value, 2);
        switch (type) {
            case ADD -> { return Math.min(MAX_EXP, DecimalUtils.format(currentBXP + value)); }
            case SUBTRACT -> { return Math.max(0, DecimalUtils.format(currentBXP - value)); }
            case SET -> { return Math.max(0, Math.min(DecimalUtils.format(value), MAX_EXP)); }
            default -> { return currentBXP; }
        }
    }

    /**
     * Updates the player's trait points based on the update type and given value.
     *
     * @param type      The type of update (ADD, SUBTRACT, SET).
     * @param currentTP The current trait points.
     * @param value     The amount to add, subtract, or set.
     * @return The updated trait points, never below 0.
     */
    public static int getTraitPoints(@NotNull EnumsLib.UpdateType type, int currentTP, int value) {
        if (value <= 0) return currentTP;

        switch (type) {
            case ADD -> { return currentTP + value; }
            case SUBTRACT -> { return Math.max(0, currentTP - value); }
            case SET -> { return value; }
            default -> { return currentTP; }
        }
    }

    /**
     * Updates the player's talent points based on the update type and given value.
     *
     * @param type      The type of update (ADD, SUBTRACT, SET).
     * @param currentTP The current talent points.
     * @param value     The amount to add, subtract, or set.
     * @return The updated talent points, never below 0.
     */
    public static int getTalentPoints(@NotNull EnumsLib.UpdateType type, int currentTP, int value) {
        if (value <= 0) return currentTP;

        switch (type) {
            case ADD -> { return currentTP + value; }
            case SUBTRACT -> { return Math.max(0, currentTP - value); }
            case SET -> { return value; }
            default -> { return currentTP; }
        }
    }

    /**
     * Updates the player's luck value based on the update type and given value.
     *
     * @param type  The type of update (ADD, SUBTRACT, SET).
     * @param luck  The current luck value.
     * @param value The amount to add, subtract, or set.
     * @return The updated luck value, never below 0.
     */
    public static int getLuck(@NotNull EnumsLib.UpdateType type, int luck, int value) {
        if (value <= 0) return luck;

        switch (type) {
            case ADD -> { return luck + value; }
            case SUBTRACT -> { return Math.max(0, luck - value); }
            case SET -> { return value; }
            default -> { return luck; }
        }
    }

    /**
     * Updates the player's trait level based on the update type and value.
     *
     * @param type         The type of update (ADD, SUBTRACT, SET).
     * @param traitLevel  The current trait level.
     * @param value        The amount to add, subtract, or set.
     * @return The updated wisdom level, never below 0.
     */
    public static int getTraitLevel(@NotNull EnumsLib.UpdateType type, int traitLevel, int value) {
        if (value <= 0) return traitLevel;

        switch (type) {
            case ADD -> { return traitLevel + value; }
            case SUBTRACT -> { return Math.max(0, traitLevel - value); }
            case SET -> { return value; }
            default -> { return traitLevel; }
        }
    }

    public static int getMaxLevel() { return MAX_LEVEL; }
    public static double getMaxExp() { return MAX_EXP; }
}
