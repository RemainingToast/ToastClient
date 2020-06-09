package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.Logger

class ClearChat : Command("ClearChat", "${ToastClient.cmdPrefix}clearchat", "Clears all messages in chat", false, "clearchat") {
    @Throws(InterruptedException::class)
    override fun run(args: Array<String>) {
        if (mc.player == null) return
        if (mc.inGameHud.chatHud != null) {
            mc.inGameHud.chatHud.clear(true)
            Logger.message("Cleared chat", Logger.EMPTY, false)
        } else {
            Logger.message("Fuck I don't know chat hud is null ¯\\_(ツ)_/¯", Logger.EMPTY, false)
        }
    }
}