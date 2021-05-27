package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.impl.module.misc.ExtraTooltips;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static dev.toastmc.toastclient.api.util.UtilKt.lit;

@Mixin(Item.class)
public abstract class MixinItem {

    @Inject(
            at = {@At("RETURN")},
            method = {"appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V"}
    )
    private void on(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if(ExtraTooltips.INSTANCE.isEnabled()) {
            if (stack.getItem() instanceof ToolItem) {
                if (Screen.hasShiftDown()) {
                    ToolItem tool = (ToolItem) stack.getItem();
                    ToolMaterial material = tool.getMaterial();
                    if (tool instanceof MiningToolItem) {
                        tooltip.add(lit("Harvest Level: " + Formatting.GREEN + material.getMiningLevel()).formatted(Formatting.GRAY));
                        int efficiency = EnchantmentHelper.get(stack).getOrDefault(Enchantments.EFFICIENCY, 0);
                        int efficiencyModifier = efficiency > 0 ? (efficiency * efficiency) + 1 : 0;
                        MutableText speedText = lit("Harvest Speed: " + Formatting.GREEN + material.getMiningSpeedMultiplier() + efficiencyModifier).formatted(Formatting.GRAY);
                        if (efficiency > 0) {
                            speedText.append(lit(" (+" + efficiencyModifier + ")").formatted(Formatting.DARK_GREEN));
                        }
                        tooltip.add(speedText);
                    }
                    tooltip.add(lit("Enchantability: " + Formatting.GREEN + material.getEnchantability()).formatted(Formatting.GRAY));
                    tooltip.add(lit("Max Durability: " + Formatting.GREEN + tool.getMaxDamage()).formatted(Formatting.GRAY));
                } else {
                    tooltip.add(lit(Formatting.GRAY + "Press " + Formatting.GREEN + "SHIFT" + Formatting.GRAY + " for stats"));
                }
            } else if (stack.getItem() instanceof ArmorItem) {
                if (Screen.hasShiftDown()) {
                    ArmorItem armor = (ArmorItem) stack.getItem();
                    ArmorMaterial material = armor.getMaterial();
                    tooltip.add(lit("Enchantability: " + Formatting.GREEN + material.getEnchantability()).formatted(Formatting.GRAY));
                    tooltip.add(lit("Max Durability: " + Formatting.GREEN + armor.getMaxDamage()).formatted(Formatting.GRAY));
                } else {
                    tooltip.add(lit(Formatting.GRAY + "Press " + Formatting.GREEN + "SHIFT" + Formatting.GRAY + " for stats"));
                }
            } else if (stack.isDamageable()) {
                if (Screen.hasShiftDown()) {
                    tooltip.add(lit("Max Durability: " + Formatting.GREEN + stack.getMaxDamage()).formatted(Formatting.GRAY));
                } else {
                    tooltip.add(lit(Formatting.GRAY + "Press " + Formatting.GREEN + "SHIFT" + Formatting.GRAY + " for stats"));
                }
            }
        }
    }
}
