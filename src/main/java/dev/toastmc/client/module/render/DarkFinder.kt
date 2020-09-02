package dev.toastmc.client.module.render

import dev.toastmc.client.event.ChunkEvent
import dev.toastmc.client.event.PacketEvent
import dev.toastmc.client.event.RenderEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.*
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket
import net.minecraft.util.math.BlockPos

@ModuleManifest(
    label = "DarkFinder",
    description = "Finds dark spots",
    category = Category.RENDER
)
class DarkFinder : Module() {
    private var matches = mutableListOf<BlockPos>()
    private var checked = mutableListOf<BlockPos>()

    @Setting(name = "Threshold (<=)")
    var threshold: Int = 3

    @EventHandler
    private val onChunkUnloadEvent = Listener(EventHook<ChunkEvent.Unload> {
        if (it.chunk == null || matches.isEmpty()) return@EventHook
        GlobalScope.launch {
            val x = (it.chunk.pos.x shl 4)
            val z = (it.chunk.pos.z shl 4)
            val xRange = IntRange(x, x + 15)
            val zRange = IntRange(z, z + 15)
            var index = matches.size
            while (--index >= 0) {
                if (xRange.contains(matches[index].x) && zRange.contains(matches[index].z)) {
                    matches.removeAt(index)
                }
            }
            index = checked.size
            while (--index >= 0) {
                if (xRange.contains(checked[index].x) && zRange.contains(checked[index].z)) {
                    matches.removeAt(index)
                }
            }
        }.start()
    })

    @EventHandler
    private val onChunkLoadEvent = Listener(EventHook<ChunkEvent.Load> {
        if (it.chunk == null) return@EventHook
        GlobalScope.launch {
            val x = (it.chunk.pos.x shl 4)
            val z = (it.chunk.pos.z shl 4)
            for (currX in x..x + 15) {
                for (currZ in z..z + 15) {
                    for (currY in 1..WorldUtil.getHighestYAtXZ(currX, currZ) + 1) {
                        val pos = BlockPos(currX, currY - 1, currZ)
                        if (mc.world!!.getBlockState(BlockPos(currX, currY, currZ)).isAir && !mc.world!!.getBlockState(
                                pos
                            ).isAir && !checked.contains(pos)
                        ) {
                            matches[matches.size] = pos
                            checked[checked.size] = pos
                        }
                    }
                }
            }
        }.start()
    })

    @EventHandler
    private val onLightUpdateEvent = Listener(EventHook<PacketEvent.Receive> {
        if (it.packet is LightUpdateS2CPacket) {
            checked.clear()
            matches.clear()
        }
    })

    @EventHandler
    private val onWorldRenderEvent = Listener(EventHook<RenderEvent.World> {
        if (mc.player == null) return@EventHook
    })

    override fun onDisable() {
        /*
        ToastClient.EVENT_BUS.unsubscribe(onWorldRenderEvent)
        ToastClient.EVENT_BUS.unsubscribe(onChunkLoadEvent)
        ToastClient.EVENT_BUS.unsubscribe(onChunkUnloadEvent)
        ToastClient.EVENT_BUS.unsubscribe(onLightUpdateEvent)
         */
        threshold.coerceIn(0, 14)
    }

    override fun onEnable() {
        MessageUtil.sendMessage("$label is kil.", MessageUtil.Color.DARK_RED)
        disable()
        /*
        ToastClient.EVENT_BUS.subscribe(onWorldRenderEvent)
        ToastClient.EVENT_BUS.subscribe(onChunkLoadEvent)
        ToastClient.EVENT_BUS.subscribe(onChunkUnloadEvent)
        ToastClient.EVENT_BUS.subscribe(onLightUpdateEvent)
        */
        threshold.coerceIn(0, 14)
    }

}