package io.github.suel_ki.foodeffecttooltips;

import io.github.suel_ki.foodeffecttooltips.config.ConfigScreen;
import io.github.suel_ki.foodeffecttooltips.config.FoodEffectsConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLLoader;

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
