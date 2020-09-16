package dev.toastmc.client.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.client.command.util.*
import dev.toastmc.client.util.Color
import dev.toastmc.client.util.mc
import dev.toastmc.client.util.sendMessage
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.server.command.CommandSource
import net.minecraft.util.Formatting
import java.text.DecimalFormat

@Cmd(name = "pos")
class Pos : Command() {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral("pos") {
            does {
                mc.keyboard.clipboard = formatPlayerCoords(mc.player)
                sendMessage("${Formatting.RED}!!${Formatting.GREEN} Coordinates saved to clipboard ${Formatting.RED}!!", Color.GREEN)
                0
            }
        }
    }

    private fun formatPlayerCoords(player: ClientPlayerEntity?): String? {
        val format = DecimalFormat("#.#")
        val x: String = format.format(player?.x)
        val y: String = format.format(player?.y)
        val z: String = format.format(player?.z)
        return "$x, $y, $z"
    }
}