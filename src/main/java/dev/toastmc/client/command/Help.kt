package dev.toastmc.client.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.client.ToastClient
import dev.toastmc.client.command.util.*
import dev.toastmc.client.util.Color
import dev.toastmc.client.util.sendMessage
import net.minecraft.server.command.CommandSource

@Cmd(name = "help", description = "List of commands.")
class Help : Command() {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral("help") {
            does {
                val str = ToastClient.COMMAND_MANAGER.commandsToString()
                sendMessage("Commands (${ToastClient.COMMAND_MANAGER.commands.size}): $str", Color.GRAY)
                0
            }
        }
    }
}