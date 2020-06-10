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
                if (KeyUtil.isNumeric(args[1])) {
                    try {
                        module.key = args[1].toInt()
                        println("Module ${module.name} is now bound to ${args[1].toInt()}")
                        ToastClient.CONFIG_MANAGER.writeKeyBinds()
                    } catch (nfe: NumberFormatException) {
                        println("Failed")
                    }
                }
            } else {
                Logger.message("${args[0]} is not a valid module", Logger.ERR, true)
            }
        } else {
            Logger.message("Not enough arguments", Logger.ERR, true)
        }
    }
}