package dev.toastmc.client.module.combat

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.WorldInteractionUtil
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.lang.Math.*

@ModuleManifest(
    label = "Surround",
    description = "Places obby around you.",
    category = Category.COMBAT
)
class Surround : Module() {
    @Setting(name = "AutoDisable") var autodisable = true
    @Setting(name = "Center") var center = true
    @Setting(name = "AllBlocks") var allblocks = false
    @Setting(name = "Rotations") var rotations = false
    @Setting(name = "BPT") var blockspertick = 8

    override fun onDisable() {
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
    }

    override fun onEnable() {
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        val lastSlot = mc.player!!.inventory.selectedSlot
        slot = mc.player!!.inventory.selectedSlot
        slot = when (allblocks) {
            true -> {
                if (!has4OrMoreBuildBlockInHotbar()) return@EventHook
                slotForBuildBlocksInHotbar
            }
            false -> {
                if (!hasInHotbar(Items.OBSIDIAN)) return@EventHook
                getItemSlotInHotbar(Items.OBSIDIAN)
            }
        }
        mc.player!!.inventory.selectedSlot = slot
        if (center) centerPlayerPos()
        placements = 0
        for (i in 1..8) {
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
                if (autodisable) disable()
            }
        }
        mc.player!!.inventory.selectedSlot = lastSlot
    })

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
            val currentItem = mc.player!!.inventory.getStack(i).item
            if (!mc.player!!.inventory.getStack(i).isEmpty) {
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
            val curritem = mc.player!!.inventory.getStack(i)
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
                val currentItem = mc.player!!.inventory.getStack(i)
                if (!mc.player!!.inventory.getStack(i).isEmpty) {
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
            val currentItem = mc.player!!.inventory.getStack(i).item
            if (!mc.player!!.inventory.getStack(i).isEmpty) {
                if (item === currentItem) {
                    slot = i
                }
            }
        }
        return slot
    }

    private fun tryPlace(offX: Double, offY: Double, offZ: Double) {
        if (placements < blockspertick && WorldInteractionUtil.isReplaceable(mc.world!!.getBlockState(BlockPos(offX, offY, offZ).add(mc.player!!.pos.getX(), mc.player!!.pos.getY(), mc.player!!.pos.getZ())).block)) {
            mc.player!!.inventory.selectedSlot = slot
            if (WorldInteractionUtil.placeBlock(BlockPos(offX, offY, offZ).add(mc.player!!.pos.getX(), mc.player!!.pos.getY(), mc.player!!.pos.getZ()), Hand.MAIN_HAND, rotations)) placements++
        }
    }

    companion object {
        private var placements = 0
        private var slot = -1
    }
}