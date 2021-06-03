package dev.toastmc.toastclient.impl.module.render

import dev.toastmc.toastclient.api.events.PacketEvent
import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket
import org.quantumclient.energy.Subscribe

object NoRender : Module("NoRender", Category.RENDER) {

    var explosion = bool("Explosions", true)
    var totem = bool("Totem", true)
    var fire = bool("Fire", false)
    var underwater = bool("Underwater", false)

    var group1 = group("Group1", explosion, totem, fire, underwater)

    var nausea = bool("Nausea", false)
    var bossbar = bool("BossBar", false)
    var pumpkin = bool("Pumpkin", false)
    var blindness = bool("Blindness", false)

    var group2 = group("Group2", nausea, bossbar, pumpkin, blindness)

    var hurtcam = bool("Hurtcam", false)
    var skylight = bool("Skylight", false)
    var xp = bool("XP", false)

    var group3 = group("Group3", hurtcam, skylight, xp)

    @Subscribe
    fun on(event: PacketEvent.Receive) {
        if (mc.player == null) return
        val packet = event.packet
        when {
            packet is ExplosionS2CPacket && explosion.value -> event.cancel()
            packet is LightUpdateS2CPacket && skylight.value -> event.cancel()
            packet is ExperienceOrbSpawnS2CPacket && xp.value -> event.cancel()
        }
    }

}