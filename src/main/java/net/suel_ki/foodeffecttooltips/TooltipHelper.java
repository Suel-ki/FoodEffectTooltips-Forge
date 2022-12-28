package net.suel_ki.foodeffecttooltips;

import com.google.common.collect.Lists;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import net.minecraft.item.SuspiciousStewItem;
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
import net.suel_ki.foodeffecttooltips.config.FoodEffectsConfig;

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
            if (stack.getItem() instanceof SuspiciousStewItem)
                buildStewFoodEffectTooltip(tooltip, stack.getTag(), isDrink);
            else buildFoodEffectTooltip(tooltip, foodProperties.getEffects(), isDrink);
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


    public static void buildStewFoodEffectTooltip(List<ITextComponent> tooltip, CompoundNBT compoundNBT, boolean drink) {
        List<Pair<EffectInstance, Float>> effects = Lists.newArrayList();
        if (compoundNBT != null && compoundNBT.contains("Effects", 9)) {
            ListNBT listnbt = compoundNBT.getList("Effects", 10);

            for (int i = 0; i < listnbt.size(); ++i) {
                int duration = 160;
                CompoundNBT effectNbt = listnbt.getCompound(i);
                if (effectNbt.contains("EffectDuration", 3)) {
                    duration = effectNbt.getInt("EffectDuration");
                }

                Effect effect = Effect.byId(effectNbt.getByte("EffectId"));
                if (effect != null) {
                    effects.add(Pair.of(new EffectInstance(effect, duration), 1.0F));
                    buildFoodEffectTooltip(tooltip, effects, drink);
                }
            }
        }
    }
}
