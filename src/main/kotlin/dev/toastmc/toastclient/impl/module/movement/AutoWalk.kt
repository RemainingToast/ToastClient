package dev.toastmc.toastclient.impl.module.movement

import dev.toastmc.toastclient.api.module.Module

object AutoWalk : Module("AutoWalk", Category.MOVEMENT) {

    private var mode = mode("Mode", "Simple", "Simple", "Baritone")
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
                "Forward" -> mc.options.keyForward.isPressed = true
                "Backward" -> mc.options.keyBack.isPressed = true
                "Leftward" -> mc.options.keyLeft.isPressed = true
                "Rightward" -> mc.options.keyRight.isPressed = true
            }
        }
    }

    private fun stop() {
        mc.options.keyForward.isPressed = false
        mc.options.keyBack.isPressed = false
        mc.options.keyLeft.isPressed = false
        mc.options.keyRight.isPressed = false
    }

}