package io.github.suel_ki.foodeffecttooltips.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.ArrayList;
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
        ForgeConfigSpec.ConfigValue<List<? extends String>> WhitelistedItemIdentifiers = FoodEffectsConfig.WhitelistedItemIdentifiers;
        ForgeConfigSpec.ConfigValue<List<? extends String>> WhitelistedModsIDs = FoodEffectsConfig.WhitelistedModsIDs;
        ForgeConfigSpec.ConfigValue<List<? extends String>> BlacklistedItemIdentifiers = FoodEffectsConfig.BlacklistedItemIdentifiers;
        ForgeConfigSpec.ConfigValue<List<? extends String>> BlacklistedModsIDs = FoodEffectsConfig.BlacklistedModsIDs;
        ForgeConfigSpec.EnumValue<FoodEffectsConfig.TooltipBehavior> tooltipBehavior = FoodEffectsConfig.DefaultTooltipBehavior;

        general.addEntry(eb
                .startBooleanToggle(
                        Component.translatable("cloth_config.FoodEffectTooltips.ShowSuspiciousStewTooltips"),
                        showSuspiciousStewTooltips.get())
                .setTooltip(Component.translatable("text.cloth_config.FoodEffectTooltips.option.ShowSuspiciousStewTooltips"))
                .setDefaultValue(showSuspiciousStewTooltips.getDefault())
                .setSaveConsumer(showSuspiciousStewTooltips::set)
                .build());

        general.addEntry(eb
                .startStrList(
                        Component.translatable("cloth_config.FoodEffectTooltips.WhitelistedItemIdentifiers"),
                        convertToListOfString(WhitelistedItemIdentifiers.get()))
                .setTooltip(Component.translatable("text.cloth_config.FoodEffectTooltips.option.WhitelistedItemIdentifiers"))
                .setDefaultValue(convertToListOfString(WhitelistedItemIdentifiers.getDefault()))
                .setSaveConsumer(WhitelistedItemIdentifiers::set)
                .build());

        general.addEntry(eb
                .startStrList(
                        Component.translatable("cloth_config.FoodEffectTooltips.WhitelistedModsIDs"),
                        convertToListOfString(WhitelistedModsIDs.get()))
                .setTooltip(Component.translatable("text.cloth_config.FoodEffectTooltips.option.WhitelistedModsIDs"))
                .setDefaultValue(convertToListOfString(WhitelistedModsIDs.getDefault()))
                .setSaveConsumer(WhitelistedModsIDs::set)
                .build());

        general.addEntry(eb
                .startStrList(
                        Component.translatable("cloth_config.FoodEffectTooltips.BlacklistedItemIdentifiers"),
                        convertToListOfString(BlacklistedItemIdentifiers.get()))
                .setTooltip(Component.translatable("text.cloth_config.FoodEffectTooltips.option.BlacklistedItemIdentifiers"))
                .setDefaultValue(convertToListOfString(BlacklistedItemIdentifiers.getDefault()))
                .setSaveConsumer(BlacklistedItemIdentifiers::set)
                .build());

        general.addEntry(eb
                .startStrList(
                        Component.translatable("cloth_config.FoodEffectTooltips.BlacklistedModsIDs"),
                        convertToListOfString(BlacklistedModsIDs.get()))
                .setTooltip(Component.translatable("text.cloth_config.FoodEffectTooltips.option.BlacklistedModsIDs"))
                .setDefaultValue(convertToListOfString(BlacklistedModsIDs.getDefault()))
                .setSaveConsumer(BlacklistedModsIDs::set)
                .build());

        general.addEntry(eb
                .startEnumSelector(
                        Component.translatable("cloth_config.FoodEffectTooltips.DefaultTooltipBehavior"),
                        FoodEffectsConfig.TooltipBehavior.class,
                        tooltipBehavior.get())
                .setTooltip(Component.translatable("text.cloth_config.FoodEffectTooltips.option.DefaultTooltipBehavior"))
                .setDefaultValue(tooltipBehavior.getDefault())
                .setSaveConsumer(tooltipBehavior::set)
                .build());

        return builder.build();

    }

    public static List<String> convertToListOfString(List<? extends String> list) {
        List<String> resultList = new ArrayList<>(list.size());
        resultList.addAll(list);
        return resultList;
    }

}
