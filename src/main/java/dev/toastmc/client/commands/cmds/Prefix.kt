package dev.toastmc.client.commands.cmds

import dev.toastmc.client.ToastClient
import dev.toastmc.client.commands.Command
import dev.toastmc.client.utils.MessageUtil

/**
 * Command to change the command prefix
 */
class Prefix : Command("Prefix", """${ToastClient.cmdPrefix}prefix [prefix]""", "Rebind command prefix", false, "prefix") {
    override fun run(args: Array<String>) {
        if (ToastClient.cmdPrefix != null) {
            ToastClient.cmdPrefix = args[0]
            MessageUtil.sendMessage("Command prefix set to: ${ToastClient.cmdPrefix}", MessageUtil.Color.GREEN)
        }
    }
}