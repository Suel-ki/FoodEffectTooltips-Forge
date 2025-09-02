package io.github.suel_ki.foodeffecttooltips;

import com.google.common.collect.Lists;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import io.github.suel_ki.foodeffecttooltips.config.FoodEffectsConfig;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TooltipHelper {

    public static boolean shouldShowTooltip(ItemStack stack) {
        Item item = stack.getItem();
        ResourceLocation id = Registry.ITEM.getKey(item);

        boolean isWhitelist = FoodEffectsConfig.UseAsWhitelistInstead.get();
        if (FoodEffectsConfig.BlacklistedItemIdentifiers.get().contains(id.toString())) {
            return isWhitelist;
        }
        if (FoodEffectsConfig.BlacklistedModsIDs.get().contains(id.getNamespace())) {
            return isWhitelist;
        }
        return !isWhitelist;
    }

    public static void addFoodComponentEffectTooltip(ItemStack stack, List<ITextComponent> tooltip) {
        Food foodProperties = stack.getItem().getFoodProperties();
        if (foodProperties != null) {
            boolean isDrink = stack.getUseAnimation() == UseAction.DRINK;
           buildFoodEffectTooltip(tooltip, foodProperties.getEffects(), isDrink);
        }
    }

    public static void buildFoodEffectTooltip(List<ITextComponent> tooltip, List<Pair<EffectInstance, Float>> effectsWithChance, boolean drink) {
        if (effectsWithChance.isEmpty()) {
            return;
        }
        List<Pair<Attribute, AttributeModifier>> modifiersList = Lists.newArrayList();
        TranslationTextComponent translatableComponent;
        Effect mobEffect;
        for (Iterator<Pair<EffectInstance, Float>> var5 = effectsWithChance.iterator(); var5.hasNext(); tooltip.add(translatableComponent.withStyle(mobEffect.getCategory().getTooltipFormatting()))) {
            Pair<EffectInstance, Float> entry = var5.next();
            EffectInstance mobEffectInstance = entry.getFirst();
            Float chance = entry.getSecond();

            translatableComponent = new TranslationTextComponent(mobEffectInstance.getDescriptionId());
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
                translatableComponent = new TranslationTextComponent("potion.withAmplifier", translatableComponent, new TranslationTextComponent("potion.potency." + mobEffectInstance.getAmplifier()));
            }
            if (mobEffectInstance.getDuration() > 20) {
                translatableComponent = new TranslationTextComponent("potion.withDuration", translatableComponent, StringUtils.formatTickDuration(mobEffectInstance.getDuration()));
            }
            if (chance < 1.0F) {
                translatableComponent = new TranslationTextComponent("foodeffecttooltips.food.withChance", translatableComponent, Math.round(chance * 100));
            }
        }

        if (!modifiersList.isEmpty()) {
            tooltip.add(StringTextComponent.EMPTY);
            if (drink) {
                tooltip.add(new TranslationTextComponent("potion.whenDrank").withStyle(TextFormatting.DARK_PURPLE));
            } else {
                tooltip.add(new TranslationTextComponent("foodeffecttooltips.food.whenEaten").withStyle(TextFormatting.DARK_PURPLE));
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
                    tooltip.add((new TranslationTextComponent("attribute.modifier.plus." + entityAttributeModifier3.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(e), new TranslationTextComponent((entityAttributeEntityAttributeModifierPair.getFirst()).getDescriptionId()))).withStyle(TextFormatting.BLUE));
                } else if (d < 0.0D) {
                    e *= -1.0D;
                    tooltip.add((new TranslationTextComponent("attribute.modifier.take." + entityAttributeModifier3.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(e), new TranslationTextComponent((entityAttributeEntityAttributeModifierPair.getFirst()).getDescriptionId()))).withStyle(TextFormatting.RED));
                }
            }
        }
    }

    public static List<EffectInstance> getStewEffects(ItemStack stew) {
        List<EffectInstance> effects = new ArrayList<>();
        CompoundNBT compoundTag = stew.getTag();
        if (compoundTag != null && compoundTag.contains("Effects", Constants.NBT.TAG_LIST)) {
            ListNBT listtag = compoundTag.getList("Effects", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < listtag.size(); ++i) {
                CompoundNBT effectTag = listtag.getCompound(i);
                int duration = 160;
                if (effectTag.contains("EffectDuration", Constants.NBT.TAG_INT)) {
                    duration = effectTag.getInt("EffectDuration");
                }

                Effect mobeffect = Effect.byId(effectTag.getByte("EffectId"));
                if (mobeffect != null) {
                    effects.add(new EffectInstance(mobeffect, duration));
                }
            }
        }
        return effects;
    }
}
