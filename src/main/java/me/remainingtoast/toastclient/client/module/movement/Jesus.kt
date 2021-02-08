package me.remainingtoast.toastclient.client.module.movement

import me.remainingtoast.toastclient.ToastClient.Companion.EVENT_BUS
import me.remainingtoast.toastclient.api.event.TickEvent
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.util.WorldUtil.isFluid
import me.remainingtoast.toastclient.api.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.util.math.BlockPos

object Jesus : Module("Jesus", Category.MOVEMENT) {

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onTickEvent)
    }

    override fun onEnable() {
        EVENT_BUS.subscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        val e = if (mc.player!!.vehicle != null) mc.player!!.vehicle else mc.player
        if (e!!.isSneaking || e.fallDistance > 3f) return@EventHook
        when {
            isFluid(BlockPos(e.pos.add(0.0, 0.3, 0.0))) -> e.setVelocity(e.velocity.x, 0.08, e.velocity.z)
            isFluid(BlockPos(e.pos.add(0.0, 0.1, 0.0))) -> e.setVelocity(e.velocity.x, 0.05, e.velocity.z)
            isFluid(BlockPos(e.pos.add(0.0, 0.05, 0.0))) -> e.setVelocity(e.velocity.x, 0.01, e.velocity.z)
            isFluid(BlockPos(e.pos)) -> {
                e.setVelocity(e.velocity.x, -0.005, e.velocity.z)
                e.isOnGround = true
            }
        }
    })

}