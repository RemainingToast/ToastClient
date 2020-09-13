package dev.toastmc.client.util

import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import java.io.File

val mc = MinecraftClient.getInstance()
val MOD_DIRECTORY: File = File(mc.runDirectory, "toastclient" + "/")

fun lit(string: String): LiteralText {
    return LiteralText(string)
}