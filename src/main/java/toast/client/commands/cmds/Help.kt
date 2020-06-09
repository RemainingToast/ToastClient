package toast.client.commands.cmds

import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting
import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.Logger

class Help : Command("Help", "${ToastClient.cmdPrefix}help [command]", "Shows all commands", false, "help", "commands") {
    var sb = StringBuilder()
    var out: String? = null
    override fun run(args: Array<String>) {
        if (mc.player == null) return
        if (args.isEmpty()) {
            sb.replace(0, sb.capacity(), "")
            var i = 0
            for (command in ToastClient.COMMAND_HANDLER.commands) {
                i++
                if (ToastClient.COMMAND_HANDLER.isDevCancel(command)) continue
                if (command.name == "Help") continue
                sb.append(Formatting.GRAY).append(command.name).append(", ")
                if (ToastClient.COMMAND_HANDLER.commands.size == i) {
                    break
                }
            }
            MinecraftClient.getInstance().inGameHud.chatHud.addMessage(LiteralText("${ToastClient.chatPrefix}${Formatting.GRAY} Commands (${ToastClient.COMMAND_HANDLER.commands.size}): $sb"))
        } else {
            val c = args[0].replaceFirst("\\.".toRegex(), "")
            val cmd = ToastClient.COMMAND_HANDLER.getCommand(c)
            if (cmd == null || ToastClient.COMMAND_HANDLER.isDevCancel(cmd)) {
                Logger.message("Cannot find command $c", Logger.ERR, false)
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
            Logger.message(out!!, Logger.EMPTY, false)
        }
    }

    companion object {
        fun loopAppend(arrayToAppend: Array<String?>): StringBuilder {
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
    }
}