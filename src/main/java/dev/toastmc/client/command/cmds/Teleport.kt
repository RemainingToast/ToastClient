package dev.toastmc.client.command.cmds

import dev.toastmc.client.command.Command
import dev.toastmc.client.command.CommandManifest
import dev.toastmc.client.util.MessageUtil
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.Vec3d
import java.text.DecimalFormat
import kotlin.math.floor


@CommandManifest(
    label = "Teleport",
    description = "Teleports to a location.",
    aliases = ["tp"]
)
class Teleport : Command() {
    var tpThread: Thread? = null
    val df: DecimalFormat = DecimalFormat("#.###")

    override fun run(args: Array<String>) {
        if (mc.player == null || args.size == 0) return
        if (args[0].equals("stop", true)) {
            if (tpThread != null && tpThread!!.isAlive) {
                MessageUtil.sendMessage("Teleport Cancelled!", MessageUtil.Color.YELLOW)
                tpThread?.interrupt()
                tpThread = null
            } else {
                MessageUtil.sendMessage("Bruh it's not running!", MessageUtil.Color.YELLOW)
            }
            return
        } else if (args.size != 4) {
            MessageUtil.sendMessage("Invalid coords!", MessageUtil.Color.DARK_RED)
            return;
        } else {
            try {
                tpThread = Thread {
                    try {
                        if (mc.player == null) return@Thread
                        val x = args[0].parseCoord(mc.player!!.x)
                        val y = args[1].parseCoord(mc.player!!.y)
                        val z = args[2].parseCoord(mc.player!!.z)
                        val bpt = args[3].toDouble()

                        val totalTicks = floor(mc.player!!.pos.distanceTo(Vec3d(x, y, z)) / bpt)
                        val moveVec = Vec3d(
                            (x - mc.player!!.x) / totalTicks,
                            (y - mc.player!!.y) / totalTicks,
                            (z - mc.player!!.z) / totalTicks
                        )
                        var tick = totalTicks

                        while (--tick >= 0) {
                            Thread.sleep(50)
                            val newPos = mc.player!!.pos.add(moveVec)
                            mc.player!!.setVelocity(0.0, 0.0, 0.0)
                            mc.player!!.updatePosition(
                                newPos.x,
                                newPos.y,
                                newPos.z
                            )
                            mc.player!!.networkHandler.sendPacket(
                                PlayerMoveC2SPacket.PositionOnly(
                                    newPos.x,
                                    newPos.y,
                                    newPos.z,
                                    mc.player!!.isOnGround
                                )
                            )
                        }
                        if (mc.player!!.pos.distanceTo(Vec3d(x, y, z)) > 0.0) {
                            Thread.sleep(50)
                            mc.player!!.updatePosition(
                                x,
                                y,
                                z
                            )
                            mc.player!!.networkHandler.sendPacket(
                                PlayerMoveC2SPacket.PositionOnly(
                                    x,
                                    y,
                                    z,
                                    mc.player!!.isOnGround
                                )
                            )
                        }
                        MessageUtil.sendMessage("Teleport Finished!", MessageUtil.Color.GREEN)
                    } catch (ignored: Throwable) {}
                }
                tpThread!!.start()

            } catch (ignored: Throwable) {}
        }
    }

    fun String.parseCoord(relativeTo: Double): Double {
        return if (this.equals("~", true)) relativeTo else if (this.elementAt(0) == '~') this.substring(1).toDoubleOrNull()?:0 + relativeTo else this.toDoubleOrNull()?:relativeTo
    }
}