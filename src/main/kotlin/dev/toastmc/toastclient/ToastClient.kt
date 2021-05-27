package dev.toastmc.toastclient

import kotlinx.serialization.json.Json
import dev.toastmc.toastclient.api.command.CommandManager
import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
import org.quantumclient.energy.EventBus

class ToastClient : ModInitializer {

    companion object {
        @JvmField val NAME = "Toast Client"
        @JvmField val VERSION = "b2"
        @JvmField val NAME_VERSION = "$NAME $VERSION"
        @JvmField val LOGGER = LogManager.getLogger(NAME)
        @JvmField val EVENT_BUS = EventBus()
        @JvmField var CMD_PREFIX = "."
        @JvmField val JSON = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }
    }

    override fun onInitialize() {
        val start = System.currentTimeMillis()

        LOGGER.info("$NAME_VERSION has started initializing...")

        CommandManager.init();

        LOGGER.info("$NAME_VERSION has finished initialization (${System.currentTimeMillis() - start}ms)")
    }

}