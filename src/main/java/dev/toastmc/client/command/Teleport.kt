package dev.toastmc.client.command

import dev.toastmc.client.util.Color
import dev.toastmc.client.util.mc
import dev.toastmc.client.util.sendMessage
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.Vec3d
import java.text.DecimalFormat
import kotlin.math.floor


class Teleport {
    var tpThread: Thread? = null
    val df: DecimalFormat = DecimalFormat("#.###")

    fun run(args: Array<String>) {
        if (mc.player == null || args.size == 0) return
        if (args[0].equals("stop", true)) {
            if (tpThread != null && tpThread!!.isAlive) {
                sendMessage("Teleport Cancelled!", Color.YELLOW)
                tpThread?.interrupt()
                tpThread = null
            } else {
                sendMessage("Bruh it's not running!", Color.YELLOW)
            }
            return
        } else if (args.size != 4) {
            sendMessage("Invalid coords!", Color.DARK_RED)
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
                        sendMessage("Teleport Finished!", Color.GREEN)
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