package net.skross04.wildalmanac.ecology.season;

/**
 * The four seasons of the Wild Almanac year, in calendar order.
 * <p>
 * This package is plain Java (no Minecraft or NeoForge imports), so the season
 * logic can be reused if the mod is ever ported to another loader.
 */
public enum Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER;

    /** Lowercase name used in translation keys, e.g. {@code "spring"}. */
    public String id() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
}
