package toast.client.commands.cmds

import org.lwjgl.glfw.GLFW
import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.KeyUtil
import toast.client.utils.Logger
import java.util.*

class Macro : Command("Macro", """${ToastClient.cmdPrefix}macro [add/remove/list] <key> <command/message>""", "Allows you to bind a message to a key", false, "macros", "macro") {
    override fun run(args: Array<String>) {
        if (args.isEmpty()) {
            Logger.message("Missing arguments!", Logger.ERR, false)
            return
        }
        when (args[0]) {
            "add" -> {
                if (args.size >= 3) {
                    if (KeyUtil.isNumeric(args[1])) {
                        try {
                            ToastClient.CONFIG_MANAGER.loadMacros()
                            ToastClient.CONFIG_MANAGER.macros?.put(args[2], args[1].toInt())
                            ToastClient.CONFIG_MANAGER.writeMacros()
                            Logger.message("Added macro: ${args[1]} | ${args[2]}", Logger.INFO, false)
                        } catch (nfe: NumberFormatException) {
                            Logger.message("Failed to add macro.", Logger.ERR, false)
                        }
                    }
                    return
                }
                Logger.message("Missing arguments!", Logger.ERR, false)
            }
            "remove" -> {
                if (KeyUtil.isNumeric(args[1])) {
                    try {
                        println(args[1].toInt())
                        ToastClient.CONFIG_MANAGER.macros!!.forEach { (command: String, key: Int) ->
                            if (key == args[1].toInt()) {
                                ToastClient.CONFIG_MANAGER.loadMacros()
                                ToastClient.CONFIG_MANAGER.macros!!.remove(command)
                                ToastClient.CONFIG_MANAGER.writeMacros()
                                Logger.message("Removed macro: $key | $command", Logger.INFO, false)
                            }
                        }
                    } catch (nfe: NumberFormatException) {
                        Logger.message("Failed to remove macro.", Logger.ERR, false)
                    }
                }
            }
            "list" -> {
                val messages = ArrayList<String>()
                messages.add("KeyNum | KeyName | Message")
                ToastClient.CONFIG_MANAGER.macros!!.forEach { (command: String, key: Int) -> messages.add(key.toString() + " | " + GLFW.glfwGetKeyName(key, GLFW.glfwGetKeyScancode(key)) + " | " + command) }
                for (message in messages) {
                    Logger.message(message, Logger.INFO, false)
                }
            }
            else -> Logger.message("Could not parse command.", Logger.ERR, false)
        }
    }
}