package me.remainingtoast.toastclient.client.command

import com.mojang.brigadier.CommandDispatcher
import me.remainingtoast.toastclient.api.command.Command
import me.remainingtoast.toastclient.api.command.type.ModuleArgumentType
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.util.*
import net.minecraft.client.MinecraftClient
import net.minecraft.command.CommandSource
import net.minecraft.util.Formatting

object ToggleModule : Command("Toggle") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral("toggle") {
            argument("module", ModuleArgumentType.getModule()){
                does { ctx ->
                    val mod: Module = "module" from ctx
                    mod.toggle()
                    MinecraftClient.getInstance().player!!.sendMessage(lit("Toggled ${mod.name}${if (!mod.isEnabled()) Formatting.RED.toString() + " OFF" else Formatting.GREEN.toString() + " ON"}").formatted(Formatting.GRAY), false)
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