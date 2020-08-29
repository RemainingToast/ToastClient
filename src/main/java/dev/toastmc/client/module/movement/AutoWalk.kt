package dev.toastmc.client.module.movement

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.PacketEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket

@ModuleManifest(
        label = "AutoWalk",
        description = "Runs Forward",
        category = Category.MOVEMENT
)
class AutoWalk : Module() {

    override fun onEnable() {
        super.onEnable()
        ToastClient.EVENT_BUS.subscribe(onEvent)
    }

    override fun onDisable() {
        super.onDisable()
        ToastClient.EVENT_BUS.unsubscribe(onEvent)
    }

    @EventHandler
    private val onEvent = Listener(EventHook<PacketEvent.Send> {
        if (mc.player == null) return@EventHook
        if (it.packet is PlayerMoveC2SPacket || it.packet is InventoryS2CPacket) {
            mc.options.sprintToggled = true
            mc.options.keyForward.isPressed = true
        }
    })

}