package dev.toastmc.client.module.movement

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener

@ModuleManifest(
        label = "Sprint",
        description = "Always Sprint",
        category = Category.MOVEMENT
)
class Sprint : Module(){

    override fun onEnable() {
        super.onEnable()
        EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        super.onDisable()
        EVENT_BUS.unsubscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        mc.player!!.isSprinting = mc.player!!.input.movementForward > 0 && mc.player!!.input.movementSideways != 0f || mc.player!!.input.movementForward > 0 && !mc.player!!.isSneaking
    })
}