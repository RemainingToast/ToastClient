package toast.client.modules.render

import com.google.common.eventbus.Subscribe
import net.minecraft.block.Block
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import toast.client.events.network.EventPacketReceived
import toast.client.modules.Module

/**
 * Searches for and highlights blocks in the world
 */
class BlockESP : Module("BlockESP", "Highlights blocks in the world.", Category.RENDER, -1) {
    override fun onEnable() {
        disable()
    }

    @Subscribe
    fun onPacketReceived(event: EventPacketReceived) {
        if (mc.world == null) return
        if (event.getPacket() is BlockUpdateS2CPacket) {
            val packet : BlockUpdateS2CPacket = event.getPacket() as BlockUpdateS2CPacket
            if (matches.contains(packet.state.block)) {
                found.add(packet.pos)
            }
        }
    }

    companion object {
        /**
         * List of blocks to search for
         */
        var matches: List<Block> = ArrayList()

        /**
         * List of coordinates of the matching blocks
         */
        var found: MutableList<BlockPos> = mutableListOf()
    }
}