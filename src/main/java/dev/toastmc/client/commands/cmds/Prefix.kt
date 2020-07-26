package dev.toastmc.client.commands.cmds

import dev.toastmc.client.ToastClient
import dev.toastmc.client.commands.Command
import dev.toastmc.client.commands.CommandManifest
import dev.toastmc.client.utils.MessageUtil

/**
 * Command to change the command prefix
 */
@CommandManifest(label = "Prefix", usage = "prefix [prefix]", description = "Rebind command prefix")
class Prefix : Command() {
    override fun run(args: Array<String>) {
        if (ToastClient.cmdPrefix != null) {
            ToastClient.cmdPrefix = args[0]
            MessageUtil.sendMessage("Command prefix set to: ${ToastClient.cmdPrefix}", MessageUtil.Color.GREEN)
        }
    }
}