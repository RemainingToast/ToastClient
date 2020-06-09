package toast.client.commands

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient

@Environment(EnvType.CLIENT)
abstract class Command(var name: String, var usage: String, desc: String, var isDev: Boolean, vararg aliases: String) {
    @JvmField
    var aliases: Array<String> = aliases as Array<String>
    var desc: String = desc
    protected var mc: MinecraftClient = MinecraftClient.getInstance()

    @Throws(InterruptedException::class)
    abstract fun run(args: Array<String>)

}