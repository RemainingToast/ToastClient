package dev.toastmc.client.commands.cmds

import dev.toastmc.client.commands.Command
import dev.toastmc.client.commands.CommandManifest
import dev.toastmc.client.utils.MessageUtil
import dev.toastmc.client.utils.RandomMOTD

/**
 * Command to display a random MOTD (Message Of The Day) from the MOTD list
 */
@CommandManifest(label = "MOTD", description = "Random message of the day.", aliases = ["motd"])
class MOTD : Command() {
    override fun run(args: Array<String>) {
        MessageUtil.sendMessage(RandomMOTD.randomMOTD(), MessageUtil.Color.GRAY)
    }
}