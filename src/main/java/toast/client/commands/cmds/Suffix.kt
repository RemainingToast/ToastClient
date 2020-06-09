package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.modules.misc.CustomChat
import toast.client.utils.Logger

class Suffix : Command("Suffix", """${ToastClient.cmdPrefix}suffix [suffix]""", "Set custom chat ending", false, "suffix", "sufx") {
    var out: String = ""
    override fun run(args: Array<String>) {
        if (mc.player == null) return
        if (args.isNotEmpty()) {
            val message = StringBuilder()
            for (i in args.indices) {
                if (i > 0) message.append(" ")
                message.append(args[i])
                out = message.toString()
            }
            Logger.message("Set suffix to $out", Logger.INFO, false)
            CustomChat.suffix = out
        } else {
            Logger.message("Specify suffix!", Logger.ERR, false)
        }
    }
}