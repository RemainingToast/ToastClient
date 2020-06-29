package toast.client.modules.player

import com.google.common.eventbus.Subscribe
import net.minecraft.block.Blocks
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.GameMode
import toast.client.events.network.EventSyncedUpdate
import toast.client.modules.Module
import java.util.*

class Nuker : Module("Nuker", "Automatically destroys blocks around you.", Category.PLAYER, -1) {
    private val pending: MutableList<BlockPos> = ArrayList()
    val blocks: ArrayList<BlockPos>?
        get() {
            if (mc.player == null || mc.world == null) return null
            val list = ArrayList<BlockPos>()
            val i = getDouble("Range").toInt() + 1
            for (x in -i..i) {
                for (y in -i..i) {
                    for (z in -i..i) {
                        val pos = BlockPos(mc.player).add(x, y, z)
                        if (!(mc.world!!.getBlockState(pos).block == Blocks.AIR || mc.world!!.getBlockState(pos).block == Blocks.CAVE_AIR)
                                && mc.player!!.squaredDistanceTo(Vec3d(pos.x.toDouble(), pos.y.toDouble(),
                                        pos.z.toDouble())) < getDouble("Range").toInt() * getDouble("Range").toInt()) list.add(pos)
                    }
                }
            }
            return list
        }

    @Subscribe
    fun onEvent(event: EventSyncedUpdate?) {
        if (mc.interactionManager == null || mc.player == null) return
        for (block in blocks!!) {
            if (pending.contains(block)) continue
            when (mc.interactionManager!!.currentGameMode) {
                GameMode.CREATIVE -> {
                    pending.add(block)
                    mc.interactionManager!!.attackBlock(block, Direction.UP)
                    mc.player!!.swingHand(Hand.MAIN_HAND)
                    pending.remove(block)
                }
                GameMode.SURVIVAL -> { //TODO: fix this shit
                    //mc.interactionManager.updateBlockBreakingProgress(block, Direction.UP);
                }
                else -> return
            }
        }
    }

    init {
        settings.addSlider("Range", 1.0, 5.0, 10.0)
    }
}