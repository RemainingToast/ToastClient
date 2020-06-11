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
                    val keyCode = KeyUtil.getKeyCode(args[1])
                    if (keyCode == -1) {
                        Logger.message("No Key by the name of ${args[1]} was found.", Logger.ERR, false)
                        return
                    }
                    try {
                        var command = args[2]
                        if (args.size > 3) {
                            for (x in 3 until args.size) {
                                command += " ${args[x]}"
                            }
                        }
                        ToastClient.CONFIG_MANAGER.loadMacros()
                        ToastClient.CONFIG_MANAGER.addMacro(command, keyCode)
                        Logger.message("Added macro: ${args[1]} | $command", Logger.INFO, false)
                    } catch (nfe: NumberFormatException) {
                        Logger.message("Failed to add macro.", Logger.ERR, false)
                    }
                    return
                }
                Logger.message("Missing arguments!", Logger.ERR, false)
            }
            "remove" -> {
                val keyCode = KeyUtil.getKeyCode(args[1])
                if (keyCode == -1) {
                    Logger.message("No Key by the name of ${args[1]} was found.", Logger.ERR, false)
                    return
                }
                try {
                    println(keyCode)
                    val macroIter = (ToastClient.CONFIG_MANAGER.getMacros() ?: return).iterator()
                    while (macroIter.hasNext()) {
                        val next = macroIter.next()
                        if (next.value == keyCode) {
                            ToastClient.CONFIG_MANAGER.loadMacros()
                            (ToastClient.CONFIG_MANAGER.getMacros() ?: continue).remove(next.key)
                            ToastClient.CONFIG_MANAGER.writeMacros()
                            Logger.message("Removed macro: ${args[1]} | ${next.key}", Logger.INFO, false)
                            return
                        }
                    }
                    Logger.message("No macro bound ${args[1]} was found.", Logger.ERR, false)
                } catch (t: Throwable) {
                    Logger.message("Failed to remove macro.", Logger.ERR, false)
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