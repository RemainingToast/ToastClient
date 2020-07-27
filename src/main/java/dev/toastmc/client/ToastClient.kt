package dev.toastmc.client

import dev.toastmc.client.command.CommandManager
import dev.toastmc.client.event.events.TickEvent
import me.zero.alpine.bus.EventBus
import me.zero.alpine.bus.EventManager
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Formatting
import org.apache.logging.log4j.LogManager

class ToastClient : ModInitializer {

    var CMD_PREFIX = "~"

    companion object{
        var MINECRAFT: MinecraftClient = MinecraftClient.getInstance()
        val COMMAND_MANAGER: CommandManager = CommandManager()
        const val MODNAME = "Toast Client"
        const val MODVER = "fabric-1.16.1-beta"

        val log = LogManager.getLogger("ToastClient")
        var CHAT_PREFIX = "${Formatting.DARK_GRAY}[${Formatting.RED}${Formatting.BOLD}Toast${Formatting.DARK_GRAY}]${Formatting.RESET}"
        var CMD_PREFIX = "."

        @JvmField
        val EVENT_BUS: EventBus = EventManager()
        var rainbow = 0xFFFFFF // This'll be updated every tick
    }

    override fun onInitialize() {
        log.info("Initialising $MODNAME $MODVER")
        COMMAND_MANAGER.initCommands()
        log.info("$MODNAME initialised")
        EVENT_BUS.post(this)
    }

    @EventHandler
    private val updateListener = Listener(EventHook<TickEvent.Client.InGame> { println("$CHAT_PREFIX Test")})


}