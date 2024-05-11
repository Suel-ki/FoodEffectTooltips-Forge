package io.github.suel_ki.foodeffecttooltips;

import com.google.common.collect.Lists;
import io.github.suel_ki.foodeffecttooltips.config.FoodEffectsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.SuspiciousStewEffects;

import java.util.ArrayList;
import java.util.Iterator;
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

    public static void addFoodComponentEffectTooltip(ItemStack stack, List<Component> tooltip, float tickRate) {
        FoodProperties foodProperties = stack.get(DataComponents.FOOD);
        if(foodProperties != null) {
            boolean isDrink = stack.getUseAnimation() == UseAnim.DRINK;
            buildFoodEffectTooltip(tooltip, foodProperties.effects(), tickRate, isDrink);
        }
    }

    public static void buildFoodEffectTooltip(List<Component> tooltip, List<FoodProperties.PossibleEffect> effectsWithChance, float tickRate, boolean drink) {
        if(effectsWithChance.isEmpty()) {
            return;
        }

        List<Pair<Holder<Attribute>, AttributeModifier>> modifiersList = Lists.newArrayList();
        MutableComponent translatableComponent;
        MobEffect mobEffect;
        for(Iterator<FoodProperties.PossibleEffect> var5 = effectsWithChance.iterator(); var5.hasNext(); tooltip.add(translatableComponent.withStyle(mobEffect.getCategory().getTooltipFormatting()))) {
            FoodProperties.PossibleEffect entry = var5.next();
            MobEffectInstance mobEffectInstance = entry.effect();
            float chance = entry.probability();

            translatableComponent = Component.translatable(mobEffectInstance.getDescriptionId());
            mobEffect = mobEffectInstance.getEffect().value();

            mobEffect.createModifiers(mobEffectInstance.getAmplifier(), (attributeHolder, attributeModifier) -> {
                modifiersList.add(new Pair<>(attributeHolder, attributeModifier));
            });

            if (mobEffectInstance.getAmplifier() > 0) {
                translatableComponent = Component.translatable("potion.withAmplifier", translatableComponent, Component.translatable("potion.potency." + mobEffectInstance.getAmplifier()));
            }
            if (mobEffectInstance.getDuration() > 20) {
                translatableComponent = Component.translatable("potion.withDuration", translatableComponent, StringUtil.formatTickDuration(mobEffectInstance.getDuration(), tickRate));
            }
            if(chance < 1.0F) {
                translatableComponent = Component.translatable("foodeffecttooltips.food.withChance", translatableComponent, Math.round(chance * 100));
            }
        }

        if (!modifiersList.isEmpty()) {
            tooltip.add(CommonComponents.EMPTY);
            if(drink) {
                tooltip.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
            } else {
                tooltip.add(Component.translatable("foodeffecttooltips.food.whenEaten").withStyle(ChatFormatting.DARK_PURPLE));
            }

            for (Pair<Holder<Attribute>, AttributeModifier> entityAttributeEntityAttributeModifierPair : modifiersList) {
                AttributeModifier entityAttributeModifier3 = entityAttributeEntityAttributeModifierPair.getSecond();
                double d = entityAttributeModifier3.amount();
                double e;
                if (entityAttributeModifier3.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && entityAttributeModifier3.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    e = entityAttributeModifier3.amount();
                } else {
                    e = entityAttributeModifier3.amount() * 100.0D;
                }

                if (d > 0.0D) {
                    tooltip.add((Component.translatable("attribute.modifier.plus." + entityAttributeModifier3.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable((entityAttributeEntityAttributeModifierPair.getFirst()).value().getDescriptionId()))).withStyle(ChatFormatting.BLUE));
                } else if (d < 0.0D) {
                    e *= -1.0D;
                    tooltip.add((Component.translatable("attribute.modifier.take." + entityAttributeModifier3.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable((entityAttributeEntityAttributeModifierPair.getFirst()).value().getDescriptionId()))).withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    public static List<MobEffectInstance> getStewEffects(ItemStack stew) {
        List<MobEffectInstance> effects = new ArrayList<>();
        SuspiciousStewEffects stewEffects = stew.getOrDefault(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY);

        for (SuspiciousStewEffects.Entry effect : stewEffects.effects()) {
            effects.add(effect.createEffectInstance());
        }

        return effects;
    }

}
