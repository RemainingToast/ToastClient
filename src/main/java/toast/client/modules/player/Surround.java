package toast.client.modules.player;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventRender;
import toast.client.modules.Module;
import toast.client.utils.WorldInteractionUtil;

public class Surround extends Module {

    private static int placements = 0;
    private static int slot = -1;

    public Surround() {
        super("Surround", Category.PLAYER, -1);
        this.settings.addBoolean("AutoDisable", true);
        this.settings.addBoolean("Center", true);
        this.settings.addBoolean("All blocks", false);
        this.settings.addBoolean("Rotations", false);
        this.settings.addSlider("Blocks/Tick", 1, 2, 8);
    }

    @EventImpl
    public void onUpdate(EventRender event) {
        if (mc.player == null) return;
        int lastSlot = mc.player.inventory.selectedSlot;
        slot = mc.player.inventory.selectedSlot;

        if (this.getBool("All blocks")) {
            if (!has4OrMoreBuildBlockInHotbar()) return;
            slot = getSlotForBuildBlocksInHotbar();
        } else if (!this.getBool("All blocks")) {
            if (!hasInHotbar(Items.OBSIDIAN)) return;
            slot = getItemSlotInHotbar(Items.OBSIDIAN);
        }

        mc.player.inventory.selectedSlot = slot;
        if (this.getBool("Center")) centerPlayerPos();

        placements = 0;
        for (int i = 0; i < (int) this.getDouble("Blocks/Tick"); i++) {
            /*
             *     -Z
             *
             * -X       X
             *
             *      Z
             */
            double xDec = Math.abs(mc.player.getX() - Math.floor(mc.player.getX()));
            double zDec = Math.abs(mc.player.getZ() - Math.floor(mc.player.getZ()));
            if (xDec < 0.3) {
                if (zDec < 0.3) {
                    /*
                     * QXXQ
                     * XOOX
                     * XOZX
                     * QXXQ
                     */
                    tryPlace(1, 0, 0);
                    tryPlace(0, 0, 1);
                    tryPlace(1, 0, -1);
                    tryPlace(-1, 0, 1);
                    tryPlace(-2, 0, 0);
                    tryPlace(0, 0, -2);
                    tryPlace(-2, 0, -1);
                    tryPlace(-1, 0, -2);
                } else if (zDec > 0.7) {
                    /*
                     * QXXQ
                     * XOZX
                     * XOOX
                     * QXXQ
                     */
                    tryPlace(-1, 0, -1);
                    tryPlace(0, 0, -1);
                    tryPlace(-2, 0, 0);
                    tryPlace(1, 0, 0);
                    tryPlace(-2, 0, 1);
                    tryPlace(1, 0, 1);
                    tryPlace(-1, 0, 2);
                    tryPlace(0, 0, 2);
                } else {
                    /*
                     * QXXQ
                     * XZOX
                     * QXXQ
                     */
                    tryPlace(-1, 0, -1);
                    tryPlace(0, 0, -1);
                    tryPlace(-2, 0, 0);
                    tryPlace(1, 0, 0);
                    tryPlace(-1, 0, 1);
                    tryPlace(0, 0, 1);
                }
            } else if (xDec > 0.7) {
                if (zDec < 0.3) {
                    /*
                     * QXXQ
                     * XOOX
                     * XZOX
                     * QXXQ
                     */
                    tryPlace(0, 0, -2);
                    tryPlace(1, 0, -2);
                    tryPlace(-1, 0, -1);
                    tryPlace(2, 0, -1);
                    tryPlace(-1, 0, 0);
                    tryPlace(2, 0, 0);
                    tryPlace(0, 0, 1);
                    tryPlace(1, 0, 1);
                } else if (zDec > 0.7) {
                    /*
                     * QXXQ
                     * XZOX
                     * XOOX
                     * QXXQ
                     */
                    tryPlace(0, 0, -1);
                    tryPlace(1, 0, -1);
                    tryPlace(-1, 0, 0);
                    tryPlace(2, 0, 0);
                    tryPlace(-1, 0, 1);
                    tryPlace(2, 0, 1);
                    tryPlace(0, 0, 2);
                    tryPlace(1, 0, 2);
                } else {
                    /*
                     * QXXQ
                     * XZOX
                     * QXXQ
                     */
                    tryPlace(0, 0, -1);
                    tryPlace(1, 0, -1);
                    tryPlace(-1, 0, 0);
                    tryPlace(2, 0, 0);
                    tryPlace(0, 0, 1);
                    tryPlace(1, 0, 1);
                }
            } else {
                if (zDec < 0.3) {
                    /*
                     * QXQ
                     * XOX
                     * XZX
                     * QXQ
                     */
                    tryPlace(0, 0, -2);
                    tryPlace(-1, 0, -1);
                    tryPlace(1, 0, -1);
                    tryPlace(-1, 0, 0);
                    tryPlace(1, 0, 0);
                    tryPlace(0, 0, 1);
                } else if (zDec > 0.7) {
                    /*
                     * QXQ
                     * XZX
                     * XOX
                     * QXQ
                     */
                    tryPlace(0, 0, -1);
                    tryPlace(-1, 0, 0);
                    tryPlace(1, 0, 0);
                    tryPlace(-1, 0, 1);
                    tryPlace(1, 0, 1);
                    tryPlace(0, 0, 2);
                } else {
                    /*
                     * QXQ
                     * XZX
                     * QXQ
                     */
                    tryPlace(1, 0, 0);
                    tryPlace(-1, 0, 0);
                    tryPlace(0, 0, 1);
                    tryPlace(0, 0, -1);
                }
            }
            if (placements == 0) {
                mc.player.inventory.selectedSlot = lastSlot;
                if (this.getBool("AutoDisable")) setEnabled(false);
            }
        }

        /*final Vec3d vec = interpolateEntity(mc.player, event.getPartialTicks());
        final BlockPos playerPos = new BlockPos(vec.x, vec.y, vec.z);

        final BlockPos[] positions = {playerPos.north(), playerPos.south(), playerPos.east(), playerPos.west()};
        if (this.canPlace(positions[0], Direction.NORTH)) {
            this.place(playerPos, Direction.NORTH);
        }
        if (this.canPlace(positions[1], Direction.SOUTH)) {
            this.place(playerPos, Direction.SOUTH);
        }
        if (this.canPlace(positions[2], Direction.EAST)) {
            this.place(playerPos, Direction.EAST);
        }
        if (this.canPlace(positions[3], Direction.WEST)) {
            this.place(playerPos, Direction.WEST);
        }
        if (this.canPlace(positions[0], Direction.UP)) {
            this.place(positions[0], Direction.UP);
        }
        if (this.canPlace(positions[1], Direction.UP)) {
            this.place(positions[1], Direction.UP);
        }
        if (this.canPlace(positions[2], Direction.UP)) {
            this.place(positions[2], Direction.UP);
        }
        if (this.canPlace(positions[3], Direction.UP)) {
            this.place(positions[3], Direction.UP);
        }*/
        mc.player.inventory.selectedSlot = lastSlot;
    }

    public void centerPlayerPos() {
        if (mc.player == null) return;
        double x = mc.player.getPos().getX();
        double z = mc.player.getPos().getZ();
        if (x < 0) {
            x = Math.ceil(x) - 0.5d;
        } else {
            x = Math.floor(x) + 0.5d;
        }
        if (z < 0) {
            z = Math.ceil(z) - 0.5d;
        } else {
            z = Math.floor(z) + 0.5d;
        }
        Vec3d p = mc.player.getPos();
        mc.player.updatePosition(x, Math.floor(p.getY()), z);
    }

    public boolean isCentered(Vec3d pos) {
        if (pos == null) return false;
        return pos.getX() == Math.floor(pos.getX()) + 0.50 &&
                pos.getZ() == Math.floor(pos.getZ()) + 0.50;
    }

    /*public Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastRenderX + (entity.getX() - entity.lastRenderX) * time,
                entity.lastRenderY + (entity.getY() - entity.lastRenderY) * time,
                entity.lastRenderZ + (entity.getZ() - entity.lastRenderZ) * time);
    }*/

    private boolean hasInHotbar(Item item) {
        if (mc.player == null) return false;
        boolean found = false;
        for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
            Item curritem = mc.player.inventory.getInvStack(i).getItem();
            if (!mc.player.inventory.getInvStack(i).isEmpty()) {
                if (item == curritem) {
                    found = true;
                }
            }
        }
        return found;
    }

    private boolean has4OrMoreBuildBlockInHotbar() {
        if (mc.player == null) return false;
        boolean found = false;
        int slotamount = 0;
        for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
            ItemStack curritem = mc.player.inventory.getInvStack(i);
            if (!curritem.isEmpty()) {
                if (curritem.getItem() instanceof BlockItem) {
                    slotamount += curritem.getCount();
                    found = slotamount > 4 || mc.player.isCreative();
                }
            }
        }
        return found;
    }

    private int getSlotForBuildBlocksInHotbar() {
        if (mc.player == null) return -1;
        int found = -1;
        for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
            ItemStack curritem = mc.player.inventory.getInvStack(i);
            if (!mc.player.inventory.getInvStack(i).isEmpty()) {
                if (curritem.getItem() instanceof BlockItem) {
                    found = i;
                }
            }
        }
        return found;
    }

    private int getItemSlotInHotbar(Item item) {
        if (mc.player == null) return -1;
        int slot = -1;
        for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
            Item curritem = mc.player.inventory.getInvStack(i).getItem();
            if (!mc.player.inventory.getInvStack(i).isEmpty()) {
                if (item == curritem) {
                    slot = i;
                }
            }
        }
        return slot;
    }

    private void tryPlace(double offX, double offY, double offZ) {
        if (placements < (int) this.getDouble("Blocks/Tick") && WorldInteractionUtil.isReplaceable(mc.world.getBlockState(new BlockPos(offX, offY, offZ).add(mc.player.getPos().getX(), mc.player.getPos().getY(), mc.player.getPos().getZ())).getBlock())) {
            mc.player.inventory.selectedSlot = slot;
            if (WorldInteractionUtil.placeBlock(new BlockPos(offX, offY, offZ).add(mc.player.getPos().getX(), mc.player.getPos().getY(), mc.player.getPos().getZ()), Hand.MAIN_HAND, this.getBool("Rotations")))
                placements++;
        }
    }

    /*public boolean canPlace(BlockPos pos, Direction direction) {
        if(mc.player == null || mc.world == null) return false;
        Block b =  mc.world.getBlockState(pos).getBlock();
        return mc.player.canPlaceOn(pos, direction, mc.player.inventory.getInvStack(mc.player.inventory.selectedSlot)) &&
                (b.equals(Blocks.AIR) || b.equals(Blocks.WATER) || b.equals(Blocks.LAVA) || b.equals(Blocks.CAVE_AIR) ||
                b.equals(Blocks.GRASS) || b.equals(Blocks.TALL_GRASS) || b.equals(Blocks.SEAGRASS) || b.equals(Blocks.TALL_SEAGRASS));// problaly some more but not bothered
    }

    public void place(BlockPos pos, Direction direction) {
        if(mc.player == null || mc.interactionManager == null) return;
        if (mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND,
                new BlockHitResult(mc.player.getPos(),
                direction, pos.add(0, -1, 0),
                true)) != ActionResult.FAIL) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }*/
}
