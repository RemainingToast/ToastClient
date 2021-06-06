package dev.toastmc.toastclient.impl.module.combat

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.InventoryUtil
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
        if (offhandTotem() || (!gui.value && mc.currentScreen != null)) return
        when {
            moving -> {
                InventoryUtil.pickupOffHand()
                moving = false
                if(!invEmpty) {
                    returning = true
                }
                return
            }
            !invEmpty -> {
                InventoryUtil.pickup(InventoryUtil.getSlotWithItem(Items.TOTEM_OF_UNDYING) ?: return)
                moving = true
            }
        }
    }

    private fun offhandTotem(): Boolean {
        return InventoryUtil.getOffHandStack().item === Items.TOTEM_OF_UNDYING
    }

}