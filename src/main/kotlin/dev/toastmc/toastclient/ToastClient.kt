package dev.toastmc.toastclient

import dev.toastmc.toastclient.api.command.CommandManager
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

        logger.info("Finished loading $nameVersion (${System.currentTimeMillis() - start}ms)")
    }

}