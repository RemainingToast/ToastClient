package dev.toastmc.client

import dev.toastmc.client.command.CommandManager
import dev.toastmc.client.module.ModuleManager
import dev.toastmc.client.module.player.Velocity
import dev.toastmc.client.util.SettingSaveUtil
import me.zero.alpine.bus.EventBus
import me.zero.alpine.bus.EventManager
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Formatting

class ToastClient : ModInitializer {
    companion object {
        var MINECRAFT: MinecraftClient = MinecraftClient.getInstance()
        val COMMAND_MANAGER: CommandManager = CommandManager()
        val MODULE_MANAGER: ModuleManager = ModuleManager()
        val CONFIG = SettingSaveUtil()
        val CONFIG_FILE = "ToastClientConfig.json"
        const val MODNAME = "Toast Client"
        const val MODVER = "fabric-1.16.1-beta"

        var CHAT_PREFIX = "${Formatting.DARK_GRAY}[${Formatting.RED}${Formatting.BOLD}Toast${Formatting.DARK_GRAY}]${Formatting.RESET}"
        var CMD_PREFIX = "."

        @JvmField
        val EVENT_BUS: EventBus = EventManager()

        var rainbow = 0xFFFFFF // This'll be updated every tick
    }

    override fun onInitialize() {
        COMMAND_MANAGER.initCommands()
        CONFIG.load()
        val vel: Velocity = MODULE_MANAGER.getModuleByClass(Velocity::class) as Velocity
        println("h=${vel.horizontal},v=${vel.vertical}")
        CONFIG.save()
    }
}