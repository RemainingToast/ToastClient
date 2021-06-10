package dev.toastmc.toastclient.api.util.entity

import dev.toastmc.toastclient.api.util.mc
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Saddleable
import net.minecraft.entity.mob.*
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.entity.vehicle.MinecartEntity
import net.minecraft.util.math.Vec3d

object EntityUtil {
    fun isSelf(e: Entity): Boolean {
        return e == mc.player && e == mc.cameraEntity
    }

    fun isAnimal(e: Entity): Boolean {
        return e is AnimalEntity ||
                e is AmbientEntity ||
                e is WaterCreatureEntity ||
                e is GolemEntity && !e.handSwinging ||
                e is VillagerEntity
    }

    fun isLiving(e: Entity): Boolean {
        return e is LivingEntity
    }

    fun isHostile(e: Entity): Boolean {
        return e is HostileEntity && e !is PiglinEntity && e !is EndermanEntity ||
                e is PiglinEntity && e.isAngryAt(mc.player) ||
                e is WolfEntity && e.isAttacking && e.ownerUuid !== mc.player!!.uuid ||
                e is EndermanEntity && e.isAngry ||
                e is GolemEntity && e.handSwinging ||
                e is MobEntity && e.isAttacking
    }

    fun isNeutral(e: Entity): Boolean {
        return e is PiglinEntity && !e.isAngryAt(mc.player) ||
                e is WolfEntity && (!e.isAttacking || e.ownerUuid === mc.player!!.uuid) ||
                e is EndermanEntity && !e.isAngry ||
                e is GolemEntity && !e.handSwinging
    }

    fun isVehicle(e: Entity): Boolean {
        return e is BoatEntity || e is MinecartEntity || (e is Saddleable && e.isSaddled)
    }

    fun Entity.predictPos(ticks: Int = 1): Vec3d {
        return pos.add(velocity.multiply(ticks.toDouble()))
    }

    fun PlayerEntity.predictEyePos(ticks: Int = 1): Vec3d {
        return eyePos.add(velocity.multiply(ticks.toDouble()))
    }
}