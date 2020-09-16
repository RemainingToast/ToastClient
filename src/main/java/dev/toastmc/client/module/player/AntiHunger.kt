package dev.toastmc.client.module.player

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.PacketEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

@ModuleManifest(
    label = "AntiHunger",
    description = "Take less hunger",
    category = Category.PLAYER
)
class AntiHunger : Module() {

    override fun onEnable() {
        EVENT_BUS.subscribe(packetEventListener)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(packetEventListener)
    }

    @EventHandler
    private val packetEventListener = Listener(EventHook<PacketEvent.Receive> {
        if (it.packet is PlayerMoveC2SPacket) it.cancel()
    })
}