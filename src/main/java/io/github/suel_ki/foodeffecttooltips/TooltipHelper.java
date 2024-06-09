package io.github.suel_ki.foodeffecttooltips;

import com.google.common.collect.Lists;
import io.github.suel_ki.foodeffecttooltips.config.FoodEffectsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.UseAnim;

import com.mojang.datafixers.util.Pair;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TooltipHelper {

    public static boolean shouldShowTooltip(ItemStack stack) {
        Item item = stack.getItem();

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);

        boolean isWhitelist = FoodEffectsConfig.UseAsWhitelistInstead.get();
        if(FoodEffectsConfig.BlacklistedItemIdentifiers.get().contains(id.toString())) {
            return isWhitelist;
        }
        if(FoodEffectsConfig.BlacklistedModsIDs.get().contains(id.getNamespace())) {
            return isWhitelist;
        }
        return !isWhitelist;
    }

    public static void addFoodComponentEffectTooltip(ItemStack stack, List<Component> tooltip) {
        FoodProperties foodProperties = stack.getItem().getFoodProperties();
        if(foodProperties != null) {
            boolean isDrink = stack.getUseAnimation() == UseAnim.DRINK;
            buildFoodEffectTooltip(tooltip, foodProperties.getEffects(), isDrink);
        }
    }

    public static void buildFoodEffectTooltip(List<Component> tooltip, List<Pair<MobEffectInstance, Float>> effectsWithChance, boolean drink) {
        if(effectsWithChance.isEmpty()) {
            return;
        }

        List<Pair<Attribute, AttributeModifier>> modifiersList = Lists.newArrayList();
        MutableComponent translatableComponent;
        MobEffect mobEffect;
        for(Iterator<Pair<MobEffectInstance, Float>> var5 = effectsWithChance.iterator(); var5.hasNext(); tooltip.add(translatableComponent.withStyle(mobEffect.getCategory().getTooltipFormatting()))) {
            Pair<MobEffectInstance, Float> entry = var5.next();
            MobEffectInstance mobEffectInstance = entry.getFirst();
            Float chance = entry.getSecond();

            translatableComponent = Component.translatable(mobEffectInstance.getDescriptionId());
            mobEffect = mobEffectInstance.getEffect();

            Map<Attribute, AttributeModifier> map = mobEffect.getAttributeModifiers();
            if (!map.isEmpty()) {
                for (Map.Entry<Attribute, AttributeModifier> entityAttributeEntityAttributeModifierEntry : map.entrySet()) {
                    AttributeModifier entityAttributeModifier = entityAttributeEntityAttributeModifierEntry.getValue();
                    AttributeModifier entityAttributeModifier2 = new AttributeModifier(entityAttributeModifier.getName(), mobEffect.getAttributeModifierValue(mobEffectInstance.getAmplifier(), entityAttributeModifier), entityAttributeModifier.getOperation());
                    modifiersList.add(new Pair<>(entityAttributeEntityAttributeModifierEntry.getKey(), entityAttributeModifier2));
                }
            }

            if (mobEffectInstance.getAmplifier() > 0) {
                translatableComponent = Component.translatable("potion.withAmplifier", translatableComponent, Component.translatable("potion.potency." + mobEffectInstance.getAmplifier()));
            }
            if (mobEffectInstance.getDuration() > 20) {
                translatableComponent = Component.translatable("potion.withDuration", translatableComponent, StringUtil.formatTickDuration(mobEffectInstance.getDuration()));
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

            for (Pair<Attribute, AttributeModifier> entityAttributeEntityAttributeModifierPair : modifiersList) {
                AttributeModifier entityAttributeModifier3 = entityAttributeEntityAttributeModifierPair.getSecond();
                double d = entityAttributeModifier3.getAmount();
                double e;
                if (entityAttributeModifier3.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && entityAttributeModifier3.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    e = entityAttributeModifier3.getAmount();
                } else {
                    e = entityAttributeModifier3.getAmount() * 100.0D;
                }

                if (d > 0.0D) {
                    tooltip.add((Component.translatable("attribute.modifier.plus." + entityAttributeModifier3.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable((entityAttributeEntityAttributeModifierPair.getFirst()).getDescriptionId()))).withStyle(ChatFormatting.BLUE));
                } else if (d < 0.0D) {
                    e *= -1.0D;
                    tooltip.add((Component.translatable("attribute.modifier.take." + entityAttributeModifier3.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable((entityAttributeEntityAttributeModifierPair.getFirst()).getDescriptionId()))).withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    public static List<MobEffectInstance> getStewEffects(ItemStack stew) {
        List<MobEffectInstance> effects = new ArrayList<>();
        CompoundTag compoundTag = stew.getTag();
        if (compoundTag != null && compoundTag.contains(SuspiciousStewItem.EFFECTS_TAG, Tag.TAG_LIST)) {
            ListTag listtag = compoundTag.getList(SuspiciousStewItem.EFFECTS_TAG, Tag.TAG_COMPOUND);

            for (int i = 0; i < listtag.size(); ++i) {
                CompoundTag effectTag = listtag.getCompound(i);
                int duration = 160;
                if (effectTag.contains(SuspiciousStewItem.EFFECT_DURATION_TAG, Tag.TAG_INT)) {
                    duration = effectTag.getInt(SuspiciousStewItem.EFFECT_DURATION_TAG);
                }

                MobEffect mobeffect = MobEffect.byId(effectTag.getByte(SuspiciousStewItem.EFFECT_ID_TAG));
                mobeffect = net.minecraftforge.common.ForgeHooks.loadMobEffect(effectTag, "forge:effect_id", mobeffect);
                if (mobeffect != null) {
                    effects.add(new MobEffectInstance(mobeffect, duration));
                }
            }
        }
        return effects;
    }

}