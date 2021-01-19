package me.remainingtoast.toastclient

import me.remainingtoast.toastclient.api.event.OverlayEvent
import me.remainingtoast.toastclient.api.event.RenderEvent
import me.remainingtoast.toastclient.api.module.ModuleManager
import me.remainingtoast.toastclient.api.setting.SettingManager
import me.remainingtoast.toastclient.api.util.mc
import me.remainingtoast.toastclient.api.gui.ToastGUI
import me.zero.alpine.bus.EventBus
import me.zero.alpine.bus.EventManager
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.fabricmc.api.ModInitializer

class ToastClient : ModInitializer {

    companion object {
        val MODNAME = "Toast Client"
        val MODVER = "2.0.1"
        val SETTING_MANAGER = SettingManager()
        val MODULE_MANAGER = ModuleManager()
        val CLICKGUI = ToastGUI()

        var CMD_PREFIX = "."

        @JvmField
        val EVENT_BUS: EventBus = EventManager()
    }

    override fun onInitialize() {
        println("${MODNAME.toUpperCase()} $MODVER STARTING")

        EVENT_BUS.subscribe(onRender)

        Runtime.getRuntime().addShutdownHook(Thread {
            println("${MODNAME.toUpperCase()} SAVING AND SHUTTING DOWN")
        })
    }

    @EventHandler
    val onRender = Listener(EventHook<OverlayEvent> {
        if (mc.player == null) return@EventHook
        MODULE_MANAGER.onRender()
        CLICKGUI.render()
    })
}