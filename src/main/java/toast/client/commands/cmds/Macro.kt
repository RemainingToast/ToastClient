package toast.client.commands.cmds

import org.lwjgl.glfw.GLFW
import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.KeyUtil
import toast.client.utils.MessageUtil
import java.util.*

/**
 * Command to add, remove and list existing macros
 */
class Macro : Command("Macro", """${ToastClient.cmdPrefix}macro [add/remove/list] <key> <command/message>""", "Allows you to bind a message to a key", false, "macros", "macro") {
    override fun run(args: Array<String>) {
        if (args.isEmpty()) {
            MessageUtil.sendMessage("Missing arguments!", MessageUtil.Color.RED)
            return
        }
        when (args[0]) {
            "add" -> {
                if (args.size >= 3) {
                    val keyCode = KeyUtil.getKeyCode(args[1])
                    if (keyCode == -1) {
                        MessageUtil.sendMessage("No Key by the name of ${args[1]} was found.", MessageUtil.Color.RED)
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
                        MessageUtil.sendMessage("Added macro: ${args[1]} | $command", MessageUtil.Color.GREEN)
                    } catch (nfe: NumberFormatException) {
                        MessageUtil.sendMessage("Failed to add macro.", MessageUtil.Color.RED)
                    }
                    return
                }
                MessageUtil.sendMessage("Missing arguments!", MessageUtil.Color.RED)
            }
            "remove" -> {
                val keyCode = KeyUtil.getKeyCode(args[1])
                if (keyCode == -1) {
                    MessageUtil.sendMessage("No Key by the name of ${args[1]} was found.", MessageUtil.Color.RED)
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
                            MessageUtil.sendMessage("Removed macro: ${args[1]} | ${next.key}", MessageUtil.Color.GREEN)
                            return
                        }
                    }
                    MessageUtil.sendMessage("No macro bound to ${args[1]} was found.", MessageUtil.Color.RED)
                } catch (t: Throwable) {
                    MessageUtil.sendMessage("Failed to remove macro.", MessageUtil.Color.RED)
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
                    MessageUtil.sendMessage(message, MessageUtil.Color.GRAY)
                }
            }
            else -> MessageUtil.sendMessage("Could not parse command.", MessageUtil.Color.RED)
        }
    }
}