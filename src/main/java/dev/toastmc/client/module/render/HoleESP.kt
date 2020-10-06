package dev.toastmc.client.module.render

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.RenderEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.WorldUtil.AIR
import dev.toastmc.client.util.WorldUtil.getBlockPositionsInArea
import dev.toastmc.client.util.drawFilledBox
import dev.toastmc.client.util.mc
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import java.awt.Color
import kotlin.math.ceil
import kotlin.math.floor

@ModuleManifest(
        label = "HoleESP",
        category = Category.RENDER
)
class HoleESP : Module() {
    @Setting(name = "Range") var range = 8
    @Setting(name = "Opacity") var opacity = 70
    @Setting(name = "Mode") var mode = "Flat"

    override fun onEnable() {
        EVENT_BUS.subscribe(onWorldRenderEvent)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onWorldRenderEvent)
    }

    private val positions: MutableList<BlockPos> = ArrayList()
    private val offsets = listOf(
            BlockPos(0, -1, 0),
            BlockPos(1, 0, 0),
            BlockPos(-1, 0, 0),
            BlockPos(0, 0, 1),
            BlockPos(0, 0, -1)
    )

    @EventHandler
    val onWorldRenderEvent = Listener(EventHook<RenderEvent.World> {
        if (mc.player == null) return@EventHook
        try {
            val blocks = mc.player!!.blockPos.add(-range, -range, -range).getBlockPositionsInArea(mc.player!!.blockPos.add(range, range, range))
            val iter = blocks.iterator()
            while(iter.hasNext()){
                val n = iter.next()
                if(AIR.contains(mc.world!!.getBlockState(n).block) && Vec3d(n.x.toDouble(), n.y.toDouble(), n.z.toDouble()).add(0.5, 0.5, 0.5).distanceTo(mc.player!!.trackedPosition) <= range){
                    positions.add(n)
                }
            }
            val posIter = positions.iterator()
            while (posIter.hasNext()){
                val pos = posIter.next()
                var bedrock = 0
                var obsidian = 0
                for (b in offsets){
                    when {
                        mc.world!!.getBlockState(pos.add(b)).block === Blocks.BEDROCK -> bedrock++
                        mc.world!!.getBlockState(pos.add(b)).block === Blocks.OBSIDIAN -> obsidian++
                    }
                }
                val bool = bedrock == 5 || obsidian == 5 || bedrock + obsidian == 5 && AIR.contains(mc.world!!.getBlockState(pos.add(0, 1, 0)).block) && AIR.contains(mc.world!!.getBlockState(pos.add(0, 2, 0)).block)
                if(bool){
                    val bedrockColor = Color(0.08f, 1f, 0.35f, (opacity + 20 / 100).toFloat())
                    val obsidianColor = Color(1f, 0f, 0f, (opacity + 20 / 100).toFloat())
                    val elseColor = Color(1f, 1f, 0f, (opacity + 20 / 100).toFloat())
                    when (mode) {
                        "Box" -> when {
                            bedrock == 5 -> drawFilledBox(Box(pos), bedrockColor)
                            obsidian == 5 -> drawFilledBox(Box(pos), obsidianColor)
                            else -> drawFilledBox(Box(pos), elseColor)
                        }
                        "Flat" -> {
                            val floorBox = Box(floor(pos.x.toDouble()), floor(pos.y.toDouble()), floor(pos.z.toDouble()), ceil(pos.x + 0.01), floor(pos.y.toDouble()) - 0.0001, ceil(pos.z + 0.01))
                            when {
                                bedrock == 5 -> drawFilledBox(floorBox, bedrockColor)
                                obsidian == 5 -> drawFilledBox(floorBox, obsidianColor)
                                else -> drawFilledBox( floorBox, elseColor)
                            }
                        }
                    }
                }
            }
        } catch (ignore: InterruptedException) {}
    })
}