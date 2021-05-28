package dev.toastmc.toastclient

import dev.toastmc.toastclient.api.managers.command.CommandManager
import dev.toastmc.toastclient.api.config.ConfigUtil
import net.fabricmc.api.ModInitializer
import org.quantumclient.energy.EventBus

open class ToastClient : ModInitializer, IToastClient {

    companion object {
        val eventBus: EventBus = EventBus()
    }

    override fun onInitialize() {
        val start = System.currentTimeMillis()

        logger.info("Started loading $nameVersion")

        CommandManager.init()

        ConfigUtil.init()

        Runtime.getRuntime().addShutdownHook(Thread {
            ConfigUtil.saveEverything()
            logger.info("$nameVersion saved and quit successfully.")
        })

        logger.info("Finished loading $nameVersion (${System.currentTimeMillis() - start}ms)")
    }

}