package dev.toastmc.toastclient.impl.module.movement

import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.util.math.Vec3d


object Strafe : Module("Strafe", Category.MOVEMENT) {

    var slowdown = false
    var water = bool("InWater", false)
    var autoSprint = bool("AutoSprint", true)

    override fun onUpdate() {
        if (mc.player == null || mc.player!!.isSneaking || mc.options.keyJump.isPressed) return

        if (mc.player!!.forwardSpeed != 0f || mc.player!!.sidewaysSpeed != 0f) {
            if (!mc.player!!.isSprinting && autoSprint.value)
                mc.player!!.networkHandler.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING))

            if (mc.player!!.isOnGround) {
                mc.player!!.velocity = Vec3d(0.0, mc.player!!.velocity.y, 0.0)
                mc.player!!.updateVelocity(0.31f, Vec3d(mc.player!!.sidewaysSpeed.toDouble(), 0.0, mc.player!!.forwardSpeed.toDouble()))
                mc.player!!.jump()
                slowdown = true
            } else if (mc.player!!.isInsideWaterOrBubbleColumn && water.value) {
                mc.player!!.velocity = Vec3d(0.0, mc.player!!.velocity.y, 0.0)
                mc.player!!.updateVelocity(0.275f, Vec3d(mc.player!!.sidewaysSpeed.toDouble(), 0.0, mc.player!!.forwardSpeed.toDouble()))
            }

            if (slowdown) {
                mc.player!!.velocity = Vec3d(0.0, mc.player!!.velocity.y, 0.0)
                mc.player!!.updateVelocity(0.27f, Vec3d(mc.player!!.sidewaysSpeed.toDouble(), 0.0, mc.player!!.forwardSpeed.toDouble()))
                slowdown = false
            }
        } else slowdown = false

        if (!mc.options.keyForward.isPressed &&
            !mc.options.keyBack.isPressed &&
            !mc.options.keyLeft.isPressed &&
            !mc.options.keyRight.isPressed
        ) mc.player!!.setVelocity(0.0, mc.player!!.velocity.y, 0.0)
    }
}