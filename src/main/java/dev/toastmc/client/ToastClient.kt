package dev.toastmc.client

import dev.toastmc.client.command.CommandManager
import dev.toastmc.client.module.ModuleManager
import dev.toastmc.client.util.ConfigUtil
import dev.toastmc.client.util.FileManager
import me.zero.alpine.bus.EventBus
import me.zero.alpine.bus.EventManager
import net.fabricmc.api.ModInitializer

class ToastClient : ModInitializer {
    companion object {
        val MODNAME = "Toast Client"
        val MODVER = "fabric-1.16.3-beta"
        val COMMAND_MANAGER: CommandManager = CommandManager()
        val MODULE_MANAGER: ModuleManager = ModuleManager()
        val FILE_MANAGER: FileManager = FileManager()
        var CMD_PREFIX = "."

        @JvmField
        val EVENT_BUS: EventBus = EventManager()
    }

    override fun onInitialize() {
        COMMAND_MANAGER.initCommands()
        FILE_MANAGER.initFileManager()
        ConfigUtil.init()
    }

}