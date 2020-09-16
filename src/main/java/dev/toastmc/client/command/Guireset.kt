package dev.toastmc.client.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.client.ToastClient
import dev.toastmc.client.command.util.Command
import dev.toastmc.client.command.util.does
import dev.toastmc.client.command.util.register
import dev.toastmc.client.command.util.rootLiteral
import dev.toastmc.client.util.Color
import dev.toastmc.client.util.sendMessage
import net.minecraft.server.command.CommandSource

class Guireset : Command("guireset") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral("guireset"){
            does {
                if(ToastClient.MODULE_MANAGER.clickguiHasOpened){
//                    val mod = ToastClient.MODULE_MANAGER.getModuleByClass(ClickGUI::class.java) as ClickGUI
//                    mod.settings.initCategoryPositions()
//                    mod.settings.savePositions()
//                    mod.settings.saveColors()
                    sendMessage("GUI has been reset", Color.GREEN)
                } else sendMessage("GUI HASNT OPENED", Color.RED)
                0
            }
        }
    }
}