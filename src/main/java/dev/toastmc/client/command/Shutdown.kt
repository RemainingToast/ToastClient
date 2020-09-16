package dev.toastmc.client.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.client.ToastClient.Companion.MODULE_MANAGER
import dev.toastmc.client.command.util.Command
import dev.toastmc.client.command.util.does
import dev.toastmc.client.command.util.register
import dev.toastmc.client.command.util.rootLiteral
import dev.toastmc.client.module.Module
import dev.toastmc.client.util.mc
import net.minecraft.server.command.CommandSource

class Shutdown : Command("shutdown") {
    private val wasEnabled: MutableList<Module> = ArrayList()
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral("shutdown"){
            does {
                if (mc.currentScreen == null) {
                    for (module in MODULE_MANAGER.modules) {
                        if (module.enabled) {
                            module.disable()
                            wasEnabled.add(module)
                        }
                    }
                    if (mc.inGameHud != null) {
                        mc.inGameHud.chatHud.clear(true)
                        mc.updateWindowTitle()
                    }
                }
                0
            }
        }
    }

}