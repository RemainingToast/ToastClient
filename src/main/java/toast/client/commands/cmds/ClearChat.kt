package toast.client.commands.cmds

import toast.client.ToastClient
import toast.client.commands.Command
import toast.client.utils.MessageUtil

/**
 * Command to wipe all messages from the in-game chat HUD
 */
class ClearChat : Command("ClearChat", "${ToastClient.cmdPrefix}clearchat", "Clears all messages in chat", false, "clearchat") {
    @Throws(InterruptedException::class)
    override fun run(args: Array<String>) {
        if (mc.player == null) return
        if (mc.inGameHud.chatHud != null) {
            mc.inGameHud.chatHud.clear(true)
            MessageUtil.sendMessage("Cleared chat!", MessageUtil.Color.GREEN)
        } else {
            MessageUtil.sendMessage("Fuck I don't know chat hud is null ¯\\_(ツ)_/¯", MessageUtil.Color.GRAY)
        }
    }
}