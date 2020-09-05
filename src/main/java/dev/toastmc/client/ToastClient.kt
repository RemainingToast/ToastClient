package dev.toastmc.client

import baritone.Baritone
import baritone.api.BaritoneAPI
import baritone.api.IBaritoneProvider
import dev.toastmc.client.command.CommandManager
import dev.toastmc.client.module.ModuleManager
import dev.toastmc.client.util.FileManager
import dev.toastmc.client.util.SettingSaveUtil
import dev.toastmc.client.util.WorldUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.zero.alpine.bus.EventBus
import me.zero.alpine.bus.EventManager
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ServerInfo
import net.minecraft.util.Formatting
import java.io.File

class ToastClient : ModInitializer {
    companion object {
        const val MODNAME = "Toast Client"
        const val MODVER = "fabric-1.16.2-beta"

        var MINECRAFT: MinecraftClient = MinecraftClient.getInstance()
        val COMMAND_MANAGER: CommandManager = CommandManager()
        val MODULE_MANAGER: ModuleManager = ModuleManager()
        val BARITONE_PROVIDER: IBaritoneProvider = BaritoneAPI.getProvider()

        val FILE_MANAGER: FileManager = FileManager()
        val CONFIG: SettingSaveUtil = SettingSaveUtil()
        val MOD_DIRECTORY: File = File(MinecraftClient.getInstance().runDirectory, MODNAME.toLowerCase().replace(" ", "") + "/")

        lateinit var serverList: List<ServerInfo>

        var CHAT_PREFIX = "${Formatting.DARK_GRAY}[${Formatting.RED}${Formatting.BOLD}Toast${Formatting.DARK_GRAY}]${Formatting.RESET}"
        var CMD_PREFIX = "."

        @JvmField
        val EVENT_BUS: EventBus = EventManager()
        var rainbow = 0xFFFFFF // This'll be updated every tick
    }

    override fun onInitialize() {
        COMMAND_MANAGER.initCommands()
        FILE_MANAGER.initFileManager()
        CONFIG.initSettingUtil()
        GlobalScope.launch {
            while (true) {
                if (MINECRAFT.world == null) {
                    WorldUtil.loadedChunks.clear()
                }
                delay(500)
            }
        }
    }
}