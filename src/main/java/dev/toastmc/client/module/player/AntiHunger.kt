package dev.toastmc.client.module.player

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.PacketEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.FabricReflect
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
        super.onEnable()
        ToastClient.EVENT_BUS.subscribe(packetEventListener)
    }

    override fun onDisable() {
        super.onDisable()
        ToastClient.EVENT_BUS.unsubscribe(packetEventListener)
    }

    @EventHandler
    private val packetEventListener = Listener(EventHook<PacketEvent.Receive> {
//        if (it.packet is PlayerMoveC2SPacket) it.cancel()
        if (it.packet is PlayerMoveC2SPacket) {
            if (mc.player!!.velocity.y != 0.0 && !mc.options.keyJump.isPressed) {
                mc.player!!.isOnGround = mc.player!!.fallDistance >= 0.1f
                FabricReflect.writeField(it.packet, mc.player!!.fallDistance >= 0.1f, "field_12891", "onGround")
            }
        }
    })
}