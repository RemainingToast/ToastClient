package dev.toastmc.toastclient.impl.module.player

import dev.toastmc.toastclient.api.events.PacketEvent
import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import org.quantumclient.energy.Subscribe

object AntiHunger : Module("AntiHunger", Category.PLAYER) {

    @Subscribe
    fun on(event: PacketEvent.Receive) {
        if (event.packet is PlayerMoveC2SPacket) {
            if (mc.isInSingleplayer) {
                mc.player!!.hungerManager.add(20, 5.0f)
                mc.player!!.hungerManager.update(mc.player)
                return
            }
            event.cancel()
        }
    }

}