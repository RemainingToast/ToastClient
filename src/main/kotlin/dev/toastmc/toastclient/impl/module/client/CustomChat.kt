package dev.toastmc.toastclient.impl.module.client

import dev.toastmc.toastclient.api.module.Module
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
import java.util.*

object CustomChat : Module("CustomChat", Category.CLIENT) {

    var isMadeByCustomChat = false

    var suffix: String = "ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ"

    var suffixBool = bool("Suffix", true)
    var fancyChat = bool("Fancy Chat", false)
    var commands = bool("Commands", false)

    var fancyChatType = mode(
        "Fancy Chat Type",
        "Classic",
        "Retard", "Classic", "Spaced"
    )

    var separator = mode(
        "Separator",
        "none",
        "none", "default", "brackets"
    )

    override fun onEnable() {
        isMadeByCustomChat = true
    }

//    @EventHandler
//    private val packetListener = me.zero.alpine.listener.Listener(EventHook<PacketEvent.Receive> {
//        if (mc.player == null) return@EventHook
//        if (it.packet is ChatMessageC2SPacket){
//            val chatMessage = it.packet.chatMessage
//            if ((chatMessage.startsWith(ToastClient.CMD_PREFIX) || chatMessage.startsWith("/")) && !commands.value) return@EventHook
//            suffix = when (separator.index){
//                0 -> " ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ"
//                1 -> " | ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ"
//                2 -> " < ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ >"
//                else -> ""
//            }
//            if(fancyChat.value){
//                var fancyMsg = when (fancyChatType.index){
//                    0 -> FancyChatUtil.retardChat(chatMessage)
//                    1 -> FancyChatUtil.classicFancy(chatMessage)
//                    2 -> FancyChatUtil.spaces(chatMessage)
//                    else -> chatMessage
//                }
//                isMadeByCustomChat = !isMadeByCustomChat
//                if(isMadeByCustomChat) return@EventHook
//                it.cancel()
//                mc.player!!.sendChatMessage(fancyMsg + suffix)
//            } else {
//                isMadeByCustomChat = !isMadeByCustomChat
//                if(isMadeByCustomChat) return@EventHook
//                it.cancel()
//                mc.player!!.sendChatMessage(chatMessage + suffix)
//            }
//        }
//    })
}