package dev.toastmc.client.util

import net.minecraft.client.MinecraftClient
import net.minecraft.network.MessageType
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting

object MessageUtil {
    /*
     * Added by RemainingToast 12/07/20
     */
    val CHAT_PREFIX =
        Formatting.DARK_GRAY.toString() + "[" + Formatting.RED + Formatting.BOLD + "Toast" + Formatting.DARK_GRAY + "] "
    private val mc = MinecraftClient.getInstance()
    fun sendRawMessage(message: String?) {
        assert(mc.player != null)
        mc.inGameHud.addChatMessage(
            MessageType.CHAT,
            LiteralText(message),
            mc.player!!.uuid
        )
    }

    fun sendPublicMessage(message: String?) {
        if (mc.player == null) return
        mc.player!!.sendChatMessage(message)
    }

    fun defaultErrorMessage() {
        sendRawMessage(CHAT_PREFIX + Formatting.RED + "Computer Says No.")
    }

    fun sendMessage(message: String, color: Color?) {
        when (color) {
            Color.DARK_RED -> sendRawMessage(CHAT_PREFIX + Formatting.DARK_RED + message)
            Color.RED -> sendRawMessage(CHAT_PREFIX + Formatting.RED + message)
            Color.GOLD -> sendRawMessage(CHAT_PREFIX + Formatting.GOLD + message)
            Color.YELLOW -> sendRawMessage(CHAT_PREFIX + Formatting.YELLOW + message)
            Color.DARK_GREEN -> sendRawMessage(CHAT_PREFIX + Formatting.DARK_GREEN + message)
            Color.GREEN -> sendRawMessage(CHAT_PREFIX + Formatting.GREEN + message)
            Color.AQUA -> sendRawMessage(CHAT_PREFIX + Formatting.AQUA + message)
            Color.DARK_AQUA -> sendRawMessage(CHAT_PREFIX + Formatting.DARK_AQUA + message)
            Color.DARK_BLUE -> sendRawMessage(CHAT_PREFIX + Formatting.DARK_BLUE + message)
            Color.BLUE -> sendRawMessage(CHAT_PREFIX + Formatting.BLUE + message)
            Color.LIGHT_PURPLE -> sendRawMessage(CHAT_PREFIX + Formatting.LIGHT_PURPLE + message)
            Color.DARK_PURPLE -> sendRawMessage(CHAT_PREFIX + Formatting.DARK_PURPLE + message)
            Color.WHITE -> sendRawMessage(CHAT_PREFIX + Formatting.WHITE + message)
            Color.DARK_GRAY -> sendRawMessage(CHAT_PREFIX + Formatting.DARK_GRAY + message)
            Color.BLACK -> sendRawMessage(CHAT_PREFIX + Formatting.BLACK + message)
            else -> sendRawMessage(CHAT_PREFIX + Formatting.GRAY + message)
        }
    }

    //A - Z Please
    enum class Color {
        DARK_RED, RED, GOLD, YELLOW, DARK_GREEN, GREEN, AQUA, DARK_AQUA, DARK_BLUE, BLUE, LIGHT_PURPLE, DARK_PURPLE, WHITE, GRAY, DARK_GRAY, BLACK
    }
}