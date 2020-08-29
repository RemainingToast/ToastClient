package dev.toastmc.client.module.player

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

@ModuleManifest(
        label = "NoFall",
        description = "Prevent Fall Damage",
        category = Category.PLAYER
)
class NoFall : Module() {

    override fun onEnable() {
        super.onEnable()
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        super.onDisable()
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        if (mc.player!!.isFallFlying && mc.player!!.fallDistance <= 3f) return@EventHook
        mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket(true))
    })

}