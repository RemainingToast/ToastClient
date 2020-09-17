package dev.toastmc.client.module.movement

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.WorldUtil.isFluid
import dev.toastmc.client.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.util.math.BlockPos


@ModuleManifest(
    label = "Jesus",
    description = "Walk on Water",
    category = Category.MOVEMENT
)
class Jesus : Module() {

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