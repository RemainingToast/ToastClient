package dev.toastmc.toastclient

import dev.toastmc.toastclient.api.command.CommandManager
import net.fabricmc.api.ModInitializer
import org.quantumclient.energy.EventBus

object ToastClient : ModInitializer, IToastClient {

    override fun onInitialize() {
        val start = System.currentTimeMillis()

        logger.info("$nameVersion has started initializing...")

        CommandManager.init()

        logger.info("$nameVersion has finished initialization (${System.currentTimeMillis() - start}ms)")
    }

}