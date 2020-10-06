package dev.toastmc.client

import dev.toastmc.client.command.util.CommandManager
import dev.toastmc.client.event.KeyPressEvent
import dev.toastmc.client.module.ModuleManager
import dev.toastmc.client.util.ConfigUtil
import dev.toastmc.client.util.KeyUtil
import dev.toastmc.client.util.mc
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.bus.EventBus
import me.zero.alpine.bus.EventManager
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.fabricmc.api.ModInitializer
import net.minecraft.client.gui.screen.ChatScreen


class ToastClient : ModInitializer {
    companion object {
        val MODNAME = "Toast Client"
        val MODVER = "fabric-1.16.3-beta"
        val COMMAND_MANAGER: CommandManager = CommandManager()
        val MODULE_MANAGER: ModuleManager = ModuleManager()

        @Setting
        var CMD_PREFIX = "."

        @JvmField
        val EVENT_BUS: EventBus = EventManager()
    }

    override fun onInitialize() {
        MODULE_MANAGER.initMods()
        ConfigUtil.init()
        println("Config loaded successfully")
        COMMAND_MANAGER.initCommands()
        EVENT_BUS.subscribe(onKeyPressEvent)
        Runtime.getRuntime().addShutdownHook(Thread {
            println("${MODNAME.toUpperCase()} SAVING AND SHUTTING DOWN")
            ConfigUtil.save()
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
                mod.toggle()
                return@EventHook
            }
        }
    })
}