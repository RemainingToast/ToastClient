package dev.toastmc.toastclient.impl.module.combat

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.entity.totemCount
import net.minecraft.item.Items
import net.minecraft.screen.slot.SlotActionType

object AutoTotem : Module("AutoTotem", Category.COMBAT) {

    private val gui = bool("WorkInGui", true)
    private var moving = false
    private var returning = false

    private val invEmpty: Boolean
        get() {
            return mc.player!!.inventory.isEmpty
        }

    override fun onUpdate() {
        if (offhandTotem() || (!gui.value && mc.currentScreen != null)) return
        when {
            moving -> {
                mc.interactionManager!!.clickSlot(0, 45, 0, SlotActionType.PICKUP, mc.player)
                moving = false
                if(!invEmpty) {
                    returning = true
                }
                return
            }
            !invEmpty -> {
                if (mc.player!!.totemCount() != 0) {
                    val totem = (0..44).firstOrNull { mc.player!!.inventory.getStack(it).item === Items.TOTEM_OF_UNDYING } ?: return
                    mc.interactionManager!!.clickSlot(0, if (totem < 9) totem + 36 else totem, 0, SlotActionType.PICKUP, mc.player)
                    moving = true
                }
            }
        }
    }

    private fun offhandTotem(): Boolean {
        return mc.player!!.inventory.offHand.first().item === Items.TOTEM_OF_UNDYING
    }

}