package toast.client.commands.cmds

import org.lwjgl.glfw.GLFW
import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.KeyUtil
import toast.client.utils.Logger
import java.util.*

/**
 * Command to add, remove and list existing macros
 */
class Macro : Command("Macro", """${ToastClient.cmdPrefix}macro [add/remove/list] <key> <command/message>""", "Allows you to bind a message to a key", false, "macros", "macro") {
    override fun run(args: Array<String>) {
        if (args.isEmpty()) {
            Logger.message("Missing arguments!", Logger.ERR, false)
            return
        }
        when (args[0]) {
            "add" -> {
                if (args.size >= 3) {
                    val key: String = args[1]
                    if (KeyUtil.isNumeric(args[1])) {
                        try {
                            var command = ""
                            for (x in 2 until args.size) {
                                command += args[x]
                            }
                            ToastClient.CONFIG_MANAGER.loadMacros()
                            ToastClient.CONFIG_MANAGER.addMacro(command, args[1].toInt())
                            Logger.message("Added macro: $key | $command", Logger.INFO, false)
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
                        for ((command, key) in ToastClient.CONFIG_MANAGER.getMacros()
                                ?: throw NumberFormatException()) {
                            if (key == args[1].toInt()) {
                                ToastClient.CONFIG_MANAGER.loadMacros()
                                (ToastClient.CONFIG_MANAGER.getMacros() ?: continue).remove(command)
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
                (ToastClient.CONFIG_MANAGER.getMacros() ?: return).forEach { (command, key) ->
                    messages.add(key.toString() + " | " + GLFW.glfwGetKeyName(key
                            ?: return@forEach, GLFW.glfwGetKeyScancode(key)) + " | " + command)
                }
                for (message in messages) {
                    Logger.message(message, Logger.INFO, false)
                }
            }
            else -> Logger.message("Could not parse command.", Logger.ERR, false)
        }
    }
}