package toast.client.modules.movement

import com.google.common.eventbus.Subscribe
import toast.client.events.network.EventSyncedUpdate
import toast.client.modules.Module

class Sprint : Module("Sprint", "Automatically sprint", Category.MOVEMENT, -1) {
    @Subscribe
    fun onTick(event: EventSyncedUpdate?) {
        if (!enabled || mc.player == null) return
        mc.player!!.isSprinting = mc.player!!.input.movementForward > 0 && mc.player!!.input.movementSideways != 0f || mc.player!!.input.movementForward > 0 && !mc.player!!.isSneaking
    }
}