package dev.toastmc.client.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.client.command.util.*
import dev.toastmc.client.util.Color
import dev.toastmc.client.util.mc
import dev.toastmc.client.util.sendMessage
import net.minecraft.command.argument.Vec3ArgumentType
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.server.command.CommandSource
import net.minecraft.util.math.Vec3d
import kotlin.math.floor

class Teleport : Command("teleport") {
    var tpThread: Thread? = null
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral(getName()) {
            string("stop") {
                does {
                    if (tpThread != null && tpThread!!.isAlive) {
                        sendMessage("Teleport Cancelled!", Color.YELLOW)
                        tpThread?.interrupt()
                        tpThread = null
                    } else {
                        sendMessage("Bruh it's not running!", Color.YELLOW)
                    }
                    0
                }
            }
            argument("pos", Vec3ArgumentType.vec3()){
                does { ctx ->
                    val vec: Vec3d = "pos" from ctx
                    try {
                        tpThread = Thread {
                            try {
                                if (mc.player == null) return@Thread
                                val x = vec.x.toString().parseCoord(mc.player!!.x)
                                val y = vec.y.toString().parseCoord(mc.player!!.y)
                                val z = vec.z.toString().parseCoord(mc.player!!.z)
                                val bpt = 100.0
                                val totalTicks = floor(mc.player!!.pos.distanceTo(Vec3d(x, y, z)) / bpt)
                                val moveVec = Vec3d((x - mc.player!!.x) / totalTicks, (y - mc.player!!.y) / totalTicks, (z - mc.player!!.z) / totalTicks)
                                var tick = totalTicks
                                while (--tick >= 0) {
                                    Thread.sleep(50)
                                    val newPos = mc.player!!.pos.add(moveVec)
                                    mc.player!!.setVelocity(0.0, 0.0, 0.0)
                                    mc.player!!.updatePosition(newPos.x, newPos.y, newPos.z)
                                    mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.PositionOnly(newPos.x, newPos.y, newPos.z, mc.player!!.isOnGround))
                                }
                                if (mc.player!!.pos.distanceTo(Vec3d(x, y, z)) > 0.0) {
                                    Thread.sleep(50)
                                    mc.player!!.updatePosition(x, y, z)
                                    mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.PositionOnly(x, y, z, mc.player!!.isOnGround))
                                }
                                sendMessage("Teleport Finished!", Color.GREEN)
                            } catch (ignored: Throwable) {}
                        }
                        tpThread!!.start()

                    } catch (ignored: Throwable) {}
                    0
                }
            }
        }
    }


    private fun String.parseCoord(relativeTo: Double): Double {
        return if (this.equals("~", true)) relativeTo else if (this.elementAt(0) == '~') this.substring(1).toDoubleOrNull()?:0 + relativeTo else this.toDoubleOrNull()?:relativeTo
    }
}