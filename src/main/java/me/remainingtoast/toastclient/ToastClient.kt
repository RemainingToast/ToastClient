package me.remainingtoast.toastclient

import me.remainingtoast.toastclient.api.command.CommandManager
import me.remainingtoast.toastclient.api.config.LoadConfig
import me.remainingtoast.toastclient.api.config.SaveConfig
import me.remainingtoast.toastclient.api.event.OverlayEvent
import me.remainingtoast.toastclient.api.event.TickEvent
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
        val SETTING_MANAGER = SettingManager
        val MODULE_MANAGER = ModuleManager
        val COMMAND_MANAGER = CommandManager
        val CLICKGUI = ToastGUI()

        var CMD_PREFIX = "."

        @JvmField
        val EVENT_BUS: EventBus = EventManager()
    }

    override fun onInitialize() {
        COMMAND_MANAGER.init()
        EVENT_BUS.subscribe(onRender)
        EVENT_BUS.subscribe(onUpdate)
        SaveConfig.init()
        LoadConfig.init()
        Runtime.getRuntime().addShutdownHook(Thread {
            SaveConfig.saveEverything()
            println("${MODNAME.toUpperCase()} SAVING AND SHUTTING DOWN")
        })

        println("${MODNAME.toUpperCase()} $MODVER STARTING")
    }

    @EventHandler
    val onRender = Listener(EventHook<OverlayEvent> {
        if (mc.player == null) return@EventHook
        MODULE_MANAGER.onRender()
        CLICKGUI.render()
    })

    @EventHandler
    val onUpdate = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        ModuleManager.onUpdate()
    })
}