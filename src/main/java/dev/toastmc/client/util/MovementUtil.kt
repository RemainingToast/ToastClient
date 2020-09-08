package dev.toastmc.client.util

import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookOnly
import net.minecraft.util.math.Vec3d
import kotlin.math.*

object MovementUtil {
    private val mc = MinecraftClient.getInstance()
    private fun lookPacket(yaw: Double, pitch: Double) {
        if (mc.player == null) return
        mc.player!!.networkHandler.sendPacket(
            LookOnly(
                yaw.toFloat(),
                pitch.toFloat(),
                mc.player!!.isOnGround
            )
        )
    }

    private fun lookClient(yaw: Double, pitch: Double) {
        if (mc.player == null) return
        mc.player!!.yaw = yaw.toFloat()
        mc.player!!.pitch = pitch.toFloat()
    }

    fun lookAt(point: Vec3d, packet: Boolean) {
        if (mc.player == null) return
        var lookx = mc.player!!.x - point.getX()
        var looky = mc.player!!.y - point.getY()
        var lookz = mc.player!!.z - point.getZ()
        val len = sqrt(lookx * lookx + looky * looky + lookz * lookz)
        lookx /= len
        looky /= len
        lookz /= len
        var pitch = asin(looky)
        var yaw = atan2(lookz, lookx)

        //to degree
        pitch = pitch * 180.0 / Math.PI
        yaw = yaw * 180.0 / Math.PI
        yaw += 90.0
        if (packet) {
            lookPacket(yaw, pitch)
        } else {
            lookClient(yaw, pitch)
        }
    }

    fun getMovementYaw(): Double {
        var strafe = 90f
        strafe += if(mc.player!!.input.movementForward != 0F) mc.player!!.input.movementForward * 0.5F else 1F
        var yaw = mc.player!!.yaw - strafe
        yaw -= if(mc.player!!.input.movementForward < 0F)180 else 0
        return Math.toRadians(yaw.toDouble())
    }


    fun getSpeed(): Double {
        return sqrt(mc.player!!.velocity.x.pow(2) + mc.player!!.velocity.z.pow(2))
    }

    fun setSpeed(speed: Double) {
        mc.player!!.setVelocity(-sin(getMovementYaw()) * speed, mc.player!!.velocity.y, cos(getMovementYaw()) * speed)
    }

}