package io.github.suel_ki.foodeffecttooltips.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class FoodEffectsConfig {

    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.BooleanValue ShowSuspiciousStewTooltips;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WhitelistedItemIdentifiers;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>>  WhitelistedModsIDs;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BlacklistedItemIdentifiers;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>>  BlacklistedModsIDs;

    public static final ForgeConfigSpec.EnumValue<TooltipBehavior> DefaultTooltipBehavior;

    public enum TooltipBehavior {
        SHOW,
        HIDE
    }

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("food effects tooltip");

        ShowSuspiciousStewTooltips = builder.
                comment("Show suspicious stew food effects tooltips").
                define("showSuspiciousStewTooltips", false);

        WhitelistedItemIdentifiers = builder.
                comment("Whitelisted Item.").
                defineList("whitelistItems", List.of("modid:testitem"), itemName -> itemName instanceof String);

        WhitelistedModsIDs = builder.
                comment("Whitelisted Item.").
                defineList("whitelistModsIDs", List.of("modid"), modName -> modName instanceof String);

        BlacklistedItemIdentifiers = builder.
                comment("Blacklisted Item.").
                defineList("blacklistItems", List.of("modid:testitem"), itemName -> itemName instanceof String);

        BlacklistedModsIDs = builder.
                comment("Blacklisted Mod IDs.").
                defineList("blacklistModsIDs", List.of("modid"), modName -> modName instanceof String);

        DefaultTooltipBehavior = builder.
                comment("Default tooltip behavior for unlisted items").
                defineEnum("defaultTooltipBehavior", TooltipBehavior.SHOW);

        builder.pop();

        CLIENT_SPEC = builder.build();
    }

}
