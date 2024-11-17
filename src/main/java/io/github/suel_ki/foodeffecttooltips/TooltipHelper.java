package io.github.suel_ki.foodeffecttooltips;

import com.google.common.collect.Lists;
import io.github.suel_ki.foodeffecttooltips.config.FoodEffectsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TooltipHelper {

    public static boolean shouldShowTooltip(ItemStack stack) {
        Item item = stack.getItem();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);

        boolean isWhitelist = FoodEffectsConfig.UseAsWhitelistInstead.get();
        if(FoodEffectsConfig.BlacklistedItemIdentifiers.get().contains(id.toString())) {
            return isWhitelist;
        }
        if(FoodEffectsConfig.BlacklistedModsIDs.get().contains(id.getNamespace())) {
            return isWhitelist;
        }
        return !isWhitelist;
    }

    public static void addFoodComponentEffectTooltip(@NotNull ItemStack stack, @NotNull Consumable foodProperties, @NotNull List<Component> tooltip, float tickRate) {
        if(foodProperties.onConsumeEffects().isEmpty()) {
            return;
        }
        boolean isDrink = stack.getUseAnimation() == ItemUseAnimation.DRINK;
        buildFoodEffectTooltip(tooltip, foodProperties.onConsumeEffects(), tickRate, isDrink);
    }

    public static void buildFoodEffectTooltip(@NotNull List<Component> tooltip, List<ConsumeEffect>  effects, float tickRate, boolean drink) {
        if(effects.isEmpty()) {
            return;
        }

        List<Pair<Holder<Attribute>, AttributeModifier>> modifiers = Lists.newArrayList();
        MutableComponent mutablecomponent;
        Holder<MobEffect> holder;

        for (ConsumeEffect entry : effects) {
            if (!(entry instanceof ApplyStatusEffectsConsumeEffect applyEffectsConsumeEffect)) {
                continue;
            }
            for (MobEffectInstance mobEffectInstance : applyEffectsConsumeEffect.effects()) {
                mutablecomponent = Component.translatable(mobEffectInstance.getDescriptionId());
                holder = mobEffectInstance.getEffect();
                holder.value().createModifiers(mobEffectInstance.getAmplifier(), (attribute, modifier) -> {
                    modifiers.add(new Pair<>(attribute, modifier));
                });
                if (mobEffectInstance.getAmplifier() > 0) {
                    mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + mobEffectInstance.getAmplifier()));
                }

                if (!mobEffectInstance.endsWithin(20)) {
                    mutablecomponent = Component.translatable("potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(mobEffectInstance, 1.0F, tickRate));
                }
                if (applyEffectsConsumeEffect.probability() < 1.0F) {
                    mutablecomponent = Component.translatable("foodeffecttooltips.food.withChance", mutablecomponent, Math.round(applyEffectsConsumeEffect.probability() * 100));
                }

                tooltip.add(mutablecomponent.withStyle(holder.value().getCategory().getTooltipFormatting()));
            }
        }

        if (!modifiers.isEmpty()) {
            tooltip.add(CommonComponents.EMPTY);
            if (drink) {
                tooltip.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
            } else {
                tooltip.add(Component.translatable("foodeffecttooltips.food.whenEaten").withStyle(ChatFormatting.DARK_PURPLE));
            }

            for (Pair<Holder<Attribute>, AttributeModifier> modifier : modifiers) {
                AttributeModifier attributeModifier = modifier.getSecond();
                double d = attributeModifier.amount();
                double e;
                if (attributeModifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && attributeModifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    e = attributeModifier.amount();
                } else {
                    e = attributeModifier.amount() * 100.0;
                }

                if (d > 0.0) {
                    tooltip.add(Component.translatable("attribute.modifier.plus." + attributeModifier.operation().id(),  ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable(modifier.getFirst().value().getDescriptionId())).withStyle(ChatFormatting.BLUE));
                } else if (d < 0.0) {
                    e *= -1.0;
                    tooltip.add(Component.translatable("attribute.modifier.take." + attributeModifier.operation().id(),  ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable(modifier.getFirst().value().getDescriptionId())).withStyle(ChatFormatting.RED));
                }
            }
        }
    }

}
