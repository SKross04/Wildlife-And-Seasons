package net.skross04.wildlifeandseasons.ecology.vegetation;

import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;

/**
 * The year of a sweet berry bush, modelled on the lingonberry (the evergreen dwarf shrub of real taiga forests that
 * Minecraft's sweet berries look like):
 * <ul>
 *     <li>early summer: white-pink bell flowers for 3–4 weeks; last year's berries drop around then,</li>
 *     <li>after flowering green berries form, ripening red in late summer (late August–September),</li>
 *     <li>the berries stay on through autumn and winter (also under the snow),</li>
 *     <li>the leaves turn purple-red in autumn and stay so through winter, then green again with the new growth.</li>
 * </ul>
 * Only in places with real seasons (temperate, ocean and cold zones); elsewhere the bushes behave like vanilla.
 * Year positions: 0 = start of spring … 1 = end of winter (0.29 ≈ mid-June in the temperate climate).
 */
public final class BerryRules {
    /** Half the flowering time, as part of a year. */
    public static final double FLOWERING_HALF_WIDTH = 0.06;
    /** Green berries can form from the end of flowering until the end of autumn. */
    public static final double LAST_BERRIES = 0.78;
    /** Berries ripen this long after the middle of flowering (≈ 2 months: mid-June → late August). */
    public static final double RIPENING_TIME = 0.15;
    /** Chance per random tick that last year's berries drop once the bush flowers again. */
    public static final double OLD_BERRIES_FALL_CHANCE = 0.2;

    private BerryRules() {
    }

    /** True where berry bushes follow the seasons (lingonberries grow in temperate and cold climates). */
    public static boolean hasBerrySeasons(ClimateZone zone) {
        return zone == ClimateZone.TEMPERATE || zone == ClimateZone.OCEAN || zone == ClimateZone.COLD;
    }

    /** The middle of flowering: mid-June, a few weeks later in cold zones (late June–July). */
    public static double floweringCenter(ClimateZone zone) {
        return zone == ClimateZone.COLD ? 0.33 : 0.29;
    }

    /** How much in flower the bushes are, 0–1. */
    public static double flowering(ClimateZone zone, double yearPosition) {
        if (!hasBerrySeasons(zone)) {
            return 0;
        }
        double d = frac(yearPosition) - floweringCenter(zone);
        if (Math.abs(d) >= FLOWERING_HALF_WIDTH) {
            return 0;
        }
        double c = Math.cos(Math.PI / 2 * d / FLOWERING_HALF_WIDTH);
        return c * c;
    }

    /** New (green) berries can form now: after flowering until the end of autumn. */
    public static boolean canFormBerries(ClimateZone zone, double yearPosition) {
        double p = frac(yearPosition);
        return !hasBerrySeasons(zone) || p >= floweringCenter(zone) + FLOWERING_HALF_WIDTH && p <= LAST_BERRIES;
    }

    /** Berries can ripen now: from late summer until the end of autumn. */
    public static boolean canRipen(ClimateZone zone, double yearPosition) {
        double p = frac(yearPosition);
        return !hasBerrySeasons(zone) || p >= floweringCenter(zone) + RIPENING_TIME && p <= LAST_BERRIES;
    }

    /** Last year's berries drop now: from just before flowering to its end, to make room for the new ones. */
    public static boolean oldBerriesFall(ClimateZone zone, double yearPosition) {
        if (!hasBerrySeasons(zone)) {
            return false;
        }
        double p = frac(yearPosition);
        double center = floweringCenter(zone);
        return p >= center - FLOWERING_HALF_WIDTH - 0.02 && p < center + FLOWERING_HALF_WIDTH;
    }

    /**
     * How purple-red the leaves are, 0–1: they colour from mid-autumn, stay so through winter, and green up again
     * with the new growth in late spring (later in cold zones).
     */
    public static double winterLeaves(ClimateZone zone, double yearPosition) {
        if (!hasBerrySeasons(zone)) {
            return 0;
        }
        double p = frac(yearPosition);
        double springShift = zone == ClimateZone.COLD ? 0.05 : 0;
        if (p < 0.5) {
            return 1 - smoothstep(0.14 + springShift, 0.26 + springShift, p);
        }
        return smoothstep(0.58, 0.70, p);
    }

    /**
     * The berry stage (0 sapling, 1 bush, 2 some berries, 3 full) a bush may have now at most, given the stage it
     * grew to: no new berries before flowering is over, no ripe ones before late summer.
     */
    public static int allowedAge(ClimateZone zone, double yearPosition, int age) {
        if (age >= 2 && !canFormBerries(zone, yearPosition)) {
            return Math.max(1, Math.min(age, 1));
        }
        if (age >= 3 && !canRipen(zone, yearPosition)) {
            return 2;
        }
        return age;
    }

    private static double frac(double value) {
        return value - Math.floor(value);
    }

    private static double smoothstep(double from, double to, double value) {
        double t = Math.max(0, Math.min(1, (value - from) / (to - from)));
        return t * t * (3 - 2 * t);
    }
}
