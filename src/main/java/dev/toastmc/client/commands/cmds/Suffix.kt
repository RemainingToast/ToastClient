package dev.toastmc.client.commands.cmds

import dev.toastmc.client.ToastClient
import dev.toastmc.client.commands.Command
import dev.toastmc.client.commands.CommandManifest
import dev.toastmc.client.modules.misc.CustomChat
import dev.toastmc.client.utils.MessageUtil

/**
 * Command to change the CustomChat suffix
 */
@CommandManifest(label = "Suffix", usage = "suffix [suffix]", description = "Set custom chat ending!")
class Suffix : Command() {
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
            MessageUtil.sendMessage("Set suffix to $out", MessageUtil.Color.GREEN)
            CustomChat.suffix = out
        } else {
            MessageUtil.sendMessage("Specify suffix!", MessageUtil.Color.RED)
        }
    }
}