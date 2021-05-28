package dev.toastmc.toastclient.impl.module.misc

import dev.toastmc.toastclient.api.events.CloseScreenInPortalEvent
import dev.toastmc.toastclient.api.managers.module.Module
import org.quantumclient.energy.Subscribe

object PortalChat : Module("PortalChat", Category.MISC) {

    @Subscribe
    fun on(event: CloseScreenInPortalEvent) {
        if (mc.player == null) return
        event.cancel()
    }

}