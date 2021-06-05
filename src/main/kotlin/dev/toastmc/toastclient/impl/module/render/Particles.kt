package dev.toastmc.toastclient.impl.module.render

import dev.toastmc.toastclient.api.events.EntityEvent
import dev.toastmc.toastclient.api.events.PlayerAttackEntityEvent
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.setting.Setting
import dev.toastmc.toastclient.api.util.RandomUtil
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.*
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import org.quantumclient.energy.Subscribe

object Particles : Module("Particles", Category.RENDER) {
    val particleTypes = Registry.PARTICLE_TYPE
    val particleNames = particleTypes.ids.map { idToName(it) }

    var crystalToggle = bool("Toggle", true)
    var crystalParticle = mode("Particle", "smoke", particleNames)
    var crystalRad = number("Radius", 1.0, 0.0, 10.0)
    var crystalCount = number("Count", 100, 1, 1000)
    var crystalSpeed = number("Speed", 1.0, 0.0, 5.0)
    var crystals = group("Crystals",
        crystalToggle,
        crystalParticle,
        crystalRad,
        crystalCount,
        crystalSpeed
    )

    var deathToggle = bool("Toggle", true)
    var deathKills = bool("KillsOnly", true)
    var deathPlayers = bool("PlayersOnly", true)
    var deathParticle = mode("Particle", "damage_indicator", particleNames)
    var deathRad = number("Radius", 0.0, 0.0, 10.0)
    var deathCount = number("Count", 1, 1, 1000)
    var deathSpeed = number("Speed", 0.0, 0.0, 5.0)
    var deaths = group("Deaths",
        deathToggle,
        deathKills,
        deathPlayers,
        deathParticle,
        deathRad,
        deathCount,
        deathSpeed
    )

    @Subscribe
    fun on(event: PlayerAttackEntityEvent) {
        if (mc.player == null || !crystalToggle.value ) return

        val ent = event.entity
        if (ent is EndCrystalEntity) {
            val pos = ent.pos.add(0.0, ent.eyeY / 2, 0.0)
            createParticleCloud(getParticle(crystalParticle, ParticleTypes.SMOKE), RandomUtil.nextCloud(crystalRad.value, crystalCount.value.toInt(), pos), pos, crystalSpeed.value)
        }
    }

    @Subscribe
    fun on(event: EntityEvent.EntityDeath) {
        if (mc.player == null || !deathToggle.value) return

        val ent = event.entity
        if ((deathPlayers.value && ent !is PlayerEntity)
            || (ent is PlayerEntity && ent == mc.player)
            || (deathKills.value && event.source.attacker != mc.player)) return

        val pos = ent.pos.add(0.0, ent.eyeY / 2, 0.0)
        createParticleCloud(getParticle(deathParticle, ParticleTypes.DAMAGE_INDICATOR), RandomUtil.nextCloud(deathRad.value, deathCount.value.toInt(), pos), pos, deathSpeed.value)
    }

    fun getParticle(mode: Setting.Mode, default: ParticleEffect): ParticleEffect {
        val particle = particleTypes.entries.filter { idToName(it.key.value) == mode.value }.getOrNull(0)
        return if (particle == null) default else particle.value as ParticleEffect
    }

    fun createParticleCloud(particleEffect: ParticleEffect, cloud: List<Vec3d>, center: Vec3d, speed: Double) {
        cloud.forEach { vec ->
            val motionVector = vec.subtract(center).normalize().multiply(speed)

            mc.world!!.addParticle(
                particleEffect,
                vec.x,
                vec.y,
                vec.z,
                motionVector.x,
                motionVector.y,
                motionVector.z
            )
        }
    }

    fun idToName(id: Identifier): String {
        return (if (id.namespace == "minecraft") "" else "${id.namespace}:") + id.path.toString()
    }

//    fun recoverPath(name: String): String {
//        return (if (name.contains(":")) "" else "minecraft:") + name
//    }
}