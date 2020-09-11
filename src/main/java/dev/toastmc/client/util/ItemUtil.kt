package dev.toastmc.client.util

import dev.toastmc.client.mixin.client.IClientPlayerInteractionManager
import net.minecraft.block.BlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EntityGroup
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.MiningToolItem
import net.minecraft.item.SwordItem
import kotlin.math.pow
import dev.toastmc.client.ToastClient.Companion.MINECRAFT as mc

object ItemUtil {
    private val pickaxeList = listOf(
            Items.WOODEN_PICKAXE,
            Items.STONE_PICKAXE,
            Items.IRON_PICKAXE,
            Items.GOLDEN_PICKAXE,
            Items.DIAMOND_PICKAXE,
            Items.NETHERITE_PICKAXE
    )

    fun isPickaxe(item: Item): Boolean {
        return pickaxeList.contains(item)
    }

    fun equipBestTool(blockState: BlockState) {
        var bestSlot = -1
        var max = 0.0
        for (i in 0..8) {
            val stack = mc.player?.inventory?.getStack(i)
            if (stack != null) {
                if (stack.isEmpty) continue
            }
            var speed = stack?.getMiningSpeedMultiplier(blockState)
            var eff: Int
            if (speed != null) {
                if (speed > 1) {
                    speed += (if (EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack).also {
                            eff = it
                        } > 0) (eff.toDouble().pow(2.0) + 1) else 0.0).toFloat()
                    if (speed > max) {
                        max = speed.toDouble()
                        bestSlot = i
                    }
                }
            }
        }
        if (bestSlot != -1) equip(bestSlot)
    }

    fun equipBestWeapon(): Int {
        var bestSlot = -1
        var maxDamage = 0.0
        for (i in 0..8) {
            val stack = mc.player?.inventory?.getStack(i)
            if (stack != null) {
                if (stack.isEmpty) continue
            }
            if (stack != null) {
                if (stack.item is MiningToolItem || stack.item is SwordItem) {
                    // Not sure of the best way to cast stack.item as either SwordItem or MiningToolItem
                    val damage = if (stack.item is SwordItem) {
                        (stack.item as SwordItem).attackDamage + EnchantmentHelper.getAttackDamage(stack, EntityGroup.DEFAULT).toDouble()
                    } else {
                        (stack.item as MiningToolItem).attackDamage + EnchantmentHelper.getAttackDamage(stack, EntityGroup.DEFAULT).toDouble()
                    }
                    if (damage > maxDamage) {
                        maxDamage = damage
                        bestSlot = i
                    }
                }
            }
        }
        if (bestSlot != -1) equip(bestSlot)
        return bestSlot
    }

    fun equip(slot: Int) {
        mc.player?.inventory?.selectedSlot = slot
        (mc.interactionManager as IClientPlayerInteractionManager).invokeSyncSelectedSlot()
    }

}