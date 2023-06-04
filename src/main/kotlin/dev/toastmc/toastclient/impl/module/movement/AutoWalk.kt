package dev.toastmc.toastclient.impl.module.movement

import dev.toastmc.toastclient.api.managers.module.Module

object AutoWalk : Module("AutoWalk", Category.MOVEMENT) {

    private var mode = mode("Mode", "Simple", "Simple"
//        , "Baritone"
    )
        .onChanged { mode1 ->
            if (isEnabled()) stop()
            direction.isHidden = mode1.value.equals("Baritone", true)
        }

    private var direction = mode("Direction", "Forward", "Forward", "Backward", "Leftward", "Rightward")
        .onChanged {
            if (isEnabled()) stop()
        }

    override fun onDisable() {
        if (mc.player != null) {
            if (mode.value.equals("Simple", true)) {
                stop()
            }
        }
    }

    override fun onUpdate() {
        if (mode.value.equals("Simple", true)) {
            when (direction.value) {
                "Forward" -> mc.options.forwardKey.isPressed = true
                "Backward" -> mc.options.backKey.isPressed = true
                "Leftward" -> mc.options.leftKey.isPressed = true
                "Rightward" -> mc.options.rightKey.isPressed = true
            }
        }
    }

    private fun stop() {
        mc.options.forwardKey.isPressed = false
        mc.options.backKey.isPressed = false
        mc.options.leftKey.isPressed = false
        mc.options.rightKey.isPressed = false
    }

}