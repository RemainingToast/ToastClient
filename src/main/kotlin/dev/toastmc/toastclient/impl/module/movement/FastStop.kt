package dev.toastmc.toastclient.impl.module.movement

import dev.toastmc.toastclient.api.managers.module.Module

object FastStop : Module("FastStop", Category.MOVEMENT){

    var airStop = bool("Air", true)

    override fun onUpdate() {
        if (!mc.options.forwardKey.isPressed &&
            !mc.options.backKey.isPressed &&
            !mc.options.leftKey.isPressed &&
            !mc.options.rightKey.isPressed
        ) {
            if (mc.player!!.isOnGround || airStop.value) {
                mc.player!!.setVelocity(0.0, mc.player!!.velocity.y, 0.0)
            }
        }
    }

}