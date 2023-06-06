package dev.toastmc.toastclient.impl.module.render

import dev.toastmc.toastclient.api.events.WorldRenderEvent
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.ToastColor
import dev.toastmc.toastclient.api.util.WorldUtil
import dev.toastmc.toastclient.api.util.render.RenderUtil
import org.quantumclient.energy.Subscribe

object TestModule : Module("TestModule", Category.RENDER) {

    @Subscribe
    fun on(event: WorldRenderEvent) {
        val tileEntities = WorldUtil.getTileEntitiesInChunk(mc.player!!.world, mc.player!!.chunkPos.x, mc.player!!.chunkPos.z)
        tileEntities.forEach { (pos, tile) ->
            if (pos.y <= 69) {
                RenderUtil.drawOutline(
                    pos,
                    ToastColor.rainbow(1),
                    2.5f
                )
            } else {
                RenderUtil.drawBox(
                    pos,
                    ToastColor.rainbow(1)
                )
            }

            RenderUtil.draw3DText(
                tile.name,
                pos.x + 0.5,
                pos.y + 0.5,
                pos.z + 0.5,
                0.50,
                background = false,
                shadow = true
            )
        }
    }

}