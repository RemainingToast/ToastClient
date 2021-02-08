package me.remainingtoast.toastclient

import kotlinx.serialization.json.Json
import me.remainingtoast.toastclient.api.command.CommandManager
import me.remainingtoast.toastclient.api.config.LoadConfig
import me.remainingtoast.toastclient.api.config.SaveConfig
import me.remainingtoast.toastclient.api.event.OverlayEvent
import me.remainingtoast.toastclient.api.event.TickEvent
import me.remainingtoast.toastclient.api.gui.ToastGUI
import me.remainingtoast.toastclient.api.module.ModuleManager
import me.remainingtoast.toastclient.api.util.mc
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
        val CLICKGUI = ToastGUI()
        val DIRECTORY = mc.runDirectory.canonicalPath;
        val JSON = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

        var CMD_PREFIX = "."

        @JvmField
        val EVENT_BUS: EventBus = EventManager()
    }

    override fun onInitialize() {
        CommandManager.init()
        EVENT_BUS.subscribe(onRender)
        EVENT_BUS.subscribe(onUpdate)
        LoadConfig.init()
        SaveConfig.init()
        SaveConfig.saveEverything()

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                println("${MODNAME.toUpperCase()} SAVING AND SHUTTING DOWN")
                SaveConfig.saveEverything()
            }
        })
        println("${MODNAME.toUpperCase()} $MODVER STARTING")
    }

    @EventHandler
    val onRender = Listener(EventHook<OverlayEvent> {
        if (mc.player == null) return@EventHook
        ModuleManager.onRender()
        CLICKGUI.render()
    })

    @EventHandler
    val onUpdate = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        ModuleManager.onUpdate()
    })
}