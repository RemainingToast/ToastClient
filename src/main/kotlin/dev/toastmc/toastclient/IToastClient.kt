package dev.toastmc.toastclient

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.toastmc.toastclient.api.util.ModVersionReader
import net.minecraft.client.MinecraftClient
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

interface IToastClient {

    val mc: MinecraftClient
        get() = MinecraftClient.getInstance()

    val clientName: String
        get() = "Toast Client"

    val version: String
        get() {
            val modVersionReader = ModVersionReader()
            return modVersionReader.readModVersion() ?: "deez-nuts-69"
        }

    val capeUrl: String
        get() = "https://raw.githubusercontent.com/RemainingToast/ToastClient/dev/capes.json"

    val nameVersion: String
        get() = "$clientName $version"

    val logger: Logger
        get() = LogManager.getLogger(clientName)

    val gson: Gson
        get() = GsonBuilder().setPrettyPrinting().create()

}