package toast.client.commands.cmds

import net.minecraft.util.Formatting
import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.MessageUtil

/**
 * Command to display a list of available commands and how to use them
 */
class Help : Command("Help", "${ToastClient.cmdPrefix}help [command]", "Shows all commands", false, "help", "commands") {
    /**
     * Turns an array of strings into a single string
     */
    private fun loopAppend(arrayToAppend: Array<String?>): StringBuilder {
        val out = StringBuilder()
        var i = 0
        for (ss in arrayToAppend) {
            out.append(" ").append(Formatting.YELLOW).append(ss)
            i++
            if (arrayToAppend.size == i) {
                break
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
                if (ToastClient.COMMAND_HANDLER.isDevCancel(command)) continue
                if (command.name == "Help") continue
                sb.append(Formatting.GRAY).append(command.name).append(", ")
            }
            sb.replace(sb.lastIndexOf(", "), sb.lastIndexOf(", ") + 1, "")
            MessageUtil.sendMessage("Commands (${ToastClient.COMMAND_HANDLER.commands.size}): $sb", MessageUtil.Color.GRAY)
        } else {
            val c = args[0].replaceFirst("\\.".toRegex(), "")
            val cmd = ToastClient.COMMAND_HANDLER.getCommand(c)
            if (cmd == null || ToastClient.COMMAND_HANDLER.isDevCancel(cmd)) {
                MessageUtil.sendMessage("Cannot find command $c", MessageUtil.Color.RED)
                return
            }
            val msg = StringBuilder()
            sb.replace(0, sb.capacity(), "")
            val desc1: Array<String?> = cmd.desc.split(" ".toRegex()).toTypedArray()
            val usage1: Array<String?> = cmd.usage.split(" ".toRegex()).toTypedArray()
            val desc = loopAppend(desc1)
            val usage = loopAppend(usage1)
            msg.append("Help for Command: ").append(Formatting.YELLOW).append(cmd.name)
            msg.append("\n").append(Formatting.GRAY).append(" - Aliases: ").append(Formatting.YELLOW).append(ToastClient.cmdPrefix).append(java.lang.String.join(Formatting.GRAY.toString() + ", " + Formatting.YELLOW + ToastClient.cmdPrefix, *cmd.aliases)).append("\n")
            msg.append(Formatting.GRAY).append(" - Usage:").append(Formatting.YELLOW).append(usage.toString()).append("\n")
            msg.append(Formatting.GRAY).append(" - Description:").append(Formatting.YELLOW).append(desc.toString())
            out = msg.toString()
            MessageUtil.sendRawMessage(out)
        }
    }
}