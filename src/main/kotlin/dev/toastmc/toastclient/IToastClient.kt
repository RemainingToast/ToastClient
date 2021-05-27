package dev.toastmc.toastclient

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraft.client.MinecraftClient
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.quantumclient.energy.EventBus

interface IToastClient {

    val mc: MinecraftClient
    get() = MinecraftClient.getInstance()

    val clientName: String
    get() = "ToastClient"

    val version: String
    get() = "b2.1"

    val nameVersion: String
    get() = "$clientName $version"

    val logger: Logger
    get() = LogManager.getLogger(clientName)

    val eventBus: EventBus
    get() = EventBus()

    val gson: Gson
    get() = GsonBuilder().setPrettyPrinting().create()

}