package dev.toastmc.client.module.player

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.ClipAtLedgeEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener

@ModuleManifest(
        label = "SafeWalk",
        description = "Prevent walking off ledges",
        category = Category.PLAYER,
        aliases = ["antiledge"]
)
class SafeWalk : Module() {

    override fun onEnable() {
        super.onEnable()
        ToastClient.EVENT_BUS.subscribe(clipListener)
    }

    override fun onDisable() {
        super.onDisable()
        ToastClient.EVENT_BUS.unsubscribe(clipListener)
    }

    @EventHandler
    private val clipListener =  Listener(EventHook<ClipAtLedgeEvent> {
        it.clip = true
    })
}