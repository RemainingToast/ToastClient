package dev.toastmc.client.commands.cmds

import dev.toastmc.client.ToastClient
import dev.toastmc.client.commands.Command
import dev.toastmc.client.commands.CommandManifest
import dev.toastmc.client.utils.MessageUtil

/**
 * Command to wipe all messages from the in-game chat HUD
 */
@CommandManifest(label = "ClearChat", usage = "clearchat", description = "Clears all messages in chat", aliases = ["clearchat", "cc", "chat"])
class ClearChat : Command() {
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