package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.Logger

class Prefix : Command("Prefix", """${ToastClient.cmdPrefix}prefix [prefix]""", "Rebind command prefix", false, "prefix") {
    override fun run(args: Array<String>) {
        if (ToastClient.cmdPrefix != null) {
            ToastClient.cmdPrefix = args[0]
            Logger.message("Command prefix set to: ${ToastClient.cmdPrefix}", Logger.INFO, true)
        }
    }
}