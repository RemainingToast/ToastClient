package me.remainingtoast.toastclient.client.command

import com.mojang.brigadier.CommandDispatcher
import me.remainingtoast.toastclient.api.command.Command
import me.remainingtoast.toastclient.api.command.type.ModuleArgumentType
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.util.*
import net.minecraft.client.MinecraftClient
import net.minecraft.command.CommandSource
import net.minecraft.util.Formatting

object Drawn : Command("Drawn") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral("draw") {
            argument("module", ModuleArgumentType.getModule()){
                does { ctx ->
                    val mod: Module = "module" from ctx
                    mod.toggleDrawn()
                    MinecraftClient.getInstance().player!!
                        .sendMessage(lit("${mod.name} now being ${if (!mod.isDrawn()) Formatting.RED.toString() + " HIDDEN" else Formatting.GREEN.toString() + " DRAWN"}")
                            .formatted(Formatting.GRAY), false)
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