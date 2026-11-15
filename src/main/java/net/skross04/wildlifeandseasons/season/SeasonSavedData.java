package net.skross04.wildlifeandseasons.season;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.ecology.season.SeasonCalendar;

/**
 * Saves the season calendar with the world (in the world's {@code data} folder).
 * There is one calendar per world, shared by all dimensions.
 */
public class SeasonSavedData extends SavedData {
    /** How the calendar is written to and read from the save file. */
    private static final Codec<SeasonSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("elapsed_ticks").forGetter(data -> data.calendar.elapsedTicks()),
            Codec.LONG.fieldOf("last_clock_ticks").forGetter(data -> data.calendar.lastClockTicks())
    ).apply(instance, (elapsedTicks, lastClockTicks) -> new SeasonSavedData(new SeasonCalendar(elapsedTicks, lastClockTicks))));

    public static final SavedDataType<SeasonSavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(WildlifeAndSeasons.MODID, "seasons"),
            level -> new SeasonSavedData(new SeasonCalendar()),
            level -> CODEC);

    private final SeasonCalendar calendar;
    /** Set when players skip the night; not saved, it only lives until the next tick. */
    private boolean slept;

    private SeasonSavedData(SeasonCalendar calendar) {
        this.calendar = calendar;
    }

    /** Loads the calendar, or creates a new one the first time Wildlife & Seasons runs in this world. */
    public static SeasonSavedData get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public SeasonCalendar calendar() {
        return calendar;
    }

    public void markSlept() {
        slept = true;
    }

    /** Called every server tick with the overworld day clock. */
    public void tick(long clockTicks) {
        if (calendar.update(clockTicks, slept)) {
            setDirty();
        }
        slept = false;
    }

    /** True when the day changed since players last got the season (not saved; after a restart everyone gets it on join). */
    private long lastSyncedDay = Long.MIN_VALUE;

    public boolean needsSync() {
        return calendar.dayIndex() != lastSyncedDay;
    }

    public void markSynced() {
        lastSyncedDay = calendar.dayIndex();
    }
}
