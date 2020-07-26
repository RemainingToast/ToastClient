package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.MessageUtil

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