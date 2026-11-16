package net.skross04.wildlifeandseasons.ecology.vegetation;

import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;

/**
 * When flowers bloom.
 * <ul>
 *     <li>Temperate and coastal zones: all of spring.</li>
 *     <li>Cold zones: spring comes late, so they bloom from mid-spring into early summer.</li>
 *     <li>Tropical, savanna, desert and seasonless places: flowers stay all year.</li>
 * </ul>
 * Tall flowers (lilac, peony, rose bush, sunflower) bloom longer: spring and summer
 * (cold zones: mid-spring to the end of summer).
 * Early flowers come first: snowdrops in late winter (real ones January–March, often through the last snow),
 * crocuses at the very start of spring (real ones February–April).
 * Outside the bloom time, wild flowers wilt and disappear (and come back next spring in the same spot);
 * flowers a player planted become dormant buds instead.
 */
public final class FlowerRules {
    /** Chance per ground tick that a flower wilts once its bloom time is over (gone within about a day). */
    public static final double WILT_CHANCE = 0.15;
    /** Chance per ground tick that a flower (re)grows or opens during its bloom time. */
    public static final double BLOOM_CHANCE = 0.15;

    private FlowerRules() {
    }

    /** @param yearPosition 0–1 through the year (0 = start of spring) */
    public static boolean isBloomTime(ClimateZone zone, double yearPosition) {
        return switch (zone) {
            case TEMPERATE, OCEAN -> yearPosition < 0.25;
            case COLD -> yearPosition >= 0.10 && yearPosition < 0.35;
            case TROPICAL, SAVANNA, ARID, NO_SEASONS -> true;
        };
    }

    /** Late-winter flowers (snowdrops): the last part of winter and the first days of spring. */
    public static boolean isLateWinterBloomTime(ClimateZone zone, double yearPosition) {
        return switch (zone) {
            case TEMPERATE, OCEAN -> yearPosition >= 0.86 || yearPosition < 0.08;
            case COLD -> yearPosition >= 0.94 || yearPosition < 0.16;
            case TROPICAL, SAVANNA, ARID, NO_SEASONS -> true;
        };
    }

    /** Early-spring flowers (crocuses): the end of winter and the first weeks of spring. */
    public static boolean isEarlySpringBloomTime(ClimateZone zone, double yearPosition) {
        return switch (zone) {
            case TEMPERATE, OCEAN -> yearPosition >= 0.92 || yearPosition < 0.15;
            case COLD -> yearPosition < 0.23;
            case TROPICAL, SAVANNA, ARID, NO_SEASONS -> true;
        };
    }

    /** Tall flowers flower from spring right through summer. */
    public static boolean isTallBloomTime(ClimateZone zone, double yearPosition) {
        return switch (zone) {
            case TEMPERATE, OCEAN -> yearPosition < 0.5;
            case COLD -> yearPosition >= 0.10 && yearPosition < 0.5;
            case TROPICAL, SAVANNA, ARID, NO_SEASONS -> true;
        };
    }
}
