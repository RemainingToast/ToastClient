package dev.toastmc.toastclient.impl.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.toastclient.api.managers.command.Command
import dev.toastmc.toastclient.api.managers.command.type.ModuleArgumentType
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.*
import net.minecraft.command.CommandSource
import net.minecraft.util.Formatting

object ToggleCommand : Command("toggle") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral(label) {
            argument("module", ModuleArgumentType.getModule()) {
                does { ctx ->
                    val mod: Module = "module" from ctx
                    mod.toggle()
                    val moduleName =
                        "${Formatting.DARK_GRAY}[${Formatting.DARK_GREEN}${mod.getName()}${Formatting.DARK_GRAY}]${Formatting.GRAY}"
                    message(
                        lit("$prefix $moduleName Successfully ${if (mod.isEnabled()) Formatting.GREEN.toString() + "ENABLED" else Formatting.RED.toString() + "DISABLED"}").formatted(
                            Formatting.GRAY
                        )
                    )
                    0
                }
            }
            does {
                println("Something didn't go right.")
                0
            }
        }
    }
}