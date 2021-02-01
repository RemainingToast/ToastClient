package me.remainingtoast.toastclient.client.module.client

import me.remainingtoast.toastclient.ToastClient
import me.remainingtoast.toastclient.api.event.PacketEvent
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.type.BooleanSetting
import me.remainingtoast.toastclient.api.setting.type.EnumSetting
import me.remainingtoast.toastclient.api.util.FancyChatUtil
import me.remainingtoast.toastclient.api.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket

object CustomChat : Module("CustomChat", Category.CLIENT) {

    var isMadeByCustomChat = false

    var suffix: String = "ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ"

    var suffixBool: BooleanSetting = registerBoolean("Suffix", true)
    var fancyChat: BooleanSetting = registerBoolean("Fancy Chat", false)
    var commands: BooleanSetting = registerBoolean("Commands", false)

    enum class FancyChatType {
        RETARD,
        CLASSIC,
        SPACED
    }

    enum class Separator {
        NONE,
        DEFAULT,
        BRACKETS
    }

    var fancyChatType: EnumSetting<FancyChatType> = registerEnum(FancyChatType.CLASSIC, "Fancy Chat Type", "Style of Fancy Chat")
    var separator: EnumSetting<Separator> = registerEnum(Separator.NONE, "Separator", "")

    override fun onEnable() {
        isMadeByCustomChat = true
        ToastClient.EVENT_BUS.subscribe(packetListener)
    }

    override fun onDisable() {
        ToastClient.EVENT_BUS.unsubscribe(packetListener)
    }

    @EventHandler
    private val packetListener = me.zero.alpine.listener.Listener(EventHook<PacketEvent.Receive> {
        if (mc.player == null) return@EventHook
        if (it.packet is ChatMessageC2SPacket){
            val chatMessage = it.packet.chatMessage
            if ((chatMessage.startsWith(ToastClient.CMD_PREFIX) || chatMessage.startsWith("/")) && !commands.value) return@EventHook
            suffix = when (separator.value){
                Separator.NONE -> " ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ"
                Separator.DEFAULT -> " | ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ"
                Separator.BRACKETS -> " < ᴛᴏᴀѕᴛᴄʟɪᴇɴᴛ >"
            }
            if(fancyChat.value){
                var fancyMsg = when (fancyChatType.value){
                    FancyChatType.RETARD -> FancyChatUtil.retardChat(chatMessage)
                    FancyChatType.CLASSIC -> FancyChatUtil.classicFancy(chatMessage)
                    FancyChatType.SPACED -> FancyChatUtil.spaces(chatMessage)
                }
                isMadeByCustomChat = !isMadeByCustomChat
                if(isMadeByCustomChat) return@EventHook
                it.cancel()
                mc.player!!.sendChatMessage(fancyMsg + suffix)
            } else {
                isMadeByCustomChat = !isMadeByCustomChat
                if(isMadeByCustomChat) return@EventHook
                it.cancel()
                mc.player!!.sendChatMessage(chatMessage + suffix)
            }
        }
    })
}