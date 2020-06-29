package toast.client.modules.render

import com.google.common.eventbus.Subscribe
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import toast.client.events.player.EventRender
import toast.client.modules.Module
import toast.client.utils.RenderUtil
import toast.client.utils.WorldInteractionUtil
import toast.client.utils.WorldUtil.getBlockPositionsInArea
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Highlights holes (single air blocks) in the world to show where attacks could come from
 */
class HoleESP : Module("HoleESP", "Highlights holes (air) in the world.", Category.RENDER, -1) {
    override fun onEnable() {
        disable()
    }

    private val offsets = listOf(
            BlockPos(0, -1, 0),
            BlockPos(1, 0, 0),
            BlockPos(-1, 0, 0),
            BlockPos(0, 0, 1),
            BlockPos(0, 0, -1)
    )

    @Subscribe
    fun onRender(event: EventRender?) {
        if (mc.player == null || mc.world == null) return
        val range = getDouble("Range")
        try {
            val positions = (mc.player?: return).blockPos.add(-range, -range, -range).getBlockPositionsInArea((mc.player
                    ?: return).blockPos.add(range, range, range))
            val airPositions: MutableList<BlockPos> = ArrayList(emptyList())
            val posIter = positions.iterator()
            while (posIter.hasNext()) {
                val next = posIter.next()
                if (WorldInteractionUtil.AIR.contains(mc.world!!.getBlockState(next).block) && Vec3d(next.x.toDouble(), next.y.toDouble(), next.z.toDouble()).add(0.5, 0.5, 0.5).distanceTo(mc.player!!.posVector) <= range) {
                    airPositions.add(next)
                }
            }
            val airPosIter = airPositions.iterator()
            while (airPosIter.hasNext()) {
                val pos = airPosIter.next()
                var bedrock = 0
                var obsidian = 0
                for (blockPos in offsets) {
                    if ((getBool("Bedrock (Green)") || getBool("Mixed (Yellow)")) && (mc.world
                            ?: continue).getBlockState(pos.add(blockPos)).block === Blocks.BEDROCK) {
                        bedrock++
                    } else if ((getBool("Obsidian (Red)") || getBool("Mixed (Yellow)")) && (mc.world
                            ?: continue).getBlockState(pos.add(blockPos)).block === Blocks.OBSIDIAN) {
                        obsidian++
                    }
                }
                if (!(bedrock == 5 && !getBool("Bedrock (Green)") || obsidian == 5 && !getBool("Obsidian (Red)") || bedrock > 0 && obsidian > 0 && !getBool("Mixed (Yellow)")) && bedrock + obsidian == 5 &&
                    WorldInteractionUtil.AIR.contains((mc.world
                        ?: continue).getBlockState(pos.add(0, 1, 0)).block) &&
                    WorldInteractionUtil.AIR.contains((mc.world
                        ?: continue).getBlockState(pos.add(0, 2, 0)).block)) {
                    when (mode) {
                        "Box" -> when {
                            bedrock == 5 -> RenderUtil.drawFilledBox(pos, 0.08f, 1f, 0.35f, (getDouble("Opacity") + 20).toFloat() / 100)
                            obsidian == 5 -> RenderUtil.drawFilledBox(pos, 1f, 0f, 0f, (getDouble("Opacity") + 20).toFloat() / 100)
                            else -> RenderUtil.drawFilledBox(pos, 1f, 1f, 0f, (getDouble("Opacity") + 20).toFloat() / 100)
                        }
                        "Flat" -> when {
                            bedrock == 5 -> RenderUtil.drawFilledBox(Box(floor(pos.x.toDouble()), floor(pos.y.toDouble()), floor(pos.z.toDouble()), ceil(pos.x + 0.01), floor(pos.y.toDouble()) - 0.0001, ceil(pos.z + 0.01)), 0.08f, 1f, 0.35f, (getDouble("Opacity") + 20).toFloat() / 100)
                            obsidian == 5 -> RenderUtil.drawFilledBox(Box(floor(pos.x.toDouble()), floor(pos.y.toDouble()), floor(pos.z.toDouble()), ceil(pos.x + 0.01), floor(pos.y.toDouble()) - 0.0001, ceil(pos.z + 0.01)), 1f, 0f, 0f, (getDouble("Opacity") + 20).toFloat() / 100)
                            else -> RenderUtil.drawFilledBox(Box(floor(pos.x.toDouble()), floor(pos.y.toDouble()), floor(pos.z.toDouble()), ceil(pos.x + 0.01), floor(pos.y.toDouble()) - 0.0001, ceil(pos.z + 0.01)), 1f, 1f, 0f, (getDouble("Opacity") + 20).toFloat() / 100)
                        }
                    }
                }
            }
        } catch (ignored: InterruptedException) { }
    }

    companion object {
        private var awaiting = false
    }

    init {
        settings.addBoolean("Bedrock (Green)", true)
        settings.addBoolean("Obsidian (Red)", true)
        settings.addBoolean("Mixed (Yellow)", true)
        settings.addSlider("Range", 1.0, 8.0, 15.0)
        settings.addSlider("Opacity", 0.0, 70.0, 100.0)
        settings.addMode("Mode", "Flat", "Flat", "Box")
    }
}