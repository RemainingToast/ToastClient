package me.remainingtoast.toastclient.client.module.render

import me.remainingtoast.toastclient.ToastClient.Companion.EVENT_BUS
import me.remainingtoast.toastclient.api.event.PacketEvent
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.type.BooleanSetting
import me.remainingtoast.toastclient.api.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket

class NoRender : Module("NoRender", Category.RENDER) {

    companion object {
        lateinit var explosion: BooleanSetting
        lateinit var skylight: BooleanSetting
        lateinit var xp: BooleanSetting
    }

    init {
        explosion = registerBoolean("Explosions", "I.E TNT, End Crystals", true)
        skylight = registerBoolean("Skylight", "Cancels Light Updates", false)
        xp = registerBoolean("XP", "Hide XP Orbs", false)
    }

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