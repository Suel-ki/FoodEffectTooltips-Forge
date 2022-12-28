package net.suel_ki.foodeffecttooltips.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.List;

public class ConfigScreen {

    public static void register() {
        ModLoadingContext.get().
                registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> ConfigScreen.getConfigScreen(parent)));
    }

    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.
                create().
                setParentScreen(parent).
                setTitle(Component.translatable("config.foodeffecttooltips.title")).
                transparentBackground();

        ConfigEntryBuilder eb = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("cloth_config.FoodEffectTooltips.category.general"));

        ForgeConfigSpec.BooleanValue showSuspiciousStewTooltips = FoodEffectsConfig.ShowSuspiciousStewTooltips;
        ForgeConfigSpec.BooleanValue useAsWhitelistInstead = FoodEffectsConfig.UseAsWhitelistInstead;
        ForgeConfigSpec.ConfigValue<List<? extends String>> BlacklistedItemIdentifiers = FoodEffectsConfig.BlacklistedItemIdentifiers;
        ForgeConfigSpec.ConfigValue<List<? extends String>> BlacklistedModsIDs = FoodEffectsConfig.BlacklistedModsIDs;

        general.addEntry(eb
                .startBooleanToggle(
                        Component.translatable("cloth_config.FoodEffectTooltips.ShowSuspiciousStewTooltips"),
                        showSuspiciousStewTooltips.get())
                .setTooltip(Component.translatable("text.cloth_config.FoodEffectTooltips.option.ShowSuspiciousStewTooltips"))
                .setDefaultValue(showSuspiciousStewTooltips.getDefault())
                .setSaveConsumer(showSuspiciousStewTooltips::set)
                .build());

        general.addEntry(eb
                .startBooleanToggle(
                        Component.translatable("cloth_config.FoodEffectTooltips.UseAsWhitelistInstead"),
                        useAsWhitelistInstead.get())
                .setTooltip(Component.translatable("text.cloth_config.FoodEffectTooltips.option.UseAsWhitelistInstead"))
                .setDefaultValue(useAsWhitelistInstead.getDefault())
                .setSaveConsumer(useAsWhitelistInstead::set)
                .build());

        general.addEntry(eb
                .startStrList(
                        Component.translatable("cloth_config.FoodEffectTooltips.BlacklistedItemIdentifiers"),
                        (List<String>) BlacklistedItemIdentifiers.get())
                .setTooltip(Component.translatable("text.cloth_config.FoodEffectTooltips.option.BlacklistedItemIdentifiers"))
                .setDefaultValue((List<String>) BlacklistedItemIdentifiers.getDefault())
                .setSaveConsumer(BlacklistedItemIdentifiers::set)
                .build());

        general.addEntry(eb
                .startStrList(
                        Component.translatable("cloth_config.FoodEffectTooltips.BlacklistedModsIDs"),
                        (List<String>) BlacklistedModsIDs.get())
                .setTooltip(Component.translatable("text.cloth_config.FoodEffectTooltips.option.BlacklistedModsIDs"))
                .setDefaultValue((List<String>) BlacklistedModsIDs.getDefault())
                .setSaveConsumer(BlacklistedItemIdentifiers::set)
                .build());

        return builder.build();

    }

}
