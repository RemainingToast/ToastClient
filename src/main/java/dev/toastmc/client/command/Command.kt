package dev.toastmc.client.command

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient

@Environment(EnvType.CLIENT)
abstract class Command(var name: String, var usage: String, var desc: String, @JvmField vararg var aliases: String) {
    protected var mc: MinecraftClient = MinecraftClient.getInstance()
    @Throws(InterruptedException::class)
    abstract fun run(args: Array<String>)
}