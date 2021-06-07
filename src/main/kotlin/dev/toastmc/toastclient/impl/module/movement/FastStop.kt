package dev.toastmc.toastclient.impl.module.movement

import dev.toastmc.toastclient.api.managers.module.Module

object FastStop : Module("FastStop", Category.MOVEMENT){

    var airStop = bool("Air", true)

    override fun onUpdate() {
        if (!mc.options.keyForward.isPressed &&
            !mc.options.keyBack.isPressed &&
            !mc.options.keyLeft.isPressed &&
            !mc.options.keyRight.isPressed
        ) {
            if (mc.player!!.isOnGround || airStop.value) {
                mc.player!!.setVelocity(0.0, mc.player!!.velocity.y, 0.0)
            }
        }
    }

}