package io.github.suel_ki.foodeffecttooltips.mixin;

import io.github.suel_ki.foodeffecttooltips.TooltipHelper;
import io.github.suel_ki.foodeffecttooltips.config.FoodEffectsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Unique
    private static float foodeffecttooltips$getTickRate() {
        ClientLevel world = Minecraft.getInstance().level;
        return world == null ? 20.0F : world.tickRateManager().tickrate();
    }

    @Inject(
            method = "getTooltipLines",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V",
                    shift = At.Shift.AFTER),
            locals= LocalCapture.CAPTURE_FAILHARD
    )
    private void getTooltipLines(Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir,List<Component> tooltips, MutableComponent text, int j) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.isEmpty() && stack.getItem().isEdible() && TooltipHelper.shouldShowTooltip(stack)) {

            if (stack.getItem() instanceof SuspiciousStewItem &&
                    FoodEffectsConfig.ShowSuspiciousStewTooltips.get() && !tooltipFlag.isCreative()) {
                List<MobEffectInstance> effects = TooltipHelper.getStewEffects(stack);
                if (effects.size() > 0)
                    PotionUtils.addPotionTooltip(effects, tooltips, 1.0F, foodeffecttooltips$getTickRate());
            }

            TooltipHelper.addFoodComponentEffectTooltip(stack, tooltips, foodeffecttooltips$getTickRate());
        }
    }

}
