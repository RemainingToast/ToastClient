package me.remainingtoast.toastclient.client.module.player

import me.remainingtoast.toastclient.ToastClient.Companion.EVENT_BUS
import me.remainingtoast.toastclient.api.event.PacketEvent
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

object AntiHunger : Module("AntiHunger", Category.PLAYER) {

    override fun onEnable() {
        EVENT_BUS.subscribe(packetEventListener)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(packetEventListener)
    }

    @EventHandler
    private val packetEventListener = Listener(EventHook<PacketEvent.Receive> {
        if (mc.player == null) return@EventHook
        if (it.packet is PlayerMoveC2SPacket) it.cancel()
    })

}