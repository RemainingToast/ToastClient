package dev.toastmc.toastclient.api.util

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Saddleable
import net.minecraft.entity.mob.*
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.entity.vehicle.MinecartEntity
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.Vec3d
import kotlin.math.*

object EntityUtil {
    fun notSelf(e: Entity): Boolean {
        return e !== mc.player && e !== mc.cameraEntity
    }

    fun isAnimal(e: Entity?): Boolean {
        return e is AnimalEntity ||
                e is AmbientEntity ||
                e is WaterCreatureEntity ||
                e is GolemEntity && !e.handSwinging ||
                e is VillagerEntity
    }

    fun isLiving(e: Entity?): Boolean {
        return e is LivingEntity
    }

    fun isHostile(e: Entity?): Boolean {
        return e is HostileEntity && e !is PiglinEntity && e !is EndermanEntity ||
                e is PiglinEntity && e.isAngryAt(mc.player) ||
                e is WolfEntity && e.isAttacking && e.ownerUuid !== mc.player!!.uuid ||
                e is EndermanEntity && e.isAngry ||
                e is GolemEntity && e.handSwinging ||
                e is MobEntity && e.isAttacking
    }

    fun isNeutral(e: Entity?): Boolean {
        return e is PiglinEntity && !e.isAngryAt(mc.player) ||
                e is WolfEntity && (!e.isAttacking || e.ownerUuid === mc.player!!.uuid) ||
                e is EndermanEntity && !e.isAngry ||
                e is GolemEntity && !e.handSwinging
    }
    fun isVehicle(e: Entity?): Boolean {
        return e is BoatEntity ||
                e is MinecartEntity ||
                (e is Saddleable && e.isSaddled)
    }

    private fun lookPacket(yaw: Double, pitch: Double) {
        if (mc.player == null) return
        mc.player!!.networkHandler.sendPacket(
            PlayerMoveC2SPacket.LookOnly(
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