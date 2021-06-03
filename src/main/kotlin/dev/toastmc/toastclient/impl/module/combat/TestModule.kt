package dev.toastmc.toastclient.impl.module.combat

import dev.toastmc.toastclient.api.events.WorldRenderEvent
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.WorldUtil
import dev.toastmc.toastclient.api.util.render.RenderUtil
import org.quantumclient.energy.Subscribe
import java.awt.Color

object TestModule : Module("TestModule", Category.COMBAT) {

    @Subscribe
    fun on(event: WorldRenderEvent) {
        val tileEntities = WorldUtil.getTileEntitiesInChunk(
            mc.player!!.world,
            mc.player!!.chunkX,
            mc.player!!.chunkZ
        )
        for ((pos, tile) in tileEntities) {
            RenderUtil.drawText3D(
                event.matrixStack,
                pos.x.toFloat(),
                pos.y.toFloat(),
                pos.z.toFloat(),
                mc.player!!.yaw,
                mc.player!!.pitch,
                1f,
                tile.name.asString(),
                Color.WHITE,
                false
            )
        }
    }

}