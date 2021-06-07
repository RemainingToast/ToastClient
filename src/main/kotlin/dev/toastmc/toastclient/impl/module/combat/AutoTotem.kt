package dev.toastmc.toastclient.impl.module.combat

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.InventoryUtil.getSlotWithItem
import dev.toastmc.toastclient.api.util.InventoryUtil.pickup
import dev.toastmc.toastclient.api.util.InventoryUtil.pickupOffHand
import net.minecraft.item.Items

object  AutoTotem : Module("AutoTotem", Category.COMBAT) {

    private val gui = bool("WorkInGui", true)
    private var moving = false
    private var returning = false

    private val invEmpty: Boolean
        get() {
            return mc.player!!.inventory.isEmpty
        }

    override fun onUpdate() {
        if (!gui.value && mc.currentScreen != null) return
        if (mc.player!!.offHandStack.item != Items.TOTEM_OF_UNDYING) {
            when {
                moving -> {
                    mc.player!!.pickupOffHand()
                    moving = false
                    return
                }
                !invEmpty -> {
                    mc.player!!.pickup(mc.player!!.getSlotWithItem(Items.TOTEM_OF_UNDYING) ?: return)
                    moving = true
                    returning = true
                }
            }
        }
    }
}