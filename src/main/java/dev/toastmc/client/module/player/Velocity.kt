package dev.toastmc.client.module.player

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.EntityEvent
import dev.toastmc.client.event.MoveEntityFluidEvent
import dev.toastmc.client.event.PacketEvent
import dev.toastmc.client.event.ToastEvent
import dev.toastmc.client.mixin.client.IEntityVelocityUpdateS2CPacket
import dev.toastmc.client.mixin.client.IExplosionS2CPacket
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.mc
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket

@ModuleManifest(
        label = "Velocity",
        description = "Neglect Knockback",
        category = Category.PLAYER,
        aliases = ["antiknockback"]
)
class Velocity : Module() {
    @Setting(name = "Horizontal") var horizontal = 0f
    @Setting(name = "Vertical") var vertical = 0f

    override fun onEnable() {
        super.onEnable()
        EVENT_BUS.subscribe(packetEventListener)
        EVENT_BUS.subscribe(entityCollisionListener)
        EVENT_BUS.subscribe(moveEntityFluidEventListener)
    }

    override fun onDisable() {
        super.onDisable()
        EVENT_BUS.unsubscribe(packetEventListener)
        EVENT_BUS.unsubscribe(entityCollisionListener)
        EVENT_BUS.unsubscribe(moveEntityFluidEventListener)
    }

    @EventHandler
    private val packetEventListener = Listener(EventHook<PacketEvent.Receive> {
        if (mc.player == null) return@EventHook
        if (it.era === ToastEvent.Era.PRE) {
            if (it.packet is EntityVelocityUpdateS2CPacket) {
                val velocity: EntityVelocityUpdateS2CPacket = it.packet
                if (velocity.id === mc.player!!.entityId) {
                    if (horizontal == 0f && vertical == 0f) it.cancel()
                    val xyz = velocity as IEntityVelocityUpdateS2CPacket
                    xyz.velocityX = (xyz.velocityX * horizontal).toInt()
                    xyz.velocityY = (xyz.velocityY * vertical).toInt()
                    xyz.velocityZ = (xyz.velocityZ * horizontal).toInt()
                }
            } else if (it.packet is ExplosionS2CPacket) {
                if (horizontal == 0f && vertical == 0f) it.cancel()
                val xyz = it.packet as IExplosionS2CPacket
                xyz.playerVelocityX = xyz.playerVelocityX * horizontal
                xyz.playerVelocityY = xyz.playerVelocityY * vertical
                xyz.playerVelocityZ = xyz.playerVelocityZ * horizontal
            }
        }
    })

    @EventHandler
    private val entityCollisionListener = Listener(EventHook<EntityEvent.EntityCollision> {
        if (it.entity === mc.player) {
            if (horizontal == 0f && vertical == 0f) {
                it.cancel()
                return@EventHook
            }
            it.x = it.x * horizontal
            it.y = 0.0
            it.z = it.z * horizontal
        }
    })

    @EventHandler
    private val moveEntityFluidEventListener = Listener(EventHook<MoveEntityFluidEvent> {
        if (it.entity === mc.player) {
            it.movement = it.movement.multiply(horizontal.toDouble(), vertical.toDouble(), horizontal.toDouble())
        }
    })
}