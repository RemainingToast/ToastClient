package toast.client.modules.misc

import org.lwjgl.glfw.GLFW
import toast.client.ToastClient
import toast.client.modules.Module
import java.util.*

class Panic : Module("Panic", "Makes the client disappear until you relaunch the game.", Category.MISC, GLFW.GLFW_KEY_P) {
    private val wasEnabled: MutableList<Module> = ArrayList()
    override fun onEnable() {
        if (mc.currentScreen != null) return
        isPanicking = true
        for (module in ToastClient.MODULE_MANAGER.modules) {
            if (module.enabled && module.javaClass != this.javaClass) {
                module.disable()
                wasEnabled.add(module)
            }
        }
        if (mc.inGameHud != null) {
            val msgs = mc.inGameHud.chatHud.messageHistory // doesn't work no idea why doesn't delete shit
            val toDelete: MutableList<Int> = ArrayList()
            for (i in msgs.indices.reversed()) {
                if (msgs[i].contains(ToastClient.cleanPrefix)) {
                    toDelete.add(i)
                }
            }
            for (msgid in toDelete) {
                mc.inGameHud.chatHud.removeMessage(msgid)
            }
            mc.updateWindowTitle()
        }
    }

    override fun onDisable() {
        isPanicking = false
        for (module in ToastClient.MODULE_MANAGER.modules) {
            if (wasEnabled.contains(module)) {
                module.onEnable()
                wasEnabled.remove(module)
            }
        }
        if (mc.currentScreen != null) {
            mc.updateWindowTitle()
        }
    }

    companion object {
        private var isPanicking = false
        @JvmStatic
        fun IsPanicking(): Boolean {
            return isPanicking
        }
    }
}