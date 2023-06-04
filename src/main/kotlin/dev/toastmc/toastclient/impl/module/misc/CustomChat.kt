package dev.toastmc.toastclient.impl.module.misc

import dev.toastmc.toastclient.api.events.PacketEvent
import dev.toastmc.toastclient.api.managers.command.CommandManager
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.FancyChatUtil
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
import org.quantumclient.energy.Subscribe

object CustomChat : Module("CustomChat", Category.MISC) {

    var isMadeByCustomChat = false

    var suffix: String = "ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ"

    var suffixBool = bool("Suffix", true)
    var fancyChat = bool("FancyChat", false)
    var commands = bool("Commands", false)

    var fancyChatType = mode(
        "Fancy Chat Type",
        "Classic",
        "Retard", "Classic", "Spaced"
    )

    var separator = mode(
        "Separator",
        "None",
        "None", "Default", "Brackets"
    )

    override fun onEnable() {
        isMadeByCustomChat = true
    }

    @Subscribe
    fun on(event: PacketEvent.Receive) {
        if (mc.player == null) return
        if (event.packet is ChatMessageC2SPacket){
            val chatMessage = event.packet.chatMessage
            if ((chatMessage.startsWith(CommandManager.prefix) || chatMessage.startsWith("/")) && !commands.value) return
            suffix = when (separator.value){
                "None" -> " ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ"
                "Default" -> " | ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ"
                "Brackets" -> " < ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ >"
                else -> " ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ"
            }
            if(fancyChat.value){
                val fancyMsg = when (fancyChatType.value){
                    "Retard" -> FancyChatUtil.retardChat(chatMessage)
                    "Classic" -> FancyChatUtil.classicFancy(chatMessage)
                    "Fancy" -> FancyChatUtil.spaces(chatMessage)
                    else -> chatMessage
                }
                isMadeByCustomChat = !isMadeByCustomChat
                if(isMadeByCustomChat) return
                event.cancel()
                mc.player!!.sendChatMessage(fancyMsg + suffix, null)
            } else {
                isMadeByCustomChat = !isMadeByCustomChat
                if(isMadeByCustomChat) return
                event.cancel()
                mc.player!!.sendChatMessage(chatMessage + suffix, null)
            }
        }
    }
}