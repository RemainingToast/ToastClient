package toast.client.modules.misc

import com.google.common.eventbus.Subscribe
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
import toast.client.ToastClient
import toast.client.events.network.EventPacketSent
import toast.client.modules.Module
import toast.client.utils.FancyChatUtil

class CustomChat : Module("CustomChat", "Custom chat messages", Category.MISC, -1) {
    private var isMadeByCustomChat = false
    override fun onEnable() {
        isMadeByCustomChat = true
    }

    @Subscribe
    fun onEvent(e: EventPacketSent) {
        if (mc.player == null) return
        if (e.getPacket() is ChatMessageC2SPacket) {
            if (!getBool("Custom Suffix")) suffix = "ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ"
            val packetMessage = (e.getPacket() as ChatMessageC2SPacket).chatMessage
            var message = packetMessage
            if (packetMessage.startsWith(ToastClient.cmdPrefix) || packetMessage.startsWith("/") && !getBool("Commands")) return
            if (getBool("Fancy Chat")) {
                when (settings.getMode("Fancy chat type")) {
                    "Classic" -> message = FancyChatUtil.classicFancy(packetMessage)
                    "Retard" -> message = FancyChatUtil.retardChat(packetMessage)
                    "Spaced" -> message = FancyChatUtil.spaces(packetMessage)
                }
            }
            when (settings.getMode("Separator")) {
                "None" -> message = FancyChatUtil.customSuffix(message, " ", suffix, "", false)
                "Default" -> message = FancyChatUtil.customSuffix(message, " | ", suffix, "", false)
                "Brackets" -> message = FancyChatUtil.customSuffix(message, " < ", suffix, " > ", true)
            }
            isMadeByCustomChat = !isMadeByCustomChat
            if (isMadeByCustomChat) return
            e.isCancelled = true
            mc.player!!.sendChatMessage(message)
        }
    }

    companion object {
        @JvmField
        var suffix: String = ""
    }

    init {
        settings.addBoolean("Fancy Chat", false)
        settings.addBoolean("Commands", false)
        settings.addBoolean("Custom Suffix", false)
        settings.addMode("Separator", "None", "None", "Default", "Brackets")
        settings.addMode("Fancy chat type", "Retard", "Classic", "Retard", "Grammar", "Spaced")
    }
}