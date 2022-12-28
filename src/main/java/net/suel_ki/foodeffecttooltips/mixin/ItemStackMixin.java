package net.suel_ki.foodeffecttooltips.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.suel_ki.foodeffecttooltips.TooltipHelper;
import net.suel_ki.foodeffecttooltips.config.FoodEffectsConfig;
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
                    target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V",
                    shift = At.Shift.AFTER),
            locals= LocalCapture.CAPTURE_FAILHARD)
    private void getTooltipLines(Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir, List<Component> tooltips) {
        ItemStack stack = (ItemStack) ( Object ) this;
        if (!stack.isEmpty() && stack.getItem().isEdible() && TooltipHelper.shouldShowTooltip(stack)) {
            if (stack.getItem() instanceof SuspiciousStewItem &&
                    FoodEffectsConfig.ShowSuspiciousStewTooltips.get() && !pIsAdvanced.isCreative())
                PotionUtils.addPotionTooltip(TooltipHelper.getStewEffects(stack), tooltips, 1.0F);
            TooltipHelper.addFoodComponentEffectTooltip(stack, tooltips);
        }
    }

}
