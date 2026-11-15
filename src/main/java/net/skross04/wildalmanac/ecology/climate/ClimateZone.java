package net.skross04.wildalmanac.ecology.climate;

import java.util.Locale;

/**
 * The climate zones biomes are sorted into. Each zone has its own year: how warm winter and summer get,
 * how big the difference between day and night is, when it rains most and how much the day length changes.
 * <p>
 * Temperatures are in °C. Starting values; tune them during testing.
 */
public enum ClimateZone {
    //            winter summer skew  day/night  rain: mean  amp   peak   daylight  ref. biome temp
    /** Plains, forests, meadows: the classic four seasons, with frost and snow in mid-winter. Wettest in late autumn, driest in early summer. */
    TEMPERATE(    -4.0,  20.0, 1.0,   3.0,             0.50, 0.15, 0.750, 0.10,     0.8),
    /** Taigas, snowy biomes, high mountains: long winter, short intense summer. */
    COLD(        -15.0,  14.0, 2.0,   4.0,             0.40, 0.10, 0.875, 0.15,     0.2),
    /** Jungles, mangroves: always warm, a wet season (around summer) and a dry season (around winter). */
    TROPICAL(     25.0,  28.0, 1.0,   3.0,             0.60, 0.35, 0.375, 0.02,     0.95),
    /** Savannas: warm, with a strong wet/dry cycle. */
    SAVANNA(      22.0,  29.0, 1.0,   7.0,             0.40, 0.40, 0.375, 0.03,     2.0),
    /** Deserts, badlands: hot and dry all year, cold nights, rare spring rains. */
    ARID(         12.0,  32.0, 1.0,  12.0,             0.06, 0.05, 0.125, 0.06,     2.0),
    /** Oceans and beaches: milder, smaller swings. */
    OCEAN(         6.0,  17.0, 1.0,   2.0,             0.55, 0.10, 0.750, 0.08,     0.5),
    /** Nether, End, void: no seasons at all. */
    NO_SEASONS(    0.0,   0.0, 1.0,   0.0,             0.00, 0.00, 0.000, 0.00,     0.0);

    /** Average temperature on the coldest day (mid-winter), °C. */
    final double winterTemperature;
    /** Average temperature on the warmest day (mid-summer), °C. */
    final double summerTemperature;
    /** 1 = winter and summer equally long; higher = longer winters, shorter summers. */
    final double skew;
    /** How much warmer the afternoon is than the daily average (and colder the night), °C. */
    final double dayNightSwing;
    /** Average rainfall (0 = never rains, 1 = always wet). */
    final double rainfallMean;
    /** How much rainfall goes up and down through the year. */
    final double rainfallAmplitude;
    /** Point in the year when it's wettest (0 = start of spring, 0.25 = start of summer, …). */
    final double rainfallPeak;
    /** How much the share of daylight changes: 0.1 means 60% daylight in summer, 40% in winter. */
    final double daylightSwing;
    /** The vanilla biome temperature this zone is designed around; warmer/colder biomes shift a bit. */
    final double referenceBiomeTemperature;

    ClimateZone(double winterTemperature, double summerTemperature, double skew, double dayNightSwing,
                double rainfallMean, double rainfallAmplitude, double rainfallPeak, double daylightSwing,
                double referenceBiomeTemperature) {
        this.winterTemperature = winterTemperature;
        this.summerTemperature = summerTemperature;
        this.skew = skew;
        this.dayNightSwing = dayNightSwing;
        this.rainfallMean = rainfallMean;
        this.rainfallAmplitude = rainfallAmplitude;
        this.rainfallPeak = rainfallPeak;
        this.daylightSwing = daylightSwing;
        this.referenceBiomeTemperature = referenceBiomeTemperature;
    }

    public boolean hasSeasons() {
        return this != NO_SEASONS;
    }

    /** Lowercase name used in tags and translation keys, e.g. {@code "temperate"}. */
    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    /**
     * Fallback for biomes that aren't in any climate zone tag, using the biome's vanilla climate values
     * (vanilla temperature: snowy plains 0.0, taiga 0.25, plains 0.8, jungle 0.95, desert 2.0;
     * downfall: desert 0.0, plains 0.4, jungle 0.9).
     */
    public static ClimateZone fromVanillaClimate(double biomeTemperature, double downfall) {
        if (biomeTemperature <= 0.3) {
            return COLD;
        }
        if (biomeTemperature >= 1.5 && downfall <= 0.2) {
            return ARID;
        }
        if (biomeTemperature >= 0.95 && downfall >= 0.8) {
            return TROPICAL;
        }
        if (biomeTemperature >= 1.2) {
            return SAVANNA;
        }
        return TEMPERATE;
    }
}
