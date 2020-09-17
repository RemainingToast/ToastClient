package dev.toastmc.client.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.client.command.util.*
import dev.toastmc.client.command.util.type.ModuleArgumentType
import dev.toastmc.client.module.Module
import dev.toastmc.client.util.Color
import dev.toastmc.client.util.sendMessage
import net.minecraft.server.command.CommandSource
import net.minecraft.util.Formatting

class Hide : Command("hide") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral("hide") {
            argument("module", ModuleArgumentType.getModule()){
                does { ctx ->
                    val mod: Module = "module" from ctx
                    mod.setHidden(!mod.hidden)
                    sendMessage("${mod.label} is now being ${if (mod.hidden) Formatting.RED.toString() + "HIDDEN" else Formatting.GREEN.toString() + "SHOWN"}", Color.GRAY)
                    0
                }
            }
            does{
                println("ree")
                0
            }
        }
    }
}