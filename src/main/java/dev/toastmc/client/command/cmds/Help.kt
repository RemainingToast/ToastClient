package dev.toastmc.client.command.cmds

import dev.toastmc.client.ToastClient.Companion.COMMAND_MANAGER
import dev.toastmc.client.command.Command
import dev.toastmc.client.command.CommandManifest
import dev.toastmc.client.util.MessageUtil
import net.minecraft.util.Formatting

@CommandManifest(
        label = "Help",
        description = "Lists Commands",
        aliases = ["help", "commands"])
class Help : Command() {
    override fun run(args: Array<String>) {
        val sb: StringBuilder = StringBuilder()
        if (mc.player == null) return
        if (args.isEmpty()) {
            sb.replace(0, sb.capacity(), "")
            for (command in COMMAND_MANAGER.commands) {
//                if (command.getLabel() == "Help") continue
                sb.append("${Formatting.GRAY}${command.getLabel()}, ")
            }
            sb.replace(sb.lastIndexOf(", "), sb.lastIndexOf(", ") + 1, "")
            MessageUtil.sendMessage("Commands (${COMMAND_MANAGER.commands.size}): $sb", MessageUtil.Color.GRAY)
        }
    }
}