package dev.toastmc.client.command.cmds

import dev.toastmc.client.command.Command
import dev.toastmc.client.command.CommandManifest
import dev.toastmc.client.util.*

@CommandManifest(
    label = "Highest",
    description = "Gets y value of highest block at given xz or player xz",
    usage = "No arguments || [x z]",
    aliases = ["high"]
)
class Highest : Command() {
    override fun run(args: Array<String>) {
        if (mc.player == null) return
        val enoughArgs = args.size >= 2
        mc.world!!.getChunk(mc.player!!.chunkX, mc.player!!.chunkZ).height
        val x = if (enoughArgs) args[0].toIntOrNull() ?: mc.player!!.blockPos.x else mc.player!!.blockPos.x
        val z = if (enoughArgs) args[1].toIntOrNull() ?: mc.player!!.blockPos.z else mc.player!!.blockPos.z
        val y = WorldUtil.getHighestYAtXZ(x, z)
        MessageUtil.sendMessage("Highest block at x: $x, z: $z is at y: $y", MessageUtil.Color.GREEN)
    }
}