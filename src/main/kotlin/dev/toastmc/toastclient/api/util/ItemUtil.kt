package dev.toastmc.toastclient.api.util

import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EntityGroup
import net.minecraft.item.MiningToolItem
import net.minecraft.item.SwordItem

object ItemUtil {

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
                        (stack.item as SwordItem).attackDamage + EnchantmentHelper.getAttackDamage(
                            stack,
                            EntityGroup.DEFAULT
                        ).toDouble()
                    } else {
                        (stack.item as MiningToolItem).attackDamage + EnchantmentHelper.getAttackDamage(
                            stack,
                            EntityGroup.DEFAULT
                        ).toDouble()
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
//        (mc.interactionManager as IClientPlayerInteractionManager).invokeSyncSelectedSlot()
    }
}