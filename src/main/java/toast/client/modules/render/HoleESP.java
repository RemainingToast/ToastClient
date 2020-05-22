package toast.client.modules.render;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;
import toast.client.utils.WorldInteractionUtil;
import toast.client.utils.WorldUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class HoleESP extends Module {

    private List<BlockPos> offsets = Arrays.asList(
            new BlockPos(0, -1, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1)
    );

    private static boolean awaiting = false;

    public HoleESP() {
        super("StorageESP", Category.RENDER, -1);
        this.addBool("Bedrock (Green)", true);
        this.addBool("Obsidian (Red)", true);
        this.addBool("Mixed (Yellow)", true);
        this.addNumberOption("Range", 8, 1, 15, false);
        this.addNumberOption("Opacity", 70, 0, 100, true);
        this.addModes("Flat", "Box");
    }

    @EventImpl
    public void onDisable(EventUpdate event) {
        if (mc.player == null || mc.world == null || awaiting) return;
        awaiting = true;
        Double range = getDouble("Range");
        try {
            List<BlockPos> positions = WorldUtil.getBlockPositionsInArea(mc.player.getBlockPos().add(-range, -range, -range), mc.player.getBlockPos().add(range, range, range));
            CountDownLatch latch = new CountDownLatch(positions.size());
            WorldUtil.searchList(mc.world, positions, WorldInteractionUtil.AIR).keySet().forEach(pos -> {
                // Render stuff for blocks
                latch.countDown();
            });
            latch.await();
            awaiting = false;
        } catch (InterruptedException ignored) { awaiting = false; }
    }
}
