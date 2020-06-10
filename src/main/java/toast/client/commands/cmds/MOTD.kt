package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.Logger
import toast.client.utils.RandomMOTD

/**
 * Command to display a random MOTD (Message Of The Day) from the MOTD list
 */
class MOTD : Command("MOTD", """${ToastClient.cmdPrefix}motd""", "Shows random MOTD", false, "motd") {
    override fun run(args: Array<String>) {
        Logger.message(RandomMOTD.randomMOTD(), Logger.EMPTY, false)
    }
}