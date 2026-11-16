package net.skross04.wildlifeandseasons.nature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.ecology.vegetation.DesertBloomRules;

/**
 * Remembers when it last rained in the desert and how many rainy days this rain season had, so a bloom
 * survives a server restart. One per world (deserts are in the overworld).
 */
public class DesertBloomSavedData extends SavedData {
    private static final long NEVER = Long.MIN_VALUE;

    private static final Codec<DesertBloomSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("last_rain_day").forGetter(data -> data.lastRainDay),
            Codec.INT.fieldOf("rainy_days").forGetter(data -> data.rainyDays)
    ).apply(instance, DesertBloomSavedData::new));

    public static final SavedDataType<DesertBloomSavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(WildlifeAndSeasons.MODID, "desert_bloom"),
            level -> new DesertBloomSavedData(NEVER, 0),
            level -> CODEC);

    private long lastRainDay;
    private int rainyDays;

    private DesertBloomSavedData(long lastRainDay, int rainyDays) {
        this.lastRainDay = lastRainDay;
        this.rainyDays = rainyDays;
    }

    public static DesertBloomSavedData get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    /** It's raining in a desert today. A new day of rain counts once; after a long dry gap a new season starts. */
    public void rainedOn(long today) {
        if (today == lastRainDay) {
            return;
        }
        if (lastRainDay == NEVER || today < lastRainDay || today - lastRainDay > DesertBloomRules.NEW_SEASON_GAP_DAYS) {
            rainyDays = 0;
        }
        rainyDays++;
        lastRainDay = today;
        setDirty();
    }

    /** Days since the last desert rain, or -1 if it never rained (or the calendar was set back before it). */
    public long daysSinceRain(long today) {
        return lastRainDay == NEVER || today < lastRainDay ? -1 : today - lastRainDay;
    }

    public int rainyDays() {
        return rainyDays;
    }
}
