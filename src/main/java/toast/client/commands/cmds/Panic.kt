package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.modules.Module
import java.util.*

/**
 * Command to disable all modules and removes "all" traces of the client
 */
class Panic :
    Command("Panic", """${ToastClient.cmdPrefix}panic""", "shut's the client down'", false, "shutdown", "panic") {
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