package toast.client.commands.cmds

import org.lwjgl.glfw.GLFW
import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.KeyUtil
import toast.client.utils.Logger

/**
 * Command to bind a module to a key
 */
class Bind : Command("Bind", "${ToastClient.cmdPrefix}bind [all, module] [key]", "Bind module to key", false, "bind") {
    override fun run(args: Array<String>) {
        if (args.isNotEmpty()) {
            val module = ToastClient.MODULE_MANAGER.getModule(args[0])
            if (args[0] == "all") {
                if (args[1] == "none") {
                    var i = 0
                    while (true) {
                        ToastClient.MODULE_MANAGER.modules[i].key = GLFW.GLFW_KEY_UNKNOWN
                        Logger.message("${ToastClient.MODULE_MANAGER.modules[i].name} keybind set to NONE", Logger.SUCCESS, true)
                        if (i == ToastClient.MODULE_MANAGER.modules.size) {
                            ToastClient.CONFIG_MANAGER.writeKeyBinds()
                            break
                        }
                        i++
                    }
                }
            } else if (module != null) {
                var keyCode = -1;
                if (args[1].toLowerCase() == "none") {
                    keyCode = -1
                } else {
                    keyCode = KeyUtil.getKeyCode(args[1])
                    if (keyCode == -1) {
                        Logger.message("No Key by the name of ${args[1]} was found.", Logger.ERR, false)
                        return
                    }
                }
                try {
                    module.key = keyCode
                    Logger.message("Module ${module.name} is now bound to ${args[1].toUpperCase()}", Logger.INFO, false)
                    ToastClient.CONFIG_MANAGER.writeKeyBinds()
                } catch (nfe: NumberFormatException) {
                    Logger.message("Failed to bind Module ${module.name} to \"${args[1].toUpperCase()}\"", Logger.ERR, false)
                }
            } else {
                Logger.message("${args[0]} is not a valid module", Logger.ERR, true)
            }
        } else {
            Logger.message("Not enough arguments", Logger.ERR, true)
        }
    }
}