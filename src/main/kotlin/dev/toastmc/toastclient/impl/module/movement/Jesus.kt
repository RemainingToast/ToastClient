package dev.toastmc.toastclient.impl.module.movement

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.WorldUtil.isFluid
import net.minecraft.util.math.BlockPos

object Jesus : Module("Jesus", Category.MOVEMENT) {

    override fun onUpdate() {
        if (mc.player == null) return
        val e = if (mc.player!!.vehicle != null) mc.player!!.vehicle else mc.player
        if (e!!.isSneaking || e.fallDistance > 3f) return
        e.isOnGround = true
        when {
            isFluid(BlockPos(e.pos.add(0.0, 0.3, 0.0))) -> e.setVelocity(e.velocity.x, 0.08, e.velocity.z)
            isFluid(BlockPos(e.pos.add(0.0, 0.1, 0.0))) -> e.setVelocity(e.velocity.x, 0.05, e.velocity.z)
            isFluid(BlockPos(e.pos.add(0.0, 0.05, 0.0))) -> e.setVelocity(e.velocity.x, 0.01, e.velocity.z)
            isFluid(BlockPos(e.pos)) -> e.setVelocity(e.velocity.x, -0.005, e.velocity.z)
        }
    }

}