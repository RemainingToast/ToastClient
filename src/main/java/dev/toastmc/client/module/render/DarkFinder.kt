package dev.toastmc.client.module.render

import baritone.api.BaritoneAPI
import baritone.api.pathing.goals.GoalBlock
import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.PacketEvent
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.MovementUtil
import dev.toastmc.client.util.WorldUtil.vec3d
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.min

@ModuleManifest(
    label = "DarkFinder",
    description = "Finds dark spots",
    category = Category.RENDER
)
class DarkFinder : Module() {
    @Setting(name = "Threshold (<=)") var threshold: Int = 3
    @Setting(name = "Min. Y") var minY: Int = 1
    @Setting(name = "Max. Y") var maxY: Int = 255
    @Setting(name = "Loop (ms)") var loop: Long = 1000

    var matches = ConcurrentLinkedQueue<BlockPos>()
    var start: BlockPos = BlockPos.ORIGIN
    var doingStuff = false

    @EventHandler
    val onLightUpdateEvent = Listener(EventHook<PacketEvent.Receive> {
        if (it.packet !is LightUpdateS2CPacket || BaritoneAPI.getProvider().primaryBaritone.pathingBehavior.isPathing || doingStuff) return@EventHook
        matches.clear()
    })

    @EventHandler
    val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (matches.size > 0 && !doingStuff) {
            doingStuff = true
            GlobalScope.launch {
                BaritoneAPI.getProvider().primaryBaritone.customGoalProcess.setGoalAndPath(GoalBlock(matches.first()))
                val async = GlobalScope.async {
                    if (!BaritoneAPI.getProvider().primaryBaritone.pathingBehavior.isPathing) return@async
                    delay(loop)
                }
                async.await()
                MovementUtil.lookAt(matches.first().add(0, -1, 0).vec3d.add(0.5, 0.0, 0.5), false)
                // TODO: PLACE TORCH
                // TODO: PLACE TORCH
                // TODO: PLACE TORCH
                // TODO: PLACE TORCH
                // TODO: PLACE TORCH
                // TODO: PLACE TORCH
                BaritoneAPI.getProvider().primaryBaritone.customGoalProcess.setGoalAndPath(GoalBlock(matches.first()))
            }
            doingStuff = false
        }
    })

    override fun onEnable() {
        ToastClient.EVENT_BUS.subscribe(onLightUpdateEvent)
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
        matches.clear()
    }

    override fun onDisable() {
        ToastClient.EVENT_BUS.unsubscribe(onLightUpdateEvent)
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
    }

    init {
        GlobalScope.async {
            while (true) {
                if (mc.player != null && !BaritoneAPI.getProvider().primaryBaritone.pathingBehavior.isPathing && !doingStuff) {
                    val distance = mc.options.viewDistance
                    for (x in -distance..distance) {
                        for (z in -distance..distance) {
                            GlobalScope.async {
                                val chunk = mc.world!!.getChunk(mc.player!!.blockPos.add(x, 0, z))
                                if (chunk.maxLightLevel <= threshold) {
                                    for (x2 in chunk.pos.startX..chunk.pos.endX) {
                                        for (z2 in chunk.pos.startZ..chunk.pos.endZ) {
                                            val height = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x2, z2)
                                            for (y in min(minY, height)..min(maxY, height)) {
                                                if (matches.size > 0) break
                                                val pos = BlockPos(x2, y, z2)
                                                val blockState = mc.world!!.getBlockState(pos)
                                                val under = mc.world!!.getBlockState(pos.add(0, -1, 0))
                                                if (blockState.isAir && under.isOpaque && mc.world!!.lightingProvider.getLight(
                                                        pos,
                                                        0
                                                    ) <= threshold
                                                ) matches.add(pos)
                                            }
                                            if (matches.size > 0 || doingStuff) break
                                        }
                                        if (matches.size > 0 || doingStuff) break
                                    }
                                }
                            }.start()
                            if (matches.size > 0 || doingStuff) break
                        }
                        if (matches.size > 0 || doingStuff) break
                    }
                }
                matches.clear()
                delay(loop)
            }
        }.start()
    }


}