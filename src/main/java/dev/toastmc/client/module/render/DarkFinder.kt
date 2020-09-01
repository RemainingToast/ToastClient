package dev.toastmc.client.module.render

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.ChunkEvent
import dev.toastmc.client.event.RenderEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.WorldUtil
import dev.toastmc.client.util.box
import dev.toastmc.client.util.color
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import org.lwjgl.opengl.GL11.GL_LINE_LOOP

@ModuleManifest(
    label = "DarkFinder",
    description = "Finds dark spots",
    category = Category.RENDER
)
class DarkFinder : Module() {
    private var matches = mutableMapOf<Int, BlockPos>()

    @Setting(name = "Threshold (<=)")
    var threshold: Int = 3

    @EventHandler
    private val onChunkUnloadEvent = Listener(EventHook<ChunkEvent.Unload> {
        if (it.chunk == null || matches.isEmpty()) return@EventHook
        val x = (it.chunk.pos.x shl 4)
        val z = (it.chunk.pos.z shl 4)
        val xRange = IntRange(x, x + 15)
        val zRange = IntRange(z, z + 15)
        var index = matches.size
        while (--index >= 0) {
            if (xRange.contains(matches[index]!!.x) && zRange.contains(matches[index]!!.z)) matches.remove(index)
        }
    })

    @EventHandler
    private val onChunkLoadEvent = Listener(EventHook<ChunkEvent.Load> {
        if (it.chunk == null) return@EventHook
        val x = (it.chunk.pos.x shl 4)
        val z = (it.chunk.pos.z shl 4)
        for (currX in x..x + 15) {
            for (currZ in z..z + 15) {
                for (currY in 1..WorldUtil.getHighestYAtXZ(currX, currZ) + 1) {
                    val pos = BlockPos(currX, currY - 1, currZ)
                    if (mc.world!!.getBlockState(BlockPos(currX, currY, currZ)).isAir && !mc.world!!.getBlockState(pos).isAir && !matches.containsValue(pos))
                        matches[matches.size] = pos
                }
            }
        }
    })

    @EventHandler
    private val onWorldRenderEvent = Listener(EventHook<RenderEvent.World> {
        if (mc.player == null) return@EventHook
        for (m in 0 until matches.size) {
            ToastClient.RENDER_BUILDER.begin(GL_LINE_LOOP) {
                color(0f, 1f, 0f, 0.5f)
                box(Box(matches[m]))
            }
        }
    })

    override fun onDisable() {
        ToastClient.EVENT_BUS.unsubscribe(onWorldRenderEvent)
        ToastClient.EVENT_BUS.unsubscribe(onChunkLoadEvent)
        ToastClient.EVENT_BUS.unsubscribe(onChunkUnloadEvent)
        threshold.coerceIn(0, 14)
    }

    override fun onEnable() {
        ToastClient.EVENT_BUS.subscribe(onWorldRenderEvent)
        ToastClient.EVENT_BUS.subscribe(onChunkLoadEvent)
        ToastClient.EVENT_BUS.subscribe(onChunkUnloadEvent)
        threshold.coerceIn(0, 14)
    }

}