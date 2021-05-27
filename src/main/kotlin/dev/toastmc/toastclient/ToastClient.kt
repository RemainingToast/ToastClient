package dev.toastmc.toastclient

import dev.toastmc.toastclient.api.command.CommandManager
import net.fabricmc.api.ModInitializer
import org.quantumclient.energy.EventBus

class ToastClient : ModInitializer, IToastClient {

    companion object {
        val instance = ToastClient()
        val eventBus = instance.eventBus
    }

    override fun onInitialize() {
        val start = System.currentTimeMillis()

        logger.info("$nameVersion has started initializing...")

        CommandManager.init()

        logger.info("$nameVersion has finished initialization (${System.currentTimeMillis() - start}ms)")
    }

}