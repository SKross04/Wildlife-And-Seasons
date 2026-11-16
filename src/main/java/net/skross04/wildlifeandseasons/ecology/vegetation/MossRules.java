package net.skross04.wildlifeandseasons.ecology.vegetation;

import net.skross04.wildlifeandseasons.ecology.climate.ClimateZone;

/**
 * How moss and lichen slowly take over stone. Real basis: bare rock gets covered by algae, then lichen, then moss,
 * over about 20 years in Norway; lichen and moss take their water from above (rain), go dormant when dry and revive
 * within hours when wet; the shady north side of a wall grows different (mossier) communities than the sunny south
 * side; some lichens grow less than half a millimetre a year. Minecraft years are short (100 days), so the game
 * squeezes this into a few years: a shady, damp old wall turns mossy over one to three years.
 */
public final class MossRules {
    /** Which side of a block the moss or lichen is on (the side facing the air). */
    public enum Face { UP, DOWN, NORTH, SOUTH, EAST, WEST }

    /**
     * Chance per visit (about 24 visits per block per year at the default randomTickSpeed) that an old
     * cobblestone or stone brick block turns mossy, in the best conditions.
     */
    public static final double MOSS_CHANCE = 0.005;

    /** Same for a lichen patch on an open natural rock face. */
    public static final double LICHEN_CHANCE = 0.004;

    /** Same for a moss carpet on top of a rock in the shade of trees. */
    public static final double MOSS_CARPET_CHANCE = 0.004;

    /** Each mossy (or lichen-covered) neighbour makes it spread faster: moss and lichen grow on from fragments. */
    public static final double NEIGHBOUR_BONUS = 0.75;
    public static final double MAX_NEIGHBOUR_FACTOR = 3;

    /** Share of rock patches lichen can colonise at all (rock varies; not every boulder gets lichen). */
    public static final double LICHEN_PATCH_SHARE = 0.6;

    private MossRules() {
    }

    /** How damp the climate is for moss: wet oceanic and tropical climates most, deserts hardly at all. */
    public static double mossClimate(ClimateZone zone) {
        return switch (zone) {
            case OCEAN, TROPICAL -> 1.0;
            case TEMPERATE -> 0.8;
            case COLD, NO_SEASONS -> 0.5;
            case SAVANNA -> 0.25;
            case ARID -> 0.03;
        };
    }

    /** Lichens cope with cold, wind and drought better than moss: they're everywhere on mountains and tundra. */
    public static double lichenClimate(ClimateZone zone) {
        return switch (zone) {
            case COLD, OCEAN -> 1.0;
            case TEMPERATE -> 0.8;
            case NO_SEASONS -> 0.5;
            case TROPICAL, SAVANNA -> 0.4;
            case ARID -> 0.2;
        };
    }

    /**
     * Weather now: they grow when wet (water from above), are dormant when frozen or in summer drought.
     *
     * @param lichen lichens keep growing a little in frost, moss doesn't
     */
    public static double weather(double dailyMean, boolean raining, boolean lichen) {
        double temperature;
        if (dailyMean <= 0) {
            temperature = lichen ? 0.3 : 0;
        } else if (dailyMean > 24) {
            temperature = 0.3;
        } else {
            temperature = 1;
        }
        return temperature * (raining ? 2.5 : 1);
    }

    /**
     * Moss likes the shady, damp north side; the sunny south side and the dry underside of an overhang much less.
     * Lichens like light and grow on every side.
     */
    public static double mossFace(Face face) {
        return switch (face) {
            case NORTH -> 1.5;
            case EAST, WEST -> 1.0;
            case UP -> 0.8;
            case SOUTH -> 0.5;
            case DOWN -> 0.3;
        };
    }

    public static double lichenFace(Face face) {
        return face == Face.UP ? 1.2 : face == Face.DOWN ? 0.3 : 1.0;
    }

    /** Neighbours that already have moss (or lichen) make it spread faster. */
    public static double neighbours(int count) {
        return Math.min(MAX_NEIGHBOUR_FACTOR, 1 + NEIGHBOUR_BONUS * count);
    }

    /** Light: moss and lichen need some (dark dungeons and deep caves stay as they are); 0..15 sky light. */
    public static double light(int skyLight) {
        return skyLight <= 0 ? 0 : skyLight < 4 ? 0.3 : 1;
    }

    /** Chance that an old cobblestone or stone brick block turns mossy on this visit. */
    public static double mossChance(ClimateZone zone, double dailyMean, boolean raining, Face face, int skyLight,
                                    int mossyNeighbours) {
        return MOSS_CHANCE * mossClimate(zone) * weather(dailyMean, raining, false) * mossFace(face) * light(skyLight)
                * neighbours(mossyNeighbours);
    }

    /** Chance that lichen grows on this open rock face on this visit (0 outside lichen patches). */
    public static double lichenChance(ClimateZone zone, double dailyMean, boolean raining, Face face, int skyLight,
                                      int lichenNeighbours, int x, int y, int z) {
        double patch = WaterLilyRules.hash(Math.floorDiv(x, 6) * 31 + Math.floorDiv(y, 6), Math.floorDiv(z, 6), 20);
        if (patch >= LICHEN_PATCH_SHARE) {
            return 0;
        }
        return LICHEN_CHANCE * lichenClimate(zone) * weather(dailyMean, raining, true) * lichenFace(face)
                * light(skyLight) * neighbours(lichenNeighbours);
    }

    /** Chance that a moss carpet grows on top of a rock in the shade of trees on this visit. */
    public static double mossCarpetChance(ClimateZone zone, double dailyMean, boolean raining, int carpetNeighbours) {
        return MOSS_CARPET_CHANCE * mossClimate(zone) * weather(dailyMean, raining, false) * neighbours(carpetNeighbours);
    }
}
