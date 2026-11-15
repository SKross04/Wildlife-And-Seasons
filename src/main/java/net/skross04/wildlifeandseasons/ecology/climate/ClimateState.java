package net.skross04.wildlifeandseasons.ecology.climate;

/**
 * The climate at one place and moment.
 *
 * @param temperature air temperature in °C (includes time of day and height)
 * @param rainfall    how wet this time of year is, 0 (bone dry) to 1 (very wet); later used for the weather schedule
 * @param daylight    share of the day that's light, e.g. 0.6 = long summer days
 */
public record ClimateState(double temperature, double rainfall, double daylight) {
}
