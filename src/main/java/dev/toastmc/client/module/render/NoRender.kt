package dev.toastmc.client.module.render

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.PacketEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket

@ModuleManifest(
    label = "NoRender",
    description = "Wont Render some particles and such.",
    category = Category.RENDER
)
class NoRender : Module() {
    @Setting(name = "Explosion") var explosion = true
    @Setting(name = "Totem") var totem = true
    @Setting(name = "Fire") var fire = true
    @Setting(name = "Underwater") var underwater = true
    @Setting(name = "Nausea") var nausea = true
    @Setting(name = "XP") var xp = true
    @Setting(name = "BossBar") var bossbar = true
    @Setting(name = "Pumpkin") var pumpkin = true
    @Setting(name = "Blindness") var blindness = true
    @Setting(name = "Hurtcam") var hurtcam = true
    @Setting(name = "Skylight") var skylight = true

    override fun onEnable() {
        EVENT_BUS.subscribe(packetListener)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(packetListener)
    }

    @EventHandler
    private val packetListener = me.zero.alpine.listener.Listener(EventHook<PacketEvent.Receive> {
        val packet = it.packet
        when {
            packet is ExplosionS2CPacket && explosion -> it.cancel()
            packet is LightUpdateS2CPacket && skylight -> it.cancel()
            packet is ExperienceOrbSpawnS2CPacket && xp -> it.cancel()
        }
    })

}