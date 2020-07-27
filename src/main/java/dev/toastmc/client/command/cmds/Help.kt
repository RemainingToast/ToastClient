package dev.toastmc.client.command.cmds

import dev.toastmc.client.ToastClient
import dev.toastmc.client.ToastClient.Companion.CMD_PREFIX
import dev.toastmc.client.ToastClient.Companion.COMMAND_MANAGER
import dev.toastmc.client.command.Command
import dev.toastmc.client.util.MessageUtil
import net.minecraft.util.Formatting

class Help : Command("Help", "help [command]", "Shows all commands", "help", "commands") {
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
            for (command in COMMAND_MANAGER.commands) {
//                if (command.name == "Help") continue
                sb.append("${Formatting.GRAY}${command.name}, ")
            }
            sb.replace(sb.lastIndexOf(", "), sb.lastIndexOf(", ") + 1, "")
            MessageUtil.sendMessage("Commands (${COMMAND_MANAGER.commands.size}): $sb", MessageUtil.Color.GRAY)
        } else {
            val c = args[0].replaceFirst("\\.".toRegex(), "")
            val cmd = COMMAND_MANAGER.getCommand(c)
            if (cmd == null) {
                MessageUtil.sendMessage("Cannot find command $c : $usage", MessageUtil.Color.RED)
                return
            }
            val msg = StringBuilder()
            sb.replace(0, sb.capacity(), "")
            val desc1: Array<String?> = cmd.desc.split(" ".toRegex()).toTypedArray()
            val usage1: Array<String?> = cmd.usage.split(" ".toRegex()).toTypedArray()
            val desc = loopAppend(desc1)
            val usage = loopAppend(usage1)
            msg.append("Help for Command: ").append(Formatting.YELLOW).append(cmd.name)
            msg.append("\n").append(Formatting.GRAY).append(" - Aliases: ").append(Formatting.YELLOW).append(CMD_PREFIX).append(java.lang.String.join(Formatting.GRAY.toString() + ", " + Formatting.YELLOW + CMD_PREFIX, *cmd.aliases)).append("\n")
            msg.append(Formatting.GRAY).append(" - Usage:").append(Formatting.YELLOW).append(usage.toString()).append("\n")
            msg.append(Formatting.GRAY).append(" - Description:").append(Formatting.YELLOW).append(desc.toString())
            out = msg.toString()
            MessageUtil.sendRawMessage(out)
        }
    }
}