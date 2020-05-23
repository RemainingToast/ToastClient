package toast.client.modules.render;

import net.minecraft.block.Block;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.util.math.BlockPos;
import toast.client.event.EventImpl;
import toast.client.event.events.network.EventPacketReceived;
import toast.client.modules.Module;
import toast.client.utils.WorldUtil;

import java.util.List;

public class BlockESP extends Module {
    public static List<Block> matches;
    public static List<BlockPos> found;

    private static List<Block> checkedChunks;

    public BlockESP() {
        super("BlockESP", Category.RENDER, -1);
    }

    @EventImpl
    public void onPacketReceived(EventPacketReceived event) {
        if (mc.world == null) return;
        if (event.getPacket() instanceof ChunkDataS2CPacket) {
            ChunkDataS2CPacket packet = (ChunkDataS2CPacket) event.getPacket();
            try {
                WorldUtil.searchChunk(mc.world, packet.getX() / 16, packet.getZ() / 16, matches).keySet().addAll(found);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
