package me.remainingtoast.toastclient.client.module.movement

import me.remainingtoast.toastclient.ToastClient.Companion.EVENT_BUS
import me.remainingtoast.toastclient.api.event.ClipAtLedgeEvent
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener


object SafeWalk : Module("SafeWalk", Category.MOVEMENT) {

    override fun onEnable() {
        EVENT_BUS.subscribe(clipListener)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(clipListener)
    }

    @EventHandler
    private val clipListener =  Listener(EventHook<ClipAtLedgeEvent> {
        if (mc.player == null) return@EventHook
        it.clip = true
    })

}