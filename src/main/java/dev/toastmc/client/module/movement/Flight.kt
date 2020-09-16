package dev.toastmc.client.module.movement

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
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
        label = "Flight",
        description = "Creative Flight",
        aliases = ["fly"],
        category = Category.MOVEMENT
)
class Flight : Module() {

    @Setting(name = "Speed") var speed = 5f

    override fun onEnable() {
        mc.player!!.abilities.allowFlying = true
        mc.player!!.abilities.flying = true
        mc.player!!.abilities.flySpeed = (0.05f * speed)
        EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        if (!mc.player!!.abilities.creativeMode) {
            mc.player!!.abilities.allowFlying = false
        }
        if (mc.player!!.isSpectator) {
            mc.player!!.abilities.allowFlying = true
            mc.player!!.abilities.flying = true
            mc.player!!.abilities.flySpeed = 0.05f
        } else {
            mc.player!!.abilities.flying = false
            mc.player!!.abilities.flySpeed = 0.05f
        }
        EVENT_BUS.unsubscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        if ((mc.player!!.isOnGround || mc.player!!.fallDistance <= 0) && this.enabled!!) {
            mc.player!!.abilities.allowFlying = true
            mc.player!!.abilities.flying = true
            mc.player!!.abilities.flySpeed = (0.05f * speed)
        }
    })
}