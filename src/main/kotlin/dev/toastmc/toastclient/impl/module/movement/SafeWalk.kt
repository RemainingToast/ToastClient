package dev.toastmc.toastclient.impl.module.movement

import dev.toastmc.toastclient.api.events.ClipAtLedgeEvent
import dev.toastmc.toastclient.api.managers.module.Module
import org.quantumclient.energy.Subscribe

object SafeWalk : Module("SafeWalk", Category.MOVEMENT) {

    @Subscribe
    fun on(event: ClipAtLedgeEvent) {
        if (mc.player == null) return
        event.clip = true
    }

}