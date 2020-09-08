package dev.toastmc.client.module.movement

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Module
import dev.toastmc.client.util.MovementUtil
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import kotlin.math.cos
import kotlin.math.sin

class Strafe : Module() {
    @Setting(name = "AirSpeed") var airspeed = true
    @Setting(name = "AutoJump") var autojump = true

    private var jumpTicks = 0

    override fun onEnable() {
        super.onEnable()
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        super.onDisable()
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        if (mc.player!!.input.movementForward != 0F || mc.player!!.input.movementSideways != 0F){
            MovementUtil.setSpeed(MovementUtil.getSpeed())
            if(airspeed) mc.player!!.velocity.add(0.0, 0.25, 0.0)
            mc.options.autoJump = autojump
            if (autojump){
                mc.player!!.setVelocity(mc.player!!.velocity.x, 0.41, mc.player!!.velocity.z)
                if(mc.player!!.isSprinting) {
                    mc.player!!.setVelocity(-sin(MovementUtil.getMovementYaw()) * 0.2, mc.player!!.velocity.y, cos(MovementUtil.getMovementYaw()) * 0.2)
                }
                mc.player!!.isOnGround = false
                jumpTicks = 5
            }
            if (jumpTicks > 0) jumpTicks--
        }
    })

}