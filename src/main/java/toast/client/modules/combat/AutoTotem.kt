package toast.client.modules.combat

import com.google.common.eventbus.Subscribe
import net.minecraft.container.SlotActionType
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import toast.client.events.network.EventSyncedUpdate
import toast.client.modules.Module

class AutoTotem : Module("AutoTotem", "Automatically places totem into offhand", Category.COMBAT, -1) {
    var totems = 0
    var totemsTotal = 0
    var moving = false
    var returning = false

    @Subscribe
    fun onUpdate(event: EventSyncedUpdate?) {
        if (mc.player == null) return
        totems = mc.player!!.inventory.main.stream().filter { itemStack: ItemStack -> itemStack.item === Items.TOTEM_OF_UNDYING }.mapToInt { obj: ItemStack -> obj.count }.sum()
        totemsTotal = totems + mc.player!!.inventory.offHand.stream().filter { itemStack: ItemStack -> itemStack.item === Items.TOTEM_OF_UNDYING }.mapToInt { obj: ItemStack -> obj.count }.sum()
        name = "AutoTotem: $totemsTotal"
        if (mc.player!!.offHandStack.item === Items.TOTEM_OF_UNDYING) totems++ else {
            if (!mc.player!!.inventory.offHand.isEmpty() && mc.player!!.inventory.offHand[0].item !== Items.TOTEM_OF_UNDYING && !settings.getBoolean("Force Totem")) return
            when {
                moving -> {
                    mc.interactionManager!!.clickSlot(0, 45, 0, SlotActionType.PICKUP, mc.player)
                    moving = false
                    if (!mc.player!!.inventory.isInvEmpty) returning = true
                    return
                }
                !mc.player!!.inventory.isInvEmpty -> {
                    if (totems == 0) return
                    val t = (0..44).firstOrNull { mc.player!!.inventory.getInvStack(it).item === Items.TOTEM_OF_UNDYING }
                            ?: -1
                    if (t == -1) return
                    mc.interactionManager!!.clickSlot(0, if (t < 9) t + 36 else t, 0, SlotActionType.PICKUP, mc.player)
                    moving = true
                }
            }
        }
    }

    init {
        settings.addBoolean("Force Totem", true)
    }
}