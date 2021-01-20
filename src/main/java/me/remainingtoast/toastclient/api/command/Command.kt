package me.remainingtoast.toastclient.api.command

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.command.CommandSource

@Environment(EnvType.CLIENT)
abstract class Command(name: String) {
    companion object {
        @JvmField
        var dispatcher = CommandDispatcher<CommandSource>()
    }

    var label: String = ""

    init {
        label = name
    }

    abstract fun register(dispatcher: CommandDispatcher<CommandSource>)

    fun getName(): String {
        return label
    }
}
