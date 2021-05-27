package dev.toastmc.toastclient.impl.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.toastclient.api.command.Command
import dev.toastmc.toastclient.api.command.type.ModuleArgumentType
import dev.toastmc.toastclient.api.module.Module
import dev.toastmc.toastclient.api.util.*
import net.minecraft.client.MinecraftClient
import net.minecraft.command.CommandSource
import net.minecraft.util.Formatting

object DrawCommand : Command("drawn") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral(label) {
            argument("module", ModuleArgumentType.getModule()){
                does { ctx ->
                    val mod: Module = "module" from ctx
                    mod.toggleDrawn()
                    val moduleName = "${Formatting.DARK_GRAY}[${Formatting.DARK_GREEN}${mod.getName()}${Formatting.DARK_GRAY}]${Formatting.GRAY}"
                    message(lit("$prefix $moduleName Successfully ${if (!mod.isDrawn()) Formatting.RED.toString() + "HIDDEN" else Formatting.GREEN.toString() + "DRAWN"}").formatted(Formatting.GRAY))
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