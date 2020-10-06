package dev.toastmc.client.module.player

import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.WorldUtil
import dev.toastmc.client.util.mc
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.item.BlockItem
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import java.util.*


@ModuleManifest(
        label = "Scaffold",
        category = Category.PLAYER
)
class Scaffold : Module() {
    @Setting(name = "Range") var range = 1
    @Setting(name = "Tower") var tower = true

    var lastPlaced: HashMap<BlockPos, Int> = HashMap()
    val towering = false

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        for ((k, v) in lastPlaced) {
            if (v > 0) lastPlaced.replace(k, v - 1) else lastPlaced.remove(k)
        }
        var slot = -1
        var prevSlot: Int = mc.player!!.inventory.selectedSlot
        if (mc.player!!.inventory.mainHandStack.item is BlockItem) {
            slot = mc.player!!.inventory.selectedSlot
        } else for (i in 0..8) {
            if (mc.player!!.inventory.getStack(i).item is BlockItem) {
                slot = i
                break
            }
        }
        if (slot == -1) return@EventHook
        mc.player!!.inventory.selectedSlot = slot
        for (r in 0..4) {
            var r1 = Vec3d(0.0, -0.85, 0.0)
            if (r == 1) r1 = r1.add(range.toDouble(), 0.0, 0.0)
            if (r == 2) r1 = r1.add(-range.toDouble(), 0.0, 0.0)
            if (r == 3) r1 = r1.add(0.0, 0.0, range.toDouble())
            if (r == 4) r1 = r1.add(0.0, 0.0, -range.toDouble())
            if (place(BlockPos(mc.player!!.pos.add(r1)))) {
                return@EventHook
            }
        }
    })

    private fun place(block: BlockPos): Boolean {
        if (lastPlaced.containsKey(block) || !WorldUtil.REPLACEABLE.contains(mc.world!!.getBlockState(block).block)) {
            return false
        }
        for (d in Direction.values()) {
            if (!WorldUtil.REPLACEABLE.contains(mc.world!!.getBlockState(block.offset(d)).getBlock())) {
                if (WorldUtil.isRightClickable(mc.world!!.getBlockState(block.offset(d)))) {
                    mc.player!!.networkHandler.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY))
                }
                mc.interactionManager!!.interactBlock(mc.player, mc.world, Hand.MAIN_HAND,
                        BlockHitResult(Vec3d(block.x.toDouble(), block.y.toDouble(), block.z.toDouble()), d.opposite, block.offset(d), false))
                mc.player!!.swingHand(Hand.MAIN_HAND)
                if (WorldUtil.isRightClickable(mc.world!!.getBlockState(block.offset(d)))) {
                    mc.player!!.networkHandler.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY))
                }
                lastPlaced[block] = 5
                return true
            }
        }
        return false
    }

}