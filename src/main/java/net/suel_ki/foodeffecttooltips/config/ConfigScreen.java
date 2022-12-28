package net.suel_ki.foodeffecttooltips.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.List;

public class ConfigScreen {

    public static void register() {
        ModLoadingContext.get().
                registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
                () -> (minecraft, parent) -> ConfigScreen.getConfigScreen(parent));
    }

    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.
                create().
                setParentScreen(parent).
                setTitle(new TranslationTextComponent("config.foodeffecttooltips.title")).
                transparentBackground();

        ConfigEntryBuilder eb = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(new TranslationTextComponent("cloth_config.FoodEffectTooltips.category.general"));

        ForgeConfigSpec.BooleanValue useAsWhitelistInstead = FoodEffectsConfig.UseAsWhitelistInstead;
        ForgeConfigSpec.ConfigValue<List<? extends String>> BlacklistedItemIdentifiers = FoodEffectsConfig.BlacklistedItemIdentifiers;
        ForgeConfigSpec.ConfigValue<List<? extends String>> BlacklistedModsIDs = FoodEffectsConfig.BlacklistedModsIDs;

        general.addEntry(eb
                .startBooleanToggle(
                        new TranslationTextComponent("cloth_config.FoodEffectTooltips.UseAsWhitelistInstead"),
                        useAsWhitelistInstead.get())
                .setTooltip(new TranslationTextComponent("text.cloth_config.FoodEffectTooltips.option.UseAsWhitelistInstead"))
                .setDefaultValue(false)
                .setSaveConsumer(useAsWhitelistInstead::set)
                .build());

        general.addEntry(eb
                .startStrList(
                        new TranslationTextComponent("cloth_config.FoodEffectTooltips.BlacklistedItemIdentifiers"),
                        (List<String>) BlacklistedItemIdentifiers.get())
                .setTooltip(new TranslationTextComponent("text.cloth_config.FoodEffectTooltips.option.BlacklistedItemIdentifiers"))
                .setDefaultValue(List.of("modid:testitem"))
                .setSaveConsumer(BlacklistedItemIdentifiers::set)
                .build());

        general.addEntry(eb
                .startStrList(
                        new TranslationTextComponent("cloth_config.FoodEffectTooltips.BlacklistedModsIDs"),
                        (List<String>) BlacklistedModsIDs.get())
                .setTooltip(new TranslationTextComponent("text.cloth_config.FoodEffectTooltips.option.BlacklistedModsIDs"))
                .setDefaultValue(List.of("modid"))
                .setSaveConsumer(BlacklistedItemIdentifiers::set)
                .build());

        return builder.build();

    }

}
