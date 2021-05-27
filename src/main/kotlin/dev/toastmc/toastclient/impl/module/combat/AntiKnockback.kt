package dev.toastmc.toastclient.impl.module.combat

import dev.toastmc.toastclient.api.events.EntityEvent
import dev.toastmc.toastclient.api.events.MoveEntityFluidEvent
import dev.toastmc.toastclient.api.events.PacketEvent
import dev.toastmc.toastclient.api.module.Module
import dev.toastmc.toastclient.mixin.client.IEntityVelocityUpdateS2CPacket
import dev.toastmc.toastclient.mixin.client.IExplosionS2CPacket
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
import org.quantumclient.energy.Era
import org.quantumclient.energy.Subscribe
import kotlin.math.max

object AntiKnockback : Module("AntiKnockback", Category.COMBAT) {

    val horizontal = number("Horizontal%", 0.0,0.0,100.0)
    val vertical = number("Vertical%", 0.0, 0.0,100.0)
    val delay = number("Delay(MS)",170.0, 0.0, 1000.0)

    private var oldVelX = Double.NaN
    private var oldVelY = Double.NaN
    private var oldVelZ = Double.NaN

    @Subscribe
    fun on(event: PacketEvent.Receive) {
        if (mc.player == null) return
        if (event.era === Era.PRE) {
            oldVelX = Double.NaN
            oldVelY = Double.NaN
            oldVelZ = Double.NaN
            if (event.packet is EntityVelocityUpdateS2CPacket) {
                val velocity: EntityVelocityUpdateS2CPacket = event.packet
                if (velocity.id == mc.player!!.entityId) {
                    if (horizontal.value == 0.0 && vertical.value == 0.0) event.cancel()
                    val xyz = velocity as IEntityVelocityUpdateS2CPacket
                    if (delay.value > 0) {
                        oldVelX = (xyz.velocityX * horizontal.value) / 8000.0
                        oldVelY = (xyz.velocityY * vertical.value) / 8000.0
                        oldVelZ = (xyz.velocityZ * horizontal.value) / 8000.0
                        event.cancel()
                    } else {
                        xyz.velocityX = (xyz.velocityX * horizontal.value).toInt()
                        xyz.velocityY = (xyz.velocityY * vertical.value).toInt()
                        xyz.velocityZ = (xyz.velocityZ * horizontal.value).toInt()
                    }
                }
            } else if (event.packet is ExplosionS2CPacket) {
                if (horizontal.value == 0.0 && vertical.value == 0.0) event.cancel()
                val xyz = event.packet as IExplosionS2CPacket
                if (delay.value > 0) {
                    oldVelX = xyz.playerVelocityX.toDouble()
                    oldVelY = xyz.playerVelocityY.toDouble()
                    oldVelZ = xyz.playerVelocityZ.toDouble()
                    event.cancel()
                } else {
                    xyz.playerVelocityX = (xyz.playerVelocityX * horizontal.value).toFloat()
                    xyz.playerVelocityY = (xyz.playerVelocityY * vertical.value).toFloat()
                    xyz.playerVelocityZ = (xyz.playerVelocityZ * horizontal.value).toFloat()
                }
            }
            if (delay.value > 0 && !oldVelX.isNaN() || !oldVelY.isNaN() || !oldVelZ.isNaN()) {
                Thread.sleep(max(0L, delay.value.toLong()))
                mc.player!!.setVelocity(mc.player!!.velocity.x + oldVelX, mc.player!!.velocity.y + oldVelY, mc.player!!.velocity.z + oldVelZ)
            }
        }
    }

    @Subscribe
    fun on(event: EntityEvent.EntityCollision) {
        if (event.entity === mc.player) {
            if (horizontal.value == 0.0 && vertical.value == 0.0) {
                event.cancel()
                return
            }
            event.x = event.x * horizontal.value
            event.y = 0.0
            event.z = event.z * horizontal.value
        }
    }

    @Subscribe
    fun on(event: MoveEntityFluidEvent) {
        if (event.entity === mc.player) {
            event.movement = event.movement.multiply(horizontal.value, vertical.value, horizontal.value)
        }
    }

}