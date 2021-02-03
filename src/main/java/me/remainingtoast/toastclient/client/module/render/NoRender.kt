package me.remainingtoast.toastclient.client.module.render

import me.remainingtoast.toastclient.ToastClient.Companion.EVENT_BUS
import me.remainingtoast.toastclient.api.event.PacketEvent
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.Setting.BooleanSetting
import me.remainingtoast.toastclient.api.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket

object NoRender : Module("NoRender", Category.RENDER) {

    var explosion: BooleanSetting = registerBoolean("Explosions",true)
    var totem: BooleanSetting = registerBoolean("Totem",  true)
    var fire: BooleanSetting = registerBoolean("Fire", false)
    var underwater: BooleanSetting = registerBoolean("Underwater", false)
    var nausea: BooleanSetting = registerBoolean("Nausea", false)
    var bossbar: BooleanSetting = registerBoolean("BossBar", false)
    var pumpkin: BooleanSetting = registerBoolean("Pumpkin", false)
    var blindness: BooleanSetting = registerBoolean("Blindness", false)
    var hurtcam: BooleanSetting = registerBoolean("Hurtcam", false)
    var skylight: BooleanSetting = registerBoolean("Skylight", false)
    var xp: BooleanSetting = registerBoolean("XP", false)

    override fun onEnable() {
        EVENT_BUS.subscribe(packetListener)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(packetListener)
    }

    @EventHandler
    private val packetListener = me.zero.alpine.listener.Listener(EventHook<PacketEvent.Receive> {
        if (mc.player == null) return@EventHook
        val packet = it.packet
        when {
            packet is ExplosionS2CPacket && explosion.value -> it.cancel()
            packet is LightUpdateS2CPacket && skylight.value -> it.cancel()
            packet is ExperienceOrbSpawnS2CPacket && xp.value -> it.cancel()
        }
    })

}