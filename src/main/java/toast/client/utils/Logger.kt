package toast.client.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting
import toast.client.ToastClient

object Logger {
    private val sb = StringBuilder()
    const val EMPTY = 0
    const val INFO = 1
    const val ERR = 2
    const val WARN = 3
    const val SUCCESS = 4
    private val INFO_PREFIX = Formatting.YELLOW.toString() + "" + Formatting.BOLD + " INFO:"
    private val ERR_PREFIX = Formatting.RED.toString() + "" + Formatting.BOLD + " ERROR:"
    private val WARN_PREFIX = Formatting.GOLD.toString() + "" + Formatting.BOLD + " WARN:"
    private val SUCCESS_PREFIX = Formatting.GREEN.toString() + "" + Formatting.BOLD + " SUCCESS:"
    private var i = 0

    private fun format(string: String, format: Formatting?) {
        sb.replace(0, sb.capacity(), "")
        val arr = string.split(" ").toTypedArray()
        for (ss in arr) {
            sb.append(" ").append(format).append(ss)
            i++
            if (arr.size == i) break
        }
    }

    @JvmStatic
    fun message(text: String, type: Int, prefix: Boolean) {
        var prefixString = ""
        when (type) {
            EMPTY -> {
                prefixString = ""
                sb.replace(0, sb.capacity(), text)
            }
            INFO -> {
                format(text, Formatting.YELLOW)
                if (prefix) prefixString = INFO_PREFIX
            }
            ERR -> {
                format(text, Formatting.RED)
                if (prefix) prefixString = ERR_PREFIX
            }
            WARN -> {
                format(text, Formatting.GOLD)
                if (prefix) prefixString = WARN_PREFIX
            }
            SUCCESS -> {
                format(text, Formatting.GREEN)
                if (prefix) prefixString = SUCCESS_PREFIX
            }
            else -> return
        }
        try {
            MinecraftClient.getInstance().inGameHud.chatHud
                .addMessage(LiteralText(ToastClient.chatPrefix + Formatting.RESET + prefixString + sb.toString()))
        } catch (ignored: Exception) {
            println("Failed send message in-game...")
        }
    }
}