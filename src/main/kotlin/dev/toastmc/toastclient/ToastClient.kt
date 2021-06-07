package dev.toastmc.toastclient

import dev.toastmc.toastclient.api.config.ConfigUtil
import dev.toastmc.toastclient.api.managers.command.CommandManager
import dev.toastmc.toastclient.api.managers.module.ModuleManager
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import org.quantumclient.energy.EventBus

open class ToastClient : ModInitializer, IToastClient {

    companion object {
        val eventBus: EventBus = EventBus()
    }

    override fun onInitialize() {
        ClientLifecycleEvents.CLIENT_STARTED.register {
            val start = System.currentTimeMillis()

            logger.info("Started loading $nameVersion")

            ModuleManager.init()

            CommandManager.init()

            ConfigUtil.init()

            logger.info("Finished loading $nameVersion (${System.currentTimeMillis() - start}ms)")
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            ConfigUtil.saveEverything()
            logger.info("$nameVersion saved and quit successfully.")
        })
    }
}