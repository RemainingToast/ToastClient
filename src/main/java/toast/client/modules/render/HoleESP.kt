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
import toast.client.utils.WorldUtil
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.function.Consumer
import kotlin.math.ceil
import kotlin.math.floor

class HoleESP : Module("HoleESP", "Highlights holes (air) in the world.", Category.RENDER, -1) {
    private val offsets = Arrays.asList(
            BlockPos(0, -1, 0),
            BlockPos(1, 0, 0),
            BlockPos(-1, 0, 0),
            BlockPos(0, 0, 1),
            BlockPos(0, 0, -1)
    )

    @Subscribe
    fun onRender(event: EventRender?) {
        if (mc.player == null || mc.world == null || awaiting) return
        awaiting = true
        val range = getDouble("Range")
        awaiting = try {
            val positions = WorldUtil.getBlockPositionsInArea(mc.player!!.blockPos.add(-range, -range, -range), mc.player!!.blockPos.add(range, range, range))
            val airPositions: MutableList<BlockPos> = ArrayList(emptyList())
            val latch = CountDownLatch(positions.size)
            WorldUtil.searchList(mc.world!!, positions, WorldInteractionUtil.AIR).keys.forEach(Consumer { pos: BlockPos ->
                if (Vec3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.50).distanceTo(mc.player!!.pos) <= getDouble("Range")) airPositions.add(pos)
                latch.countDown()
            })
            latch.await()
            val latch2electricboogaloo = CountDownLatch(airPositions.size)
            Thread(Runnable {
                airPositions.forEach(Consumer { pos: BlockPos ->
                    latch2electricboogaloo.countDown()
                    var bedrock = 0
                    var obsidian = 0
                    for (blockPos in offsets) {
                        if ((getBool("Bedrock (Green)") || getBool("Mixed (Yellow)")) && mc.world!!.getBlockState(pos.add(blockPos)).block === Blocks.BEDROCK) {
                            bedrock++
                        } else if ((getBool("Obsidian (Red)") || getBool("Mixed (Yellow)")) && mc.world!!.getBlockState(pos.add(blockPos)).block === Blocks.OBSIDIAN) {
                            obsidian++
                        }
                    }
                    if (!(bedrock == 5 && !getBool("Bedrock (Green)") || obsidian == 5 && !getBool("Obsidian (Red)") || bedrock > 0 && obsidian > 0 && !getBool("Mixed (Yellow)")) && bedrock + obsidian == 5 &&
                            WorldInteractionUtil.AIR.contains(mc.world!!.getBlockState(pos.add(0, 1, 0)).block) &&
                            WorldInteractionUtil.AIR.contains(mc.world!!.getBlockState(pos.add(0, 2, 0)).block)) {
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
                })
            }).start()
            latch2electricboogaloo.await()
            false
        } catch (ignored: InterruptedException) {
            false
        }
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