package dev.toastmc.client.commands.cmds

import net.minecraft.util.Formatting
import dev.toastmc.client.ToastClient
import dev.toastmc.client.commands.Command
import dev.toastmc.client.commands.CommandManifest
import dev.toastmc.client.utils.MessageUtil

/**
 * Command to display a list of available commands and how to use them
 */
@CommandManifest(label = "Help", usage = "help [command]", description =  "Shows all commands", aliases = ["listcommands", "help", "commands"])
class Help : Command() {
    /**
     * Turns an array of strings into a single string
     */
    private fun loopAppend(arrayToAppend: List<String>?): StringBuilder {
        val out = StringBuilder()
        var i = 0
        if (arrayToAppend != null) {
            for (ss in arrayToAppend) {
                out.append(" ").append(Formatting.YELLOW).append(ss)
                i++
                if (arrayToAppend.size == i) {
                    break
                }
            }
        }
        return out
    }

    override fun run(args: Array<String>) {
        val sb: StringBuilder = StringBuilder()
        lateinit var out: String
        if (mc.player == null) return
        if (args.isEmpty()) {
            sb.replace(0, sb.capacity(), "")
            for (command in ToastClient.COMMAND_HANDLER.commands) {
                if (command.getLabel() == "Help") continue
                sb.append("${Formatting.GRAY}${command.getLabel()}, ")
            }
            sb.replace(sb.lastIndexOf(", "), sb.lastIndexOf(", ") + 1, "")
            MessageUtil.sendMessage("Commands (${ToastClient.COMMAND_HANDLER.commands.size}): $sb", MessageUtil.Color.GRAY)
        } else {
            val c = args[0].replaceFirst("\\.".toRegex(), "")
            val cmd = ToastClient.COMMAND_HANDLER.getCommand(c)
            if (cmd == null) {
                MessageUtil.sendMessage("Cannot find command $c", MessageUtil.Color.RED)
                return
            }
            val msg = StringBuilder()
            sb.replace(0, sb.capacity(), "")
            val desc1: List<String>? = cmd.getDescription()?.split(" ".toRegex())
            val usage1: List<String>? = cmd.getUsage()?.split(" ".toRegex())
            val usage = loopAppend(usage1)
            msg.append("Help for Command: ${Formatting.GRAY}${cmd.getLabel()}")
            msg.append("\n").append(Formatting.GRAY).append(" - Aliases: ${Formatting.YELLOW}${ToastClient.cmdPrefix}${cmd.getAlias()}\n")
            msg.append(Formatting.GRAY).append(" - Usage: ${Formatting.YELLOW}${usage}\n")
            msg.append(Formatting.GRAY).append(" - Description: ${Formatting.YELLOW}${desc1}\n")
            out = msg.toString()
            MessageUtil.sendRawMessage(out)
        }
    }
}