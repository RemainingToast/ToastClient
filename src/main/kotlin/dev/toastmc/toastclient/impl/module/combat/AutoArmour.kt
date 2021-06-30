package dev.toastmc.toastclient.impl.module.combat

import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.ProtectionEnchantment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.*
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.screen.slot.SlotActionType

object AutoArmour : Module("AutoArmour", Category.COMBAT) {

    val elytra = bool("Elytra", false)
    var preventBreak = bool("PreventBreak", true)

    override fun onUpdate() {
        val armorMap = HashMap<EquipmentSlot, IntArray>(4)
        armorMap[EquipmentSlot.FEET] = intArrayOf(36, getProt(mc.player!!.inventory.getStack(36)), -1, -1)
        armorMap[EquipmentSlot.LEGS] = intArrayOf(37, getProt(mc.player!!.inventory.getStack(37)), -1, -1)
        armorMap[EquipmentSlot.CHEST] = intArrayOf(38, getProt(mc.player!!.inventory.getStack(38)), -1, -1)
        armorMap[EquipmentSlot.HEAD] = intArrayOf(39, getProt(mc.player!!.inventory.getStack(39)), -1, -1)
        when {
            preventBreak.value -> {
                armorMap.toList().parallelStream().forEach { (_, value) ->
                    val `is` = mc.player!!.inventory.getStack(value[0])
                    val armorSlot = value[0] - 34 + (39 - value[0]) * 2
                    if (`is`.isDamageable && `is`.maxDamage - `is`.damage < 7) {
                        var forceMoveSlot = -1
                        for (s in 0..35) {
                            if (mc.player!!.inventory.getStack(s).isEmpty) {
                                mc.interactionManager!!.clickSlot(
                                    mc.player!!.currentScreenHandler.syncId,
                                    armorSlot,
                                    1,
                                    SlotActionType.QUICK_MOVE,
                                    mc.player
                                )
                                return@forEach
                            } else if (mc.player!!.inventory.getStack(s).item !is ToolItem
                                && mc.player!!.inventory.getStack(s).item !is ArmorItem
                                && mc.player!!.inventory.getStack(s).item !is ElytraItem
                                && mc.player!!.inventory.getStack(s).item !== Items.TOTEM_OF_UNDYING && forceMoveSlot == -1
                            ) {
                                forceMoveSlot = s
                            }
                        }
                        if (forceMoveSlot != -1) {
                            mc.interactionManager!!.clickSlot(mc.player!!.currentScreenHandler.syncId, if (forceMoveSlot < 9) 36 + forceMoveSlot else forceMoveSlot, 1, SlotActionType.THROW, mc.player)
                            mc.interactionManager!!.clickSlot(mc.player!!.currentScreenHandler.syncId, armorSlot, 1, SlotActionType.QUICK_MOVE, mc.player)
                            return@forEach
                        }
                        mc.interactionManager!!.clickSlot(mc.player!!.currentScreenHandler.syncId, armorSlot, 1, SlotActionType.THROW, mc.player)
                        return@forEach
                    }
                }
            }
        }

        for (s in 0..35) {
            val prot: Int = getProt(mc.player!!.inventory.getStack(s))
            if (prot > 0) {
                val slot = if (mc.player!!.inventory.getStack(s).item is ElytraItem) EquipmentSlot.CHEST else (mc.player!!.inventory.getStack(s).item as ArmorItem).slotType
                for ((key1, value) in armorMap) {
                    if (key1 == slot) {
                        if (prot > value[1] && prot > value[3]) {
                            value[2] = s
                            value[3] = prot
                        }
                    }
                }
            }
        }

        armorMap.toList().parallelStream().forEach { (_, value) ->
            if (value[2] != -1) {
                if (value[1] == -1 && value[2] < 9) {
                    if (value[2] != mc.player!!.inventory.selectedSlot) {
                        mc.player!!.inventory.selectedSlot = value[2]
                        mc.player!!.networkHandler.sendPacket(UpdateSelectedSlotC2SPacket(value[2]))
                    }
                    mc.interactionManager!!.clickSlot(mc.player!!.currentScreenHandler.syncId, 36 + value[2], 1, SlotActionType.QUICK_MOVE, mc.player)
                } else if (mc.player!!.playerScreenHandler === mc.player!!.currentScreenHandler) {
                    /* Convert inventory slots to container slots */
                    val armorSlot = value[0] - 34 + (39 - value[0]) * 2
                    val newArmorslot = if (value[2] < 9) 36 + value[2] else value[2]
                    mc.interactionManager!!.clickSlot(mc.player!!.currentScreenHandler.syncId, newArmorslot, 0, SlotActionType.PICKUP, mc.player)
                    mc.interactionManager!!.clickSlot(mc.player!!.currentScreenHandler.syncId, armorSlot, 0, SlotActionType.PICKUP, mc.player)
                    if (value[1] != -1) mc.interactionManager!!.clickSlot(mc.player!!.currentScreenHandler.syncId, newArmorslot, 0, SlotActionType.PICKUP, mc.player)
                }
                return@forEach
            }
        }
    }

    private fun getProt(`is`: ItemStack): Int {
        if (`is`.item is ArmorItem || `is`.item === Items.ELYTRA) {
            var prot = 0
            if (`is`.item is ElytraItem) {
                if (!ElytraItem.isUsable(`is`)) return 0
                prot = if (elytra.value) 32767 else 1
            } else if (`is`.maxDamage - `is`.damage < 7 && preventBreak.value) {
                return 0
            }
            if (`is`.hasEnchantments()) {
                for ((key1, value) in EnchantmentHelper.get(`is`)) {
                    if (key1 is ProtectionEnchantment) prot += value
                }
            }
            return (if (`is`.item is ArmorItem) (`is`.item as ArmorItem).protection else 0) + prot
        } else if (!`is`.isEmpty) {
            return 0
        }
        return -1
    }

}