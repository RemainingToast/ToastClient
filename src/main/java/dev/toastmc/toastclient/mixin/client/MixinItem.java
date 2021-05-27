package dev.toastmc.toastclient.mixin.client;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public abstract class MixinItem {

    @Inject(method = "appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/client/item/TooltipContext;)V", at = @At("HEAD"))
    public void appendTooltipInject(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
//        if(stack.getItem() instanceof ToolItem) {
//            if(Screen.hasShiftDown()) {
//                ToolItem tool = (ToolItem)stack.getItem();
//                ToolMaterial material = tool.getMaterial();
//                if(tool instanceof MiningToolItem) {
//                    tooltip.add(lit("Harvest Level: " + material.getMiningLevel()).formatted(Formatting.GRAY));
//                    int efficiency = EnchantmentHelper.get(stack).getOrDefault(Enchantments.EFFICIENCY, 0);
//                    int efficiencyModifier = efficiency>0?(efficiency*efficiency)+1:0;
//                    MutableText speedText = lit("Harvest Speed: " + material.getMiningSpeedMultiplier()+efficiencyModifier).formatted(Formatting.GRAY);
//                    if(efficiency > 0) {
//                        speedText.append(lit(" (+"+efficiencyModifier+")").formatted(Formatting.WHITE));
//                    }
//                    tooltip.add(speedText);
//                }
//                tooltip.add(lit("Enchantability: " + material.getEnchantability()).formatted(Formatting.GRAY));
//                tooltip.add(lit("Max Durability: " + tool.getMaxDamage()).formatted(Formatting.GRAY));
//            } else {
//                tooltip.add(lit("Press SHIFT for stats").formatted(Formatting.GRAY));
//            }
//        } else if(stack.getItem() instanceof ArmorItem) {
//            if(Screen.hasShiftDown()) {
//                ArmorItem armor = (ArmorItem)stack.getItem();
//                ArmorMaterial material = armor.getMaterial();
//                tooltip.add(lit("Enchantability: " + material.getEnchantability()).formatted(Formatting.GRAY));
//                tooltip.add(lit("Max Durability: " + armor.getMaxDamage()).formatted(Formatting.GRAY));
//            } else {
//                tooltip.add(lit("Press SHIFT for stats").formatted(Formatting.GRAY));
//            }
//        } else if(stack.isDamageable()) {
//            if(Screen.hasShiftDown()) {
//                tooltip.add(lit("Max Durability: " + stack.getMaxDamage()).formatted(Formatting.GRAY));
//            } else {
//                tooltip.add(lit("Press SHIFT for stats").formatted(Formatting.GRAY));
//            }
//        }
    }
}
