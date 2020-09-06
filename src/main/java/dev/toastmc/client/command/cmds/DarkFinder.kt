package dev.toastmc.client.command.cmds

import baritone.api.BaritoneAPI
import baritone.api.pathing.goals.GoalBlock
import dev.toastmc.client.command.Command
import dev.toastmc.client.command.CommandManifest
import dev.toastmc.client.util.MessageUtil
import dev.toastmc.client.util.WorldInteractionUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.LightType
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.max

@CommandManifest(
    label = "DarkFinder",
    description = "Goes to a dark position.",
    aliases = ["dark"],
    usage = ".DarkFinder <threshold (<=)>"
)
class DarkFinder : Command(){
    private var matches = ConcurrentLinkedQueue<BlockPos>()
    private var threshold = -1

    private var finder = GlobalScope.async {}

    override fun run(args: Array<String>) {
        if (args.isEmpty() || args[0].toIntOrNull() == null) {
            MessageUtil.sendMessage("Not enough arguments.", MessageUtil.Color.DARK_RED)
            return
        }
        if (finder.isActive) {
            MessageUtil.sendMessage("Finder is still active.", MessageUtil.Color.DARK_RED)
            return
        }
        threshold = max(args[0].toIntOrNull()!!, 0)
        finder = GlobalScope.async {
            if (mc.player != null && !BaritoneAPI.getProvider().primaryBaritone.customGoalProcess.isActive) {
                MessageUtil.sendMessage("DarkFinder started to find blocks with light level <= $threshold", MessageUtil.Color.GREEN)
                val distance = mc.options.viewDistance
                var ran = 0
                for (x in -distance..distance) {
                    for (z in -distance..distance) {
                        GlobalScope.launch {
                            val chunk = mc.world!!.getChunk(mc.player!!.blockPos.add(x, 0, z))
                            if (chunk.maxLightLevel <= threshold) {
                                for (x2 in chunk.pos.startX..chunk.pos.endX) {
                                    for (z2 in chunk.pos.startZ..chunk.pos.endZ) {
                                        val height = chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x2, z2) + 1
                                        for (y in 0..height) {
                                            if (matches.size > 0) break
                                            val pos = BlockPos(x2, y, z2)
                                            val blockState = mc.world!!.getBlockState(pos)
                                            val under = mc.world!!.getBlockState(pos.add(0, -1, 0))
                                            if (
                                                WorldInteractionUtil.AIR.contains(blockState.block) && under.isOpaque &&
                                                //mc.world!!.chunkManager.lightingProvider.get(LightType.BLOCK).getLightLevel(pos) <= threshold
                                                mc.world!!.getLightLevel(LightType.BLOCK, pos) <= threshold
                                            ) {
                                                matches.add(pos)
                                                System.out.println(pos)
                                                break
                                            }
                                        }
                                        if (matches.size > 0) break
                                    }
                                    if (matches.size > 0) break
                                }
                            }
                            ++ran
                        }
                        if (matches.size > 0) break
                    }
                    if (matches.size > 0) break
                }
                val async = GlobalScope.async {
                    while (ran < (2 * distance + 1) * (2 * distance + 1) && matches.size > 0) { delay(100L) }
                }
                async.await()
                MessageUtil.sendMessage(if (matches.size > 0) "DarkFinder found a dark spot at ${matches.elementAt(0)}" else "DarkFinder did not find any dark spots within render distance", MessageUtil.Color.AQUA)
                if (matches.size > 0) {
                    System.out.println(matches.elementAt(0))
                    BaritoneAPI.getProvider().primaryBaritone.customGoalProcess.setGoalAndPath(GoalBlock(matches.elementAt(0)))
                    matches.clear()
                }
            }
        }
    }
}