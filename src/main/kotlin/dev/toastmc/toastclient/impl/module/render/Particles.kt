package dev.toastmc.toastclient.impl.module.render

import dev.toastmc.toastclient.api.events.PlayerAttackEntityEvent
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.RandomUtil
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.particle.*
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.quantumclient.energy.Subscribe

object Particles : Module("Particles", Category.RENDER) {
    val particleTypes = Registry.PARTICLE_TYPE
    val particleNames = particleTypes.ids.map { idToName(it) }

    var crystalToggle = bool("Toggle", true)
    var crystalParticle = mode("Particle", "smoke", particleNames)
    var crystalRad = number("Radius", 1.0, 1.0, 10.0)
    var crystalCount = number("Count", 100, 1, 1000)
    var crystalSpeed = number("Speed", 1.0, 0.1, 5.0)
    var crystals = group("Crystals",
        crystalToggle,
        crystalParticle,
        crystalRad,
        crystalCount,
        crystalSpeed
    )

    @Subscribe
    fun on(event: PlayerAttackEntityEvent) {
        if (mc.player == null) return
        if (crystalToggle.value && event.entity is EndCrystalEntity) {
            val pos = event.entity.pos
            RandomUtil.nextCloud(crystalRad.value, crystalCount.value.toInt(), pos).forEach { vec ->
                val motionVector = vec.subtract(pos).normalize().multiply(crystalSpeed.value)
                val particle = particleTypes.entries.filter { idToName(it.key.value) == crystalParticle.value }.getOrNull(0)

                mc.world!!.addParticle(
                    if (particle == null) ParticleTypes.SMOKE else particle.value as ParticleEffect,
                    vec.x,
                    vec.y,
                    vec.z,
                    motionVector.x,
                    motionVector.y,
                    motionVector.z
                )
            }
        }
    }

    fun idToName(id: Identifier): String {
        return (if (id.namespace == "minecraft") "" else "${id.namespace}:") + id.path.toString()
    }

    fun recoverPath(name: String): String {
        return (if (name.contains(":")) "" else "minecraft:") + name
    }
}