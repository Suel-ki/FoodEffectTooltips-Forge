package io.github.suel_ki.foodeffecttooltips.mixin;

import io.github.suel_ki.foodeffecttooltips.TooltipHelper;
import io.github.suel_ki.foodeffecttooltips.config.FoodEffectsConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(
            method = "getTooltipLines",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/Item;appendHoverText(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/util/ITooltipFlag;)V",
                    shift = At.Shift.AFTER),
            locals= LocalCapture.CAPTURE_FAILHARD
    )
    private void getTooltipLines(PlayerEntity player, ITooltipFlag tooltipFlag, CallbackInfoReturnable<List<ITextComponent>> cir, List<ITextComponent> tooltips, IFormattableTextComponent text, int i) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.isEmpty() && stack.getItem().isEdible() && TooltipHelper.shouldShowTooltip(stack)) {

            if (stack.getItem() instanceof SuspiciousStewItem &&
                    FoodEffectsConfig.ShowSuspiciousStewTooltips.get()) {
                List<EffectInstance> effects = TooltipHelper.getStewEffects(stack);
                if (!effects.isEmpty())
                    PotionUtils.addPotionTooltip(stack, tooltips, 1.0F);
            }

            TooltipHelper.addFoodComponentEffectTooltip(stack, tooltips);
        }
    }
}
