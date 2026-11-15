package net.skross04.wildlifeandseasons.datagen.lang;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.skross04.wildlifeandseasons.WildlifeAndSeasons;
import net.skross04.wildlifeandseasons.ecology.season.Season;
import net.skross04.wildlifeandseasons.ecology.season.SubSeason;
import net.skross04.wildlifeandseasons.season.SeasonTranslations;

/**
 * Shared base for all language files. English ({@link EnUsLanguageProvider}) is the source;
 * every new item, block or message gets a line in every language provider in the same commit.
 */
public abstract class ModLanguageProvider extends LanguageProvider {
    protected ModLanguageProvider(PackOutput output, String locale) {
        super(output, WildlifeAndSeasons.MODID, locale);
    }

    /** Adds the early / mid / late names for one season. */
    protected void addSeason(Season season, String early, String mid, String late) {
        add(SeasonTranslations.seasonKey(SubSeason.EARLY, season), early);
        add(SeasonTranslations.seasonKey(SubSeason.MID, season), mid);
        add(SeasonTranslations.seasonKey(SubSeason.LATE, season), late);
    }
}
