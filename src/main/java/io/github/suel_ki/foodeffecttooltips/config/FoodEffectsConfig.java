package io.github.suel_ki.foodeffecttooltips.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class FoodEffectsConfig {

    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.BooleanValue ShowSuspiciousStewTooltips;

    public static final ForgeConfigSpec.BooleanValue UseAsWhitelistInstead;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BlacklistedItemIdentifiers;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>>  BlacklistedModsIDs;

    public static final List<String> dafaultBlacklistedods = Lists.newArrayList("farmersdelight", "expandeddelight", "moredelight", "oceansdelight", "ubesdelight", "vinery");

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("food effects tooltip");

        ShowSuspiciousStewTooltips = builder.
                comment("Show suspicious stew food effects tooltips").
                define("showSuspiciousStewTooltips", false);

        UseAsWhitelistInstead = builder.
                comment("Use Lists as Whitelists instead.").
                define("whitelistInstead", false);

        BlacklistedItemIdentifiers = builder.
                comment("Blacklisted Item.").
                defineList("blacklistItems", Lists.newArrayList("modid:testitem"), itemName -> itemName instanceof String);

        BlacklistedModsIDs = builder.
                comment("Blacklisted Mod IDs.").
                defineList("blacklistModsIDs",
                        dafaultBlacklistedods, modName -> modName instanceof String);

        builder.pop();

        CLIENT_SPEC = builder.build();
    }

}
