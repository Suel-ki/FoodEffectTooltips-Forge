package io.github.suel_ki.foodeffecttooltips.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigScreen {

    public static void register() {
        ModLoadingContext.get().
                registerExtensionPoint(IConfigScreenFactory.class,
                        () -> new IConfigScreenFactory() {
                            @Override
                            public @NotNull Screen createScreen(@NotNull ModContainer modContainer, @NotNull Screen parent) {
                                return getConfigScreen(parent);
                            }
                        });
    }

    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.
                create().
                setParentScreen(parent).
                setTitle(Component.translatable("config.foodeffecttooltips.title")).
                transparentBackground();

        ConfigEntryBuilder eb = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("cloth_config.FoodEffectTooltips.category.general"));

        ModConfigSpec.BooleanValue showSuspiciousStewTooltips = FoodEffectsConfig.ShowSuspiciousStewTooltips;
        ModConfigSpec.BooleanValue useAsWhitelistInstead = FoodEffectsConfig.UseAsWhitelistInstead;
        ModConfigSpec.ConfigValue<List<? extends String>> BlacklistedItemIdentifiers = FoodEffectsConfig.BlacklistedItemIdentifiers;
        ModConfigSpec.ConfigValue<List<? extends String>> BlacklistedModsIDs = FoodEffectsConfig.BlacklistedModsIDs;

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
                .setSaveConsumer(BlacklistedModsIDs::set)
                .build());

        return builder.build();

    }

}
