package dev.toastmc.toastclient.impl.module.movement

import dev.toastmc.toastclient.api.managers.module.Module

object Sprint : Module("Sprint", Category.MOVEMENT) {

    override fun onUpdate() {
        if (mc.player == null) return
        mc.player!!.isSprinting = mc.player!!.input.movementForward > 0 &&
                mc.player!!.input.movementSideways != 0f ||
                mc.player!!.input.movementForward > 0 && !mc.player!!.isSneaking
    }

}