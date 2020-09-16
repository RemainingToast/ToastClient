package dev.toastmc.client.command.util

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.server.command.CommandSource

@Environment(EnvType.CLIENT)
abstract class Command {
    companion object {
        @JvmField
        var dispatcher = CommandDispatcher<CommandSource>()
    }

    private var name: String = ""
    private var description: String = ""

    init {
        if (javaClass.isAnnotationPresent(Cmd::class.java)) {
            val moduleManifest = javaClass.getAnnotation(Cmd::class.java)
            name = moduleManifest.name
            description = moduleManifest.description
        }
    }

    abstract fun register(dispatcher: CommandDispatcher<CommandSource>)

    open fun getDescription(): String {
        return description
    }

    open fun getName(): String {
        return name
    }
}