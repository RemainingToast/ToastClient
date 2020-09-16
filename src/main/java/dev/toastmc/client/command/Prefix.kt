package dev.toastmc.client.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.client.ToastClient
import dev.toastmc.client.command.util.*
import dev.toastmc.client.util.MessageUtil
import net.minecraft.server.command.CommandSource

@Cmd(
        name = "prefix"
)
class Prefix : Command() {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral("prefix") {
            greedyString("char"){
                does { ctx ->
                    val str: String = "char" from ctx
                    val char: Char = str.toCharArray()[0]
                    ToastClient.CMD_PREFIX = char.toString()
                    MessageUtil.sendMessage("Prefix has been set to: $char", MessageUtil.Color.GREEN)
                    0
                }
            }
        }
    }
}