package dev.toastmc.toastclient.impl.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.toastclient.api.command.Command
import dev.toastmc.toastclient.api.command.type.ModuleArgumentType
import dev.toastmc.toastclient.api.module.Module
import dev.toastmc.toastclient.api.util.*
import net.minecraft.client.MinecraftClient
import net.minecraft.command.CommandSource
import net.minecraft.util.Formatting

object ToggleCommand : Command("toggle") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral(label) {
            argument("module", ModuleArgumentType.getModule()){
                does { ctx ->
                    val mod: Module = "module" from ctx
                    mod.toggle()
                    MinecraftClient.getInstance().player!!.sendMessage(lit("Toggled ${mod.getName()}${if (!mod.isEnabled()) Formatting.RED.toString() + " OFF" else Formatting.GREEN.toString() + " ON"}").formatted(Formatting.GRAY), false)
                    0
                }
            }
            does{
                println("Something didn't go right.")
                0
            }
        }
    }
}