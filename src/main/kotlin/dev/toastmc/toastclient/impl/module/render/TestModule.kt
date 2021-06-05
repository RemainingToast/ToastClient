package dev.toastmc.toastclient.impl.module.render

import dev.toastmc.toastclient.api.events.WorldRenderEvent
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.WorldUtil
import dev.toastmc.toastclient.api.util.render.RenderUtil
import org.quantumclient.energy.Subscribe

object TestModule : Module("TestModule", Category.RENDER) {

    @Subscribe
    fun on(event: WorldRenderEvent) {
        val tileEntities = WorldUtil.getTileEntitiesInChunk(mc.player!!.world, mc.player!!.chunkX, mc.player!!.chunkZ)
        for ((pos, tile) in tileEntities) {
            RenderUtil.draw3DText(
                tile.name,
                pos.x + 0.5,
                pos.y + 0.5,
                pos.z + 0.5,
                0.50,
                true
            )
            RenderUtil.drawGuiItem(
                pos.x + 0.5,
                pos.y + 0.25,
                pos.z + 0.5,
                0.0,
                0.0,
                0.25,
                tile.asItem().defaultStack
            )
        }
    }

}