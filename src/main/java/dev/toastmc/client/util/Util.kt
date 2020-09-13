package dev.toastmc.client.util

import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import java.io.File

val mc = MinecraftClient.getInstance()
val MOD_DIRECTORY: File = File(mc.runDirectory, "toastclient" + "/")

fun lit(string: String): LiteralText {
    return LiteralText(string)
}

fun getNeededYaw(vec: Vec3d): Float {
    return mc.player!!.yaw + MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec.z - mc.player!!.z, vec.x - mc.player!!.x)).toFloat() - 90f - mc.player!!.yaw)
}

fun getNeededPitch(vec: Vec3d): Float {
    val diffX: Double = vec.x - mc.player!!.x
    val diffY: Double = vec.y - (mc.player!!.y + mc.player!!.getEyeHeight(mc.player!!.pose))
    val diffZ: Double = vec.z - mc.player!!.z
    val diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ)
    return mc.player!!.pitch + MathHelper.wrapDegrees((-Math.toDegrees(Math.atan2(diffY, diffXZ))).toFloat() - mc.player!!.pitch)
}