package me.remainingtoast.toastclient.client.module.combat

import me.remainingtoast.toastclient.ToastClient.Companion.EVENT_BUS
import me.remainingtoast.toastclient.api.event.EntityEvent
import me.remainingtoast.toastclient.api.event.MoveEntityFluidEvent
import me.remainingtoast.toastclient.api.event.PacketEvent
import me.remainingtoast.toastclient.api.event.ToastEvent
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.util.mc
import me.remainingtoast.toastclient.mixin.client.IEntityVelocityUpdateS2CPacket
import me.remainingtoast.toastclient.mixin.client.IExplosionS2CPacket
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
import kotlin.math.max

object AntiKnockback : Module("AntiKnockback", Category.COMBAT) {

    val horizontal = registerDouble("Horizontal%", "",0.0,0.0,100.0, true)
    val vertical = registerDouble("Vertical%", "", 0.0, 0.0,100.0, true)
    val delay = registerInteger("Delay(MS)", "", 170, 0, 1000, false)


    private var oldVelX = Double.NaN
    private var oldVelY = Double.NaN
    private var oldVelZ = Double.NaN

    override fun onEnable() {
        EVENT_BUS.subscribe(packetEventListener)
        EVENT_BUS.subscribe(entityCollisionListener)
        EVENT_BUS.subscribe(moveEntityFluidEventListener)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(packetEventListener)
        EVENT_BUS.unsubscribe(entityCollisionListener)
        EVENT_BUS.unsubscribe(moveEntityFluidEventListener)
    }

    @EventHandler
    private val packetEventListener = Listener(EventHook<PacketEvent.Receive> {
        if (mc.player == null) return@EventHook
        if (it.era === ToastEvent.Era.PRE) {
            oldVelX = Double.NaN
            oldVelY = Double.NaN
            oldVelZ = Double.NaN
            if (it.packet is EntityVelocityUpdateS2CPacket) {
                val velocity: EntityVelocityUpdateS2CPacket = it.packet
                if (velocity.id == mc.player!!.entityId) {
                    if (horizontal.value == 0.0 && vertical.value == 0.0) it.cancel()
                    val xyz = velocity as IEntityVelocityUpdateS2CPacket
                    if (delay.value > 0) {
                        oldVelX = (xyz.velocityX * horizontal.value) / 8000.0
                        oldVelY = (xyz.velocityY * vertical.value) / 8000.0
                        oldVelZ = (xyz.velocityZ * horizontal.value) / 8000.0
                        it.cancel()
                    } else {
                        xyz.velocityX = (xyz.velocityX * horizontal.value).toInt()
                        xyz.velocityY = (xyz.velocityY * vertical.value).toInt()
                        xyz.velocityZ = (xyz.velocityZ * horizontal.value).toInt()
                    }
                }
            } else if (it.packet is ExplosionS2CPacket) {
                if (horizontal.value == 0.0 && vertical.value == 0.0) it.cancel()
                val xyz = it.packet as IExplosionS2CPacket
                if (delay.value > 0) {
                    oldVelX = xyz.playerVelocityX.toDouble()
                    oldVelY = xyz.playerVelocityY.toDouble()
                    oldVelZ = xyz.playerVelocityZ.toDouble()
                    it.cancel()
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
    })

    @EventHandler
    private val entityCollisionListener = Listener(EventHook<EntityEvent.EntityCollision> {
        if (it.entity === mc.player) {
            if (horizontal.value == 0.0 && vertical.value == 0.0) {
                it.cancel()
                return@EventHook
            }
            it.x = it.x * horizontal.value
            it.y = 0.0
            it.z = it.z * horizontal.value
        }
    })

    @EventHandler
    private val moveEntityFluidEventListener = Listener(EventHook<MoveEntityFluidEvent> {
        if (it.entity === mc.player) {
            it.movement = it.movement.multiply(horizontal.value, vertical.value, horizontal.value)
        }
    })

}