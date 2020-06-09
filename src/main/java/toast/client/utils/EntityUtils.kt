package toast.client.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.*
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.passive.WolfEntity

object EntityUtils {
    private val mc = MinecraftClient.getInstance()
    fun notSelf(e: Entity): Boolean {
        return e !== mc.player && e !== mc.cameraEntity
    }

    @JvmStatic
    fun isAnimal(e: Entity?): Boolean {
        return e is AnimalEntity ||
                e is AmbientEntity ||
                e is WaterCreatureEntity ||
                e is GolemEntity && !e.isHandSwinging ||
                e is VillagerEntity
    }

    fun isLiving(e: Entity?): Boolean {
        return e is LivingEntity
    }

    @JvmStatic
    fun isHostile(e: Entity?): Boolean {
        return e is HostileEntity && e !is ZombiePigmanEntity && e !is EndermanEntity ||
                e is ZombiePigmanEntity && e.isAngryAt(mc.player) ||
                e is WolfEntity && e.isAngry && e.ownerUuid !== mc.player!!.uuid ||
                e is EndermanEntity && e.isAngry ||
                e is GolemEntity && e.isHandSwinging
    }

    @JvmStatic
    fun isNeutral(e: Entity?): Boolean {
        return e is ZombiePigmanEntity && !e.isAngryAt(mc.player) ||
                e is WolfEntity && (!e.isAngry || e.ownerUuid === mc.player!!.uuid) ||
                e is EndermanEntity && !e.isAngry ||
                e is GolemEntity && !e.isHandSwinging
    }
}