package toast.client.modules.movement

import com.google.common.eventbus.Subscribe
import toast.client.events.network.EventSyncedUpdate
import toast.client.modules.Module

class FastStop : Module("Fast Stop", "Brings you to a stop instantly.", Category.MOVEMENT, -1) {
    @Subscribe
    fun onUpdate(event: EventSyncedUpdate?) {
        if (mc.player == null) return
        if (!mc.options.keyForward.isPressed && !mc.options.keyBack.isPressed && !mc.options.keyLeft.isPressed && !mc.options.keyRight.isPressed) when (mc.player!!.onGround) {
            true -> mc.player!!.setVelocity(0.0, mc.player!!.velocity.y, 0.0)
            !getBool("Air Stop") -> mc.player!!.setVelocity(0.0, mc.player!!.velocity.y, 0.0)
        }
    }

    init {
        settings.addBoolean("Air Stop", false)
    }
}