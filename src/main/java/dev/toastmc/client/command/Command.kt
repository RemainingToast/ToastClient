package dev.toastmc.client.command

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient

@Environment(EnvType.CLIENT)
abstract class Command() {
    protected var mc: MinecraftClient = MinecraftClient.getInstance()
    private var label: String? = null
    private var description: String? = null
    private var usage: String? = null
    private var alias: Array<String>? = null
    /**
     * Method that runs a command while passing it an array of arguments
     */

    init {
        if (javaClass.isAnnotationPresent(CommandManifest::class.java)) {
            val moduleManifest = javaClass.getAnnotation(CommandManifest::class.java)
            label = moduleManifest.label
            alias = moduleManifest.aliases
            description = moduleManifest.description
            usage = moduleManifest.usage
        }
    }

    @Throws(InterruptedException::class)
    abstract fun run(args: Array<String>)

    open fun getLabel(): String? {
        return label
    }

    open fun getDescription(): String? {
        return description
    }

    open fun getAlias(): Array<String>? {
        return alias
    }

    open fun getUsage(): String? {
        return usage
    }
}