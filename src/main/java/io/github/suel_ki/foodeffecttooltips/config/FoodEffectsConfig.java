package io.github.suel_ki.foodeffecttooltips.config;


import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class FoodEffectsConfig {

    public static final ModConfigSpec CLIENT_SPEC;

    public static final ModConfigSpec.BooleanValue ShowSuspiciousStewTooltips;

    public static final ModConfigSpec.BooleanValue UseAsWhitelistInstead;

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BlacklistedItemIdentifiers;

    public static final ModConfigSpec.ConfigValue<List<? extends String>>  BlacklistedModsIDs;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("food effects tooltip");

        ShowSuspiciousStewTooltips = builder.
                comment("Show suspicious stew food effects tooltips").
                define("showSuspiciousStewTooltips", false);

        UseAsWhitelistInstead = builder.
                comment("Use Lists as Whitelists instead.").
                define("whitelistInstead", false);

        BlacklistedItemIdentifiers = builder.
                comment("Blacklisted Item.").
                defineList("blacklistItems", List.of("modid:testitem"), itemName -> itemName instanceof String);

        BlacklistedModsIDs = builder.
                comment("Blacklisted Mod IDs.").
                defineList("blacklistModsIDs", List.of("modid"), modName -> modName instanceof String);

        builder.pop();

        CLIENT_SPEC = builder.build();
    }

}
