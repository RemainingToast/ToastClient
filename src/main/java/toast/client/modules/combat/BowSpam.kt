package toast.client.modules.combat

import com.google.common.eventbus.Subscribe
import net.minecraft.item.BowItem
import net.minecraft.util.Identifier
import toast.client.events.player.EventUpdate
import toast.client.modules.Module

class BowSpam : Module("BowSpam", "Makes your bow fully automatic.", Category.COMBAT, -1) {
    var ticksLeft = -1

    @Subscribe
    fun onUpdate(event: EventUpdate?) {
        if (mc.player == null || mc.world == null || mc.player!!.mainHandStack.item !is BowItem || mc.player!!.mainHandStack.item.getPropertyGetter(Identifier("pulling"))!!.call(mc.player!!.mainHandStack, mc.world, mc.player) == 0.0f) {
            ticksLeft = -1
            return
        }
        if (ticksLeft != -1) ticksLeft--
        else {
            when (mode) {
                "No Charge" -> ticksLeft = 2
                "Medium Charge" -> ticksLeft = 4
                "Full Charge" -> ticksLeft = 20
            }
        }
        if (ticksLeft == 0) {
            mc.interactionManager!!.stopUsingItem(mc.player)
            ticksLeft = -1
        }
    }

    init {
        settings.addMode("Mode", "No Charge", "No Charge", "Medium Charge", "Full Charge")
    }
}