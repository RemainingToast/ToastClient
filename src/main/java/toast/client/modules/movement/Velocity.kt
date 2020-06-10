package toast.client.modules.movement

import com.google.common.eventbus.Subscribe
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
import toast.client.events.network.EventPacketReceived
import toast.client.modules.Module

class Velocity : Module("Velocity", "Changes your velocity, can stop things from pushing you.", Category.MOVEMENT, -1) {
    var lastx = 0.0
    var lasty = 0.0
    var lastz = 0.0
    override fun onEnable() {
        lastx = 0.0
        lasty = 0.0
        lastz = 0.0
    }

    override fun onDisable() {
        lastx = 0.0
        lasty = 0.0
        lastz = 0.0
    }

    @Subscribe
    fun onPacket(e: EventPacketReceived) {
        if (this.enabled) {
            if (mc.player == null) return
            when (mode) {
                "Vanilla" -> when (e.getPacket()) {
                    is ExplosionS2CPacket -> {
                        val s27 = e.getPacket() as ExplosionS2CPacket
                        if (!getBool("Cancel")) {
                            val hvel = getDouble("HVel")
                            val vvel = getDouble("VVel")
                            mc.player!!.setVelocity(s27.x * (hvel * 0.00001),
                                    s27.y * (vvel * 0.00001), s27.z * (hvel * 0.00001))
                        }
                        e.isCancelled = true
                    }
                    is EntityVelocityUpdateS2CPacket -> {
                        val packetEntityId = e.getPacket() as EntityVelocityUpdateS2CPacket
                        if (packetEntityId.id != mc.player!!.entityId) return
                        if (!getBool("Cancel")) {
                            val hVel = getDouble("HVel")
                            val vVel = getDouble("VVel")
                            mc.player!!.setVelocity(packetEntityId.velocityX * (hVel * 0.00001),
                                    packetEntityId.velocityY * (vVel * 0.00001), packetEntityId.velocityZ * (hVel * 0.00001))
                        }
                        e.isCancelled = true
                    }
                }
                "YTeleport" -> when {
                    mc.player!!.hurtTime.toFloat() == 0f -> {
                        lastx = mc.player!!.x
                        lasty = mc.player!!.y
                        lastz = mc.player!!.z
                    }
                    mc.player!!.hurtTime >= 8 -> mc.player!!.setPos(lastx, lasty + 50.0, lastz)
                }
            }
        }
    }

    init {
        settings.addMode("Mode", "Vanilla", "Vanilla", "YTeleport")
        settings.addBoolean("Cancel", true)
        settings.addSlider("HVel", -100.0, 0.0, 100.0)
        settings.addSlider("VVel", -100.0, 0.0, 100.0)
    }
}