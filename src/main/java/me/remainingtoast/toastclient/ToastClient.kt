package me.remainingtoast.toastclient

import me.remainingtoast.toastclient.api.event.KeyPressEvent
import me.remainingtoast.toastclient.api.module.ModuleManager
import me.remainingtoast.toastclient.api.setting.SettingManager
import me.remainingtoast.toastclient.api.util.KeyUtil
import me.zero.alpine.bus.EventBus
import me.zero.alpine.bus.EventManager
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen

class ToastClient : ClientModInitializer {

    companion object {
        val MODNAME = "Toast Client"
        val MODVER = "1.2.0"
        val SETTING_MANAGER = SettingManager()
        val MODULE_MANAGER = ModuleManager()
        val mc = MinecraftClient.getInstance()

        var CMD_PREFIX = "."

        @JvmField
        val EVENT_BUS: EventBus = EventManager()
    }

    override fun onInitializeClient() {
        EVENT_BUS.subscribe(onKeyPressEvent)
        Runtime.getRuntime().addShutdownHook(Thread {
            println("${MODNAME.toUpperCase()} SAVING AND SHUTTING DOWN")
        })
    }

    @EventHandler
    private val onKeyPressEvent = Listener(EventHook<KeyPressEvent> {
        if (mc.player == null || CMD_PREFIX.length != 1) return@EventHook
        if (it.key == KeyUtil.getKeyCode(CMD_PREFIX) && mc.currentScreen == null) {
            mc.openScreen(ChatScreen(""))
            return@EventHook
        }
        for (mod in MODULE_MANAGER.modules) {
            if (mod.key == -1) continue
            if (mod.key == it.key) {
                mod.setEnabled(true)
                return@EventHook
            }
        }
    })
}