package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.modules.Module
import java.util.*

class Panic : Command("Panic", """${ToastClient.cmdPrefix}panic""", "shutdowns client", false, "shutdown", "panic") {
    private val wasEnabled: MutableList<Module> = ArrayList()

    @Throws(InterruptedException::class)
    override fun run(args: Array<String>) {
        if (mc.currentScreen != null) return
        for (module in ToastClient.MODULE_MANAGER.modules) {
            if (module.isEnabled()) {
                module.setEnabled(false)
                wasEnabled.add(module)
            }
        }
        if (mc.inGameHud != null) {
            mc.inGameHud.chatHud.clear(true)
            mc.updateWindowTitle()
        }
    }
}