package dev.toastmc.toastclient.impl.module.render

import dev.toastmc.toastclient.api.events.PacketEvent
import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket
import org.quantumclient.energy.Subscribe

object NoRender : Module("NoRender", Category.RENDER) {

    var explosion = bool("Explosions",true)
    var totem = bool("Totem",  true)
    var fire = bool("Fire", false)
    var underwater = bool("Underwater", false)

    var nausea = bool("Nausea", false)
    var bossbar = bool("BossBar", false)
    var pumpkin = bool("Pumpkin", false)
    var blindness = bool("Blindness", false)

    var hurtcam = bool("Hurtcam", false)
    var skylight = bool("Skylight", false)
    var xp = bool("XP", false)

    private var player = group("Player", explosion, fire, underwater, nausea, blindness)
    private var world = group("World", xp, skylight)
    private var gui = group("GUI", totem, bossbar, pumpkin, hurtcam)

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