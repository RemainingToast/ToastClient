package me.remainingtoast.toastclient.client.module.misc

import me.remainingtoast.toastclient.ToastClient.Companion.EVENT_BUS
import me.remainingtoast.toastclient.api.event.CloseScreenInPortalEvent
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener

object PortalChat : Module("PortalChat", Category.MISC) {

    override fun onEnable() {
        EVENT_BUS.subscribe(closeScreenEvent)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(closeScreenEvent)
    }

    @EventHandler
    private val closeScreenEvent =  Listener(EventHook<CloseScreenInPortalEvent> {
        if (mc.player == null) return@EventHook
        it.cancel()
    })

}