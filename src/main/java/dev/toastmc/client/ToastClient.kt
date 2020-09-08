package dev.toastmc.client

import dev.toastmc.client.command.CommandManager
import dev.toastmc.client.module.ModuleManager
import dev.toastmc.client.module.movement.Flight
import dev.toastmc.client.util.FileManager
import dev.toastmc.client.util.SettingSaveUtil
import me.zero.alpine.bus.EventBus
import me.zero.alpine.bus.EventManager
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import java.io.File

class ToastClient : ModInitializer {
    companion object {
        const val MODNAME = "Toast Client"
        const val MODVER = "fabric-1.16.2-beta"

        var MINECRAFT: MinecraftClient = MinecraftClient.getInstance()
        val COMMAND_MANAGER: CommandManager = CommandManager()
        val MODULE_MANAGER: ModuleManager = ModuleManager()

        val FILE_MANAGER: FileManager = FileManager()
        val CONFIG: SettingSaveUtil = SettingSaveUtil()
        val MOD_DIRECTORY: File = File(MinecraftClient.getInstance().runDirectory, MODNAME.toLowerCase().replace(" ", "") + "/")

        var CMD_PREFIX = "."

        @JvmField
        val EVENT_BUS: EventBus = EventManager()
        var rainbow = 0xFFFFFF // This'll be updated every tick
    }

    override fun onInitialize() {
        COMMAND_MANAGER.initCommands()
        FILE_MANAGER.initFileManager()
        CONFIG.initSettingUtil()s
    }
}