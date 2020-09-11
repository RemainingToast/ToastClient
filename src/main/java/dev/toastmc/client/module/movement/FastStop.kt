package dev.toastmc.client.module.movement

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.mc
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener

@ModuleManifest(
    label = "FastStop",
    description = "Stops you faster.",
    category = Category.MOVEMENT
)
class FastStop : Module() {
    @Setting(name = "AirStop") var airstop = true

    override fun onDisable() {
        if (mc.player == null) return
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
    }

    override fun onEnable() {
        if (mc.player == null) return
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        if (!mc.options.keyForward.isPressed && !mc.options.keyBack.isPressed && !mc.options.keyLeft.isPressed && !mc.options.keyRight.isPressed) when (mc.player!!.isOnGround) {
            true -> mc.player!!.setVelocity(0.0, mc.player!!.velocity.y, 0.0) !airstop -> mc.player!!.setVelocity(0.0, mc.player!!.velocity.y, 0.0)
        }
    })
}