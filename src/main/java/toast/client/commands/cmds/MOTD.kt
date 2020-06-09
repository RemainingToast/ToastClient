package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.Logger
import toast.client.utils.RandomMOTD

class MOTD : Command("MOTD", """${ToastClient.cmdPrefix}motd""", "Shows random MOTD", false, "motd") {
    override fun run(args: Array<String>) {
        Logger.message(RandomMOTD.randomMOTD(), Logger.EMPTY, false)
    }
}