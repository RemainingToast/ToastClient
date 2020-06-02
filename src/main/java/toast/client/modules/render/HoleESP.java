package toast.client.modules.render;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventRender;
import toast.client.modules.Module;
import toast.client.utils.RenderUtil;
import toast.client.utils.WorldInteractionUtil;
import toast.client.utils.WorldUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class HoleESP extends Module {

    private static boolean awaiting = false;
    private final List<BlockPos> offsets = Arrays.asList(
            new BlockPos(0, -1, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1)
    );

    public HoleESP() {
        super("HoleESP", "Highlights holes (air) in the world.", Category.RENDER, -1);
        this.settings.addBoolean("Bedrock (Green)", true);
        this.settings.addBoolean("Obsidian (Red)", true);
        this.settings.addBoolean("Mixed (Yellow)", true);
        this.settings.addSlider("Range", 1, 8, 15);
        this.settings.addSlider("Opacity", 0, 70, 100);
        this.settings.addMode("Mode", "Flat", "Flat", "Box");
    }

    @Override
    public void onEnable() {
        this.disable();
    }

    @EventImpl
    public void onRender(EventRender event) {
        if (mc.player == null || mc.world == null || awaiting) return;
        awaiting = true;
        double range = getDouble("Range");
        try {
            List<BlockPos> positions = WorldUtil.getBlockPositionsInArea(mc.player.getBlockPos().add(-range, -range, -range), mc.player.getBlockPos().add(range, range, range));
            List<BlockPos> airPositions = new ArrayList<>(Collections.emptyList());
            CountDownLatch latch = new CountDownLatch(positions.size());
            WorldUtil.searchList(mc.world, positions, WorldInteractionUtil.AIR).keySet().forEach(pos -> {
                if (new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.50).distanceTo(mc.player.getPos()) <= this.getDouble("Range"))
                    airPositions.add(pos);
                latch.countDown();
            });
            latch.await();
            CountDownLatch latch2electricboogaloo = new CountDownLatch(airPositions.size());
            new Thread(() -> {
                airPositions.forEach(pos -> {
                    latch2electricboogaloo.countDown();
                    int bedrock = 0;
                    int obsidian = 0;

                    for (BlockPos blockPos : offsets) {
                        if (((this.getBool("Bedrock (Green)") || this.getBool("Mixed (Yellow)")) && mc.world.getBlockState(pos.add(blockPos)).getBlock() == Blocks.BEDROCK)) {
                            bedrock++;
                        } else if ((this.getBool("Obsidian (Red)") || this.getBool("Mixed (Yellow)")) && mc.world.getBlockState(pos.add(blockPos)).getBlock() == Blocks.OBSIDIAN) {
                            obsidian++;
                        }
                    }

                    if (!((bedrock == 5 && !this.getBool("Bedrock (Green)")) || (obsidian == 5 && !this.getBool("Obsidian (Red)")) || (bedrock > 0 && obsidian > 0 && !this.getBool("Mixed (Yellow)"))) &&
                            bedrock + obsidian == 5 &&
                            (WorldInteractionUtil.AIR.contains(mc.world.getBlockState(pos.add(0, 1, 0)).getBlock())) &&
                            (WorldInteractionUtil.AIR.contains(mc.world.getBlockState(pos.add(0, 2, 0)).getBlock()))) {

                        if (this.isMode("Box")) {
                            if (bedrock == 5) {
                                RenderUtil.drawFilledBox(pos, 0.08f, 1f, 0.35f, (float) (this.getDouble("Opacity") + 20) / 100);
                            } else if (obsidian == 5) {
                                RenderUtil.drawFilledBox(pos, 1f, 0f, 0f, (float) (this.getDouble("Opacity") + 20) / 100);
                            } else {
                                RenderUtil.drawFilledBox(pos, 1f, 1f, 0f, (float) (this.getDouble("Opacity") + 20) / 100);
                            }
                        } else if (this.isMode("Flat")) {
                            if (bedrock == 5) {
                                RenderUtil.drawFilledBox(new Box(Math.floor(pos.getX()), Math.floor(pos.getY()), Math.floor(pos.getZ()), Math.ceil(pos.getX() + 0.01), Math.floor(pos.getY()) - 0.0001, Math.ceil(pos.getZ() + 0.01)), 0.08f, 1f, 0.35f, (float) (this.getDouble("Opacity") + 20) / 100);
                                //RenderUtils.drawQuad(Math.floor(pos.getX()), Math.floor(pos.getZ()), Math.ceil(pos.getX()), Math.ceil(pos.getZ()), Math.floor(pos.getY()), 0.08f, 1f, 0.35f, (float) getSettings().get(4).toSlider().getValue() / 100);
                            } else if (obsidian == 5) {
                                RenderUtil.drawFilledBox(new Box(Math.floor(pos.getX()), Math.floor(pos.getY()), Math.floor(pos.getZ()), Math.ceil(pos.getX() + 0.01), Math.floor(pos.getY()) - 0.0001, Math.ceil(pos.getZ() + 0.01)), 1f, 0f, 0f, (float) (this.getDouble("Opacity") + 20) / 100);
                                //RenderUtils.drawQuad(Math.floor(pos.getX()), Math.floor(pos.getZ()), Math.ceil(pos.getX()), Math.ceil(pos.getZ()), Math.floor(pos.getY()), 1f, 1f, 0f, (float) getSettings().get(4).toSlider().getValue() / 100);
                            } else {
                                RenderUtil.drawFilledBox(new Box(Math.floor(pos.getX()), Math.floor(pos.getY()), Math.floor(pos.getZ()), Math.ceil(pos.getX() + 0.01), Math.floor(pos.getY()) - 0.0001, Math.ceil(pos.getZ() + 0.01)), 1f, 1f, 0f, (float) (this.getDouble("Opacity") + 20) / 100);
                                //RenderUtils.drawQuad(Math.floor(pos.getX()), Math.floor(pos.getZ()), Math.ceil(pos.getX()), Math.ceil(pos.getZ()), Math.floor(pos.getY()), 1f, 0f, 0f, (float) getSettings().get(4).toSlider().getValue() / 100);
                            }
                        }
                    }
                });
            }).start();
            latch2electricboogaloo.await();
            awaiting = false;
        } catch (InterruptedException ignored) {
            awaiting = false;
        }
    }
}
