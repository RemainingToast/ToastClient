package toast.client.modules.render

import com.google.common.eventbus.Subscribe
import net.minecraft.block.Block
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket
import net.minecraft.util.math.BlockPos
import toast.client.events.network.EventPacketReceived
import toast.client.modules.Module
import toast.client.utils.WorldUtil.searchChunk

class BlockESP : Module("BlockESP", "Highlights blocks in the world.", Category.RENDER, -1) {
    @Subscribe
    fun onPacketReceived(event: EventPacketReceived) {
        if (mc.world == null) return
        if (event.getPacket() is ChunkDataS2CPacket) {
            val packet = event.getPacket() as ChunkDataS2CPacket
            try {
                searchChunk(mc.world!!, packet.x / 16, packet.z / 16, matches).keys.addAll(found!!)
            } catch (ignored: InterruptedException) {
            }
        }
    }

    companion object {
        var matches: List<Block> = ArrayList()
        var found: List<BlockPos> = ArrayList()
    }
}