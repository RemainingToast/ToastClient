package dev.toastmc.toastclient.api.util

import dev.toastmc.toastclient.mixin.client.IChatHud
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

fun lit(string: String): LiteralText {
    return LiteralText(string)
}

fun message(text: Text) {
    if (mc.player != null) (mc.inGameHud.chatHud as IChatHud).callAddMessage(text, 5932)
}

fun message(str: String) {
    message(lit(str))
}

fun capitalize(str: String): String {
    return if (str.isEmpty()) { str }
    else str.substring(0, 1).toUpperCase() + str.substring(1)
}
