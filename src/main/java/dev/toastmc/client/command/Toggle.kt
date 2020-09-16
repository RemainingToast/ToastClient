package dev.toastmc.client.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.client.command.util.*
import dev.toastmc.client.command.util.type.ModuleArgumentType
import dev.toastmc.client.module.Module
import dev.toastmc.client.util.Color
import dev.toastmc.client.util.sendMessage
import net.minecraft.server.command.CommandSource
import net.minecraft.util.Formatting

@Cmd(name = "toggle")
class Toggle : Command() {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral("toggle") {
            argument("module", ModuleArgumentType.getModule()){
                does { ctx ->
                    val mod: Module = "module" from ctx
                    mod.enabled = !mod.enabled
                    sendMessage("Toggled ${mod.label}${if (!mod.enabled) Formatting.RED.toString() + " OFF" else Formatting.GREEN.toString() + " ON"}", Color.GRAY)
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