package dev.toastmc.client.commands.cmds

import dev.toastmc.client.ToastClient
import dev.toastmc.client.commands.Command
import dev.toastmc.client.utils.MessageUtil
import toast.client.utils.RandomMOTD

/**
 * Command to display a random MOTD (Message Of The Day) from the MOTD list
 */
class MOTD : Command("MOTD", """${ToastClient.cmdPrefix}motd""", "Shows random MOTD", false, "motd") {
    override fun run(args: Array<String>) {
        MessageUtil.sendMessage(RandomMOTD.randomMOTD(), MessageUtil.Color.GRAY)
    }
}