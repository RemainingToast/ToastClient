package dev.toastmc.toastclient.impl.module.movement

import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.util.math.Vec3d

object Strafe : Module("Strafe", Category.MOVEMENT) {

    var slowdown = false
    var speed = number("Speed", 310.0, 1.0, 500.0, 1)
    var water = bool("InWater", false)
    var lava = bool("InLava", false)
    var autoSprint = bool("AutoSprint", true)

    override fun onUpdate() {
        if (mc.player!!.isSneaking || mc.options.jumpKey.isPressed) return

        if (mc.player!!.forwardSpeed != 0f || mc.player!!.sidewaysSpeed != 0f) {
            if (!mc.player!!.isSprinting && autoSprint.value && (!Sprint.isEnabled() && Sprint.mode.equals("Rage")))
                mc.player!!.networkHandler.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING))

            if (mc.player!!.isOnGround) {
                mc.player!!.velocity = Vec3d(0.0, mc.player!!.velocity.y, 0.0)
                mc.player!!.updateVelocity(speed.floatValue / 1000.0f, Vec3d(mc.player!!.sidewaysSpeed.toDouble(), 0.0, mc.player!!.forwardSpeed.toDouble()))
                mc.player!!.jump()
                slowdown = true
            } else if (mc.player!!.isInsideWaterOrBubbleColumn && water.value) {
                mc.player!!.velocity = Vec3d(0.0, mc.player!!.velocity.y, 0.0)
                mc.player!!.updateVelocity(0.275f, Vec3d(mc.player!!.sidewaysSpeed.toDouble(), 0.0, mc.player!!.forwardSpeed.toDouble()))
            } else if (mc.player!!.isInLava && lava.value) {
                mc.player!!.velocity = Vec3d(0.0, mc.player!!.velocity.y, 0.0)
                mc.player!!.updateVelocity(0.273f, Vec3d(mc.player!!.sidewaysSpeed.toDouble(), 0.0, mc.player!!.forwardSpeed.toDouble()))
            }

            if (slowdown) {
                mc.player!!.velocity = Vec3d(0.0, mc.player!!.velocity.y, 0.0)
                mc.player!!.updateVelocity((speed.floatValue / 1000.0f) - 0.4f, Vec3d(mc.player!!.sidewaysSpeed.toDouble(), 0.0, mc.player!!.forwardSpeed.toDouble()))
                slowdown = false
            }
        } else slowdown = false

        if (!mc.options.forwardKey.isPressed &&
            !mc.options.backKey.isPressed &&
            !mc.options.leftKey.isPressed &&
            !mc.options.rightKey.isPressed
        ) mc.player!!.setVelocity(0.0, mc.player!!.velocity.y, 0.0)
    }
}