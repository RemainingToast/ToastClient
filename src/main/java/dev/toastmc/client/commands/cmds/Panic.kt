package dev.toastmc.client.commands.cmds

import dev.toastmc.client.ToastClient
import dev.toastmc.client.commands.Command
import dev.toastmc.client.commands.CommandManifest
import dev.toastmc.client.modules.Module
import java.util.*

/**
 * Command to disable all modules and removes "all" traces of the client
 */
@CommandManifest(label = "Panic", usage = "panic", description = "Shuts the client down.", aliases = ["shutdown", "shut", "panic"])
class Panic : Command() {
    private val wasEnabled: MutableList<Module> = ArrayList()
    @Throws(InterruptedException::class)
    override fun run(args: Array<String>) {
        if (mc.currentScreen != null) return
        for (module in ToastClient.MODULE_MANAGER.modules) {
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
}