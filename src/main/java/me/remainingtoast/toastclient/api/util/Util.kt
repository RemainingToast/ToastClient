package me.remainingtoast.toastclient.api.util

import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText

val mc = MinecraftClient.getInstance()

fun lit(string: String): LiteralText {
    return LiteralText(string)
}

