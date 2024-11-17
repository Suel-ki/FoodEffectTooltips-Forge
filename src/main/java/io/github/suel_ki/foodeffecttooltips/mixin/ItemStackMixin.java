package io.github.suel_ki.foodeffecttooltips.mixin;

import io.github.suel_ki.foodeffecttooltips.TooltipHelper;
import io.github.suel_ki.foodeffecttooltips.config.FoodEffectsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
                    target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V",
                    shift = At.Shift.AFTER),
            locals= LocalCapture.CAPTURE_FAILHARD
    )
    private void getTooltipLines(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, List<Component> tooltips, Consumer<Component> consumer) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.isEmpty() && TooltipHelper.shouldShowTooltip(stack)) {
            @Nullable Consumable foodComponent = stack.get(DataComponents.CONSUMABLE);
            if (foodComponent != null) {
                TooltipHelper.addFoodComponentEffectTooltip(stack, foodComponent, tooltips, foodeffecttooltips$getTickRate());
            }
            if (FoodEffectsConfig.ShowSuspiciousStewTooltips.get() && !tooltipFlag.isCreative()) {
                @Nullable SuspiciousStewEffects sus = stack.getOrDefault(DataComponents.SUSPICIOUS_STEW_EFFECTS, null);
                if (sus != null && !sus.effects().isEmpty()) {
                    List<MobEffectInstance> effects = new ArrayList<>();
                    for (SuspiciousStewEffects.Entry stewEffect : sus.effects()) {
                        effects.add(stewEffect.createEffectInstance());
                    }
                    PotionContents.addPotionTooltip(effects, tooltips::add, 1.0F, foodeffecttooltips$getTickRate());
                }
            }
        }
    }

}
