package dev.toastmc.toastclient.api.managers.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.toastclient.IToastClient
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.command.CommandSource
import net.minecraft.util.Formatting

@Environment(EnvType.CLIENT)
abstract class Command(name: String) : IToastClient {
    companion object {
        @JvmField
        var dispatcher = CommandDispatcher<CommandSource>()
    }

    var label: String = ""

    val prefix =
        Formatting.DARK_GRAY.toString() + "[" + Formatting.RED + clientName + Formatting.DARK_GRAY + "]" + Formatting.GRAY

    init {
        label = name
    }

    abstract fun register(dispatcher: CommandDispatcher<CommandSource>)

    fun getName(): String {
        return label
    }
}
