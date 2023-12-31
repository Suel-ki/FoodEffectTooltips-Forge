package io.github.suel_ki.foodeffecttooltips;

import io.github.suel_ki.foodeffecttooltips.config.ConfigScreen;
import io.github.suel_ki.foodeffecttooltips.config.FoodEffectsConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;

@Mod(FoodEffectTooltips.MODID)
public class FoodEffectTooltips
{
    public static final String MODID = "foodeffecttooltips";

    public FoodEffectTooltips()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FoodEffectsConfig.CLIENT_SPEC);
        if (FMLLoader.getDist() == Dist.CLIENT && ModList.get().isLoaded("cloth_config")) {
            ConfigScreen.register();
        }
    }

}
