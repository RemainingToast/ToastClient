package toast.client.modules.combat

import com.google.common.eventbus.Subscribe
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import toast.client.events.network.EventSyncedUpdate
import toast.client.modules.Module
import toast.client.utils.WorldInteractionUtil
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Module to automatically surround the player's feet with blocks to prevent taking damage
 */
class Surround : Module("Surround", "Surrounds your feet with blocks.", Category.COMBAT, -1) {
    @Subscribe
    fun onUpdate(event: EventSyncedUpdate?) {
        if (mc.player == null) return
        val lastSlot = mc.player!!.inventory.selectedSlot
        slot = mc.player!!.inventory.selectedSlot
        slot = when (getBool("All blocks")) {
            true -> {
                if (!has4OrMoreBuildBlockInHotbar()) return
                slotForBuildBlocksInHotbar
            }
            false -> {
                if (!hasInHotbar(Items.OBSIDIAN)) return
                getItemSlotInHotbar(Items.OBSIDIAN)
            }
        }
        mc.player!!.inventory.selectedSlot = slot
        if (getBool("Center")) centerPlayerPos()
        placements = 0
        for (i in 0 until getDouble("Blocks/Tick").toInt()) {
            /*
             *     -Z
             *
             * -X       X
             *
             *      Z
             */
            val xDec = abs(mc.player!!.x - floor(mc.player!!.x))
            val zDec = abs(mc.player!!.z - floor(mc.player!!.z))
            when {
                xDec < 0.3 -> {
                    when {
                        zDec < 0.3 -> {
                            /*
                                 * QXXQ
                                 * XOOX
                                 * XOZX
                                 * QXXQ
                                 */
                            tryPlace(1.0, 0.0, 0.0)
                            tryPlace(0.0, 0.0, 1.0)
                            tryPlace(1.0, 0.0, -1.0)
                            tryPlace(-1.0, 0.0, 1.0)
                            tryPlace(-2.0, 0.0, 0.0)
                            tryPlace(0.0, 0.0, -2.0)
                            tryPlace(-2.0, 0.0, -1.0)
                            tryPlace(-1.0, 0.0, -2.0)
                        }
                        zDec > 0.7 -> {
                            /*
                                 * QXXQ
                                 * XOZX
                                 * XOOX
                                 * QXXQ
                                 */
                            tryPlace(-1.0, 0.0, -1.0)
                            tryPlace(0.0, 0.0, -1.0)
                            tryPlace(-2.0, 0.0, 0.0)
                            tryPlace(1.0, 0.0, 0.0)
                            tryPlace(-2.0, 0.0, 1.0)
                            tryPlace(1.0, 0.0, 1.0)
                            tryPlace(-1.0, 0.0, 2.0)
                            tryPlace(0.0, 0.0, 2.0)
                        }
                        else -> {
                            /*
                                 * QXXQ
                                 * XZOX
                                 * QXXQ
                                 */
                            tryPlace(-1.0, 0.0, -1.0)
                            tryPlace(0.0, 0.0, -1.0)
                            tryPlace(-2.0, 0.0, 0.0)
                            tryPlace(1.0, 0.0, 0.0)
                            tryPlace(-1.0, 0.0, 1.0)
                            tryPlace(0.0, 0.0, 1.0)
                        }
                    }
                }
                xDec > 0.7 -> {
                    when {
                        zDec < 0.3 -> {
                            /*
                                 * QXXQ
                                 * XOOX
                                 * XZOX
                                 * QXXQ
                                 */
                            tryPlace(0.0, 0.0, -2.0)
                            tryPlace(1.0, 0.0, -2.0)
                            tryPlace(-1.0, 0.0, -1.0)
                            tryPlace(2.0, 0.0, -1.0)
                            tryPlace(-1.0, 0.0, 0.0)
                            tryPlace(2.0, 0.0, 0.0)
                            tryPlace(0.0, 0.0, 1.0)
                            tryPlace(1.0, 0.0, 1.0)
                        }
                        zDec > 0.7 -> {
                            /*
                                 * QXXQ
                                 * XZOX
                                 * XOOX
                                 * QXXQ
                                 */
                            tryPlace(0.0, 0.0, -1.0)
                            tryPlace(1.0, 0.0, -1.0)
                            tryPlace(-1.0, 0.0, 0.0)
                            tryPlace(2.0, 0.0, 0.0)
                            tryPlace(-1.0, 0.0, 1.0)
                            tryPlace(2.0, 0.0, 1.0)
                            tryPlace(0.0, 0.0, 2.0)
                            tryPlace(1.0, 0.0, 2.0)
                        }
                        else -> {
                            /*
                                 * QXXQ
                                 * XZOX
                                 * QXXQ
                                 */
                            tryPlace(0.0, 0.0, -1.0)
                            tryPlace(1.0, 0.0, -1.0)
                            tryPlace(-1.0, 0.0, 0.0)
                            tryPlace(2.0, 0.0, 0.0)
                            tryPlace(0.0, 0.0, 1.0)
                            tryPlace(1.0, 0.0, 1.0)
                        }
                    }
                }
                else -> {
                    when {
                        zDec < 0.3 -> {
                            /*
                                 * QXQ
                                 * XOX
                                 * XZX
                                 * QXQ
                                 */
                            tryPlace(0.0, 0.0, -2.0)
                            tryPlace(-1.0, 0.0, -1.0)
                            tryPlace(1.0, 0.0, -1.0)
                            tryPlace(-1.0, 0.0, 0.0)
                            tryPlace(1.0, 0.0, 0.0)
                            tryPlace(0.0, 0.0, 1.0)
                        }
                        zDec > 0.7 -> {
                            /*
                                 * QXQ
                                 * XZX
                                 * XOX
                                 * QXQ
                                 */
                            tryPlace(0.0, 0.0, -1.0)
                            tryPlace(-1.0, 0.0, 0.0)
                            tryPlace(1.0, 0.0, 0.0)
                            tryPlace(-1.0, 0.0, 1.0)
                            tryPlace(1.0, 0.0, 1.0)
                            tryPlace(0.0, 0.0, 2.0)
                        }
                        else -> {
                            /*
                                 * QXQ
                                 * XZX
                                 * QXQ
                                 */
                            tryPlace(1.0, 0.0, 0.0)
                            tryPlace(-1.0, 0.0, 0.0)
                            tryPlace(0.0, 0.0, 1.0)
                            tryPlace(0.0, 0.0, -1.0)
                        }
                    }
                }
            }
            if (placements == 0) {
                mc.player!!.inventory.selectedSlot = lastSlot
                if (getBool("AutoDisable")) disable()
            }
        }

        /*final Vec3d vec = interpolateEntity(mc.player, events.getPartialTicks());
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
        }*/mc.player!!.inventory.selectedSlot = lastSlot
    }

    private fun centerPlayerPos() {
        if (mc.player == null) return
        var x = mc.player!!.pos.getX()
        var z = mc.player!!.pos.getZ()
        x = if (x < 0) {
            ceil(x) - 0.5
        } else {
            floor(x) + 0.5
        }
        z = if (z < 0) {
            ceil(z) - 0.5
        } else {
            floor(z) + 0.5
        }
        val p = mc.player!!.pos
        mc.player!!.updatePosition(x, floor(p.getY()), z)
    }

    fun isCentered(pos: Vec3d?): Boolean {
        return if (pos == null) false else pos.getX() == floor(pos.getX()) + 0.50 &&
                pos.getZ() == floor(pos.getZ()) + 0.50
    }

    /*public Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastRenderX + (entity.getX() - entity.lastRenderX) * time,
                entity.lastRenderY + (entity.getY() - entity.lastRenderY) * time,
                entity.lastRenderZ + (entity.getZ() - entity.lastRenderZ) * time);
    }*/
    private fun hasInHotbar(item: Item): Boolean {
        if (mc.player == null) return false
        var found = false
        for (i in 0 until PlayerInventory.getHotbarSize()) {
            val currentItem = mc.player!!.inventory.getInvStack(i).item
            if (!mc.player!!.inventory.getInvStack(i).isEmpty) {
                if (item === currentItem) {
                    found = true
                }
            }
        }
        return found
    }

    private fun has4OrMoreBuildBlockInHotbar(): Boolean {
        if (mc.player == null) return false
        var found = false
        var slotamount = 0
        for (i in 0 until PlayerInventory.getHotbarSize()) {
            val curritem = mc.player!!.inventory.getInvStack(i)
            if (!curritem.isEmpty) {
                if (curritem.item is BlockItem) {
                    slotamount += curritem.count
                    found = slotamount > 4 || mc.player!!.isCreative
                }
            }
        }
        return found
    }

    private val slotForBuildBlocksInHotbar: Int
        get() {
            if (mc.player == null) return -1
            var found = -1
            for (i in 0 until PlayerInventory.getHotbarSize()) {
                val currentItem = mc.player!!.inventory.getInvStack(i)
                if (!mc.player!!.inventory.getInvStack(i).isEmpty) {
                    if (currentItem.item is BlockItem) {
                        found = i
                    }
                }
            }
            return found
        }

    private fun getItemSlotInHotbar(item: Item): Int {
        if (mc.player == null) return -1
        var slot = -1
        for (i in 0 until PlayerInventory.getHotbarSize()) {
            val currentItem = mc.player!!.inventory.getInvStack(i).item
            if (!mc.player!!.inventory.getInvStack(i).isEmpty) {
                if (item === currentItem) {
                    slot = i
                }
            }
        }
        return slot
    }

    private fun tryPlace(offX: Double, offY: Double, offZ: Double) {
        if (placements < getDouble("Blocks/Tick").toInt() && WorldInteractionUtil.isReplaceable(mc.world!!.getBlockState(BlockPos(offX, offY, offZ).add(mc.player!!.pos.getX(), mc.player!!.pos.getY(), mc.player!!.pos.getZ())).block)) {
            mc.player!!.inventory.selectedSlot = slot
            if (WorldInteractionUtil.placeBlock(BlockPos(offX, offY, offZ).add(mc.player!!.pos.getX(), mc.player!!.pos.getY(), mc.player!!.pos.getZ()), Hand.MAIN_HAND, getBool("Rotations"))) placements++
        }
    } /*public boolean canPlace(BlockPos pos, Direction direction) {
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

    companion object {
        private var placements = 0
        private var slot = -1
    }

    init {
        settings.addBoolean("AutoDisable", true)
        settings.addBoolean("Center", true)
        settings.addBoolean("All blocks", false)
        settings.addBoolean("Rotations", false)
        settings.addSlider("Blocks/Tick", 1.0, 2.0, 8.0)
    }
}