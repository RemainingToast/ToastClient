package dev.toastmc.toastclient.impl.module.player

import dev.toastmc.toastclient.api.events.PacketEvent
import dev.toastmc.toastclient.api.module.Module
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import org.quantumclient.energy.Subscribe

object AntiHunger : Module("AntiHunger", Category.PLAYER) {

    @Subscribe
    fun on(event: PacketEvent.Receive) {
        if (mc.player == null) return
        if (event.packet is PlayerMoveC2SPacket) {
            if (mc.isInSingleplayer) {
                mc.player!!.hungerManager.foodLevel = 20
                mc.player!!.hungerManager.setSaturationLevelClient(5.0f)
                return
            }
            event.cancel()
        }
    }

}