package toast.client.modules.player;

import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;

import java.util.ArrayList;
import java.util.List;

public class Nuker extends Module {
    public Nuker() {
        super("Nuker", Category.PLAYER, -1);
        this.addNumberOption("Range", 5, 1, 10);
    }
    public ArrayList<BlockPos> getBlocks() {
        if(mc.player == null || mc.world == null) return null;
        ArrayList<BlockPos> list = new ArrayList<>();
        int i = (int)this.getDouble("Range") + 1;
        for (int x = -i; x <= i; x++) {
            for (int y = -i; y <= i; y++) {
                for (int z = -i; z <= i; z++) {
                    BlockPos pos = (new BlockPos(mc.player)).add(x, y, z);
                    if (!(mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos).getBlock().equals(Blocks.CAVE_AIR))
                            && mc.player.squaredDistanceTo(new Vec3d(pos.getX(), pos.getY(),
                            pos.getZ())) < ((int)this.getDouble("Range") * (int)this.getDouble("Range")))
                        list.add(pos);
                }
            }
        }  return list;
    }

    private List<BlockPos> pending = new ArrayList<>();

    @EventImpl
    public void onEvent(EventUpdate event) {
        if(mc.interactionManager == null || mc.player == null) return;
        for (BlockPos block : getBlocks()) {
            if(pending.contains(block)) continue;
            if(mc.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
                pending.add(block);
                mc.interactionManager.attackBlock(block, Direction.UP);
                mc.player.swingHand(Hand.MAIN_HAND);
                pending.remove(block);
            } else if(mc.interactionManager.getCurrentGameMode() == GameMode.SURVIVAL) {//TODO: fix this shit
                //mc.interactionManager.updateBlockBreakingProgress(block, Direction.UP);
            } else return;

        }
    }
}
