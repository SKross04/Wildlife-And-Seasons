package net.skross04.wildalmanac.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.skross04.wildalmanac.ecology.season.Season;
import net.skross04.wildalmanac.ecology.season.SeasonDate;
import net.skross04.wildalmanac.season.SeasonSavedData;
import net.skross04.wildalmanac.season.SeasonTranslations;
import net.skross04.wildalmanac.season.WorldSeasons;

/**
 * {@code /season} – shows the season (anyone can use it)
 * {@code /season set <spring|summer|autumn|winter>} – jumps to day 1 of that season (operators only)
 * {@code /season set day <1-100>} – jumps to a day of the year (operators only)
 * <p>
 * Setting keeps the current year. Vanilla {@code /time set} does not change the season.
 */
public class SeasonCommand {
    private static final int YEAR_LENGTH = SeasonDate.DEFAULT_YEAR_LENGTH;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Brigadier builds commands as a tree: "season" → "set" → "summer" etc.
        LiteralArgumentBuilder<CommandSourceStack> set = Commands.literal("set")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));

        for (Season season : Season.values()) {
            set.then(Commands.literal(season.id())
                    .executes(context -> setSeason(context.getSource(), season)));
        }

        set.then(Commands.literal("day")
                .then(Commands.argument("day", IntegerArgumentType.integer(1, YEAR_LENGTH))
                        .executes(context -> setDay(context.getSource(), IntegerArgumentType.getInteger(context, "day")))));

        dispatcher.register(Commands.literal("season")
                .executes(context -> query(context.getSource()))
                .then(Commands.literal("query").executes(context -> query(context.getSource())))
                .then(set));
    }

    private static int query(CommandSourceStack source) {
        SeasonDate date = WorldSeasons.currentDate(source.getServer());
        source.sendSuccess(() -> SeasonTranslations.describe(date), false);
        // The return value can be stored with /execute store, e.g. for command blocks.
        return date.dayOfYear();
    }

    private static int setSeason(CommandSourceStack source, Season season) {
        SeasonSavedData data = SeasonSavedData.get(source.getServer());
        data.calendar().setSeason(season, YEAR_LENGTH);
        return afterSet(source, data);
    }

    private static int setDay(CommandSourceStack source, int dayOfYear) {
        SeasonSavedData data = SeasonSavedData.get(source.getServer());
        data.calendar().setDayOfYear(dayOfYear, YEAR_LENGTH);
        return afterSet(source, data);
    }

    private static int afterSet(CommandSourceStack source, SeasonSavedData data) {
        data.setDirty();
        SeasonDate date = data.calendar().date(YEAR_LENGTH);
        // true = other operators also see this message, like vanilla admin commands
        source.sendSuccess(() -> Component.translatable(SeasonTranslations.COMMAND_SET, SeasonTranslations.describe(date)), true);
        return date.dayOfYear();
    }
}
