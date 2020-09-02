package dev.toastmc.client

import dev.toastmc.client.command.CommandManager
import dev.toastmc.client.module.ModuleManager
import dev.toastmc.client.util.FileManager
import dev.toastmc.client.util.RenderBuilder
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
import net.minecraft.client.options.ServerList
import net.minecraft.util.Formatting
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
        FILE_MANAGER.writeFile(File(MOD_DIRECTORY, "README.txt"), "${MODNAME} ${MODVER} is WORK IN PROGRESS, bugs are very probable.\n"
                + "Please REPORT BUGS or SUGGEST FEATURES in the official discord: https://discord.gg/gxyWEdG\n"
                + "\nNOTE: THIS BUILD OF TOAST CLIENT DOES NOT HAVE A CLICKGUI, YOU MUST TOGGLE MODULES USING COMMANDS.\n\n"
                + "Do .help to get a list of all the commands.\n" +
                "Do .toggle <module> to toggle a module.\n" +
                "Do .modules for a list of modules\n\n" +
                "Thanks for using Toast Client :)")
        GlobalScope.launch {
            if (MINECRAFT.world == null) {
                WorldUtil.loadedChunks.clear()
            }
            delay(500)
        }.start()
    }
}