package dev.toastmc.client.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.client.command.util.*
import dev.toastmc.client.util.Color
import dev.toastmc.client.util.mc
import dev.toastmc.client.util.sendMessage
import net.minecraft.server.command.CommandSource

class Fov : Command("fov") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral("fov"){
            float("fov"){
                does { ctx ->
                    val float: Float = "fov" from ctx
                    if (!(float < 150 || float < 10)) {
                        mc.options.fov = float.toDouble()
                        sendMessage("Fov set to $float", Color.GREEN)
                } else sendMessage("Number out of bounds [10 - 150]", Color.RED)
                    0
                }
            }
        }
    }
}