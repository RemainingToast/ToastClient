package dev.toastmc.toastclient.impl.module.combat

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.InventoryUtil.putInHotbar
import dev.toastmc.toastclient.api.util.WorldUtil
import dev.toastmc.toastclient.api.util.WorldUtil.block
import dev.toastmc.toastclient.api.util.WorldUtil.centeredVec3d
import dev.toastmc.toastclient.api.util.WorldUtil.isSurrounded
import dev.toastmc.toastclient.api.util.entity.canReach
import dev.toastmc.toastclient.api.util.entity.eyePos
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.math.Box

object AutoAnvil : Module("AutoAnvil", Category.COMBAT) {

    var surrounded = bool("Surrounded", true)
    var rotations = bool("Rotations", true)
    var range = number("Range", 4.5, 0.0, 6.0, 2)
    var trapHeight = number("TrapHeight", 5.0, 0.0, 30.0, 2)
    var fallHeight = number("FallHeight", 30.0, 0.0, 30.0, 2)
    var obbySlot = number("ObbySlot", 3, 1, 9, 2)
    var anvilSlot = number("AnvilSlot", 8, 1, 9, 2)

    override fun onUpdate() {
        if (mc.player == null) return

        val targetOpt = mc.world!!.entities.toList().filterIsInstance(PlayerEntity::class.java).parallelStream().filter {
            (!surrounded.value || it.blockPos.isSurrounded()) && mc.player!!.blockPos.x != it.blockPos.x && mc.player!!.blockPos.z != it.blockPos.z
        }.sorted { player1, player2 ->
            player1.distanceTo(mc.player).compareTo(player2.distanceTo(mc.player))
        }.findFirst()

        val target = if (targetOpt.isPresent) targetOpt.get() else return

        var completedAction = false

        for (h in 0..trapHeight.intValue) {
            val current = target.blockPos.add(0, h, 0)
            if (!current.isSurrounded()) {
                for (offset in WorldUtil.surroundOffsets) {
                    val pos = current.add(offset)
                    if (mc.player!!.canReach(Box(pos), range.floatValue.toDouble()) && WorldUtil.isReplaceable(pos.block)) {
                        mc.player!!.putInHotbar(Items.OBSIDIAN, obbySlot.intValue - 1)
                        mc.player!!.inventory.selectedSlot = obbySlot.intValue - 1

                        completedAction = WorldUtil.placeBlock(pos, Hand.MAIN_HAND, rotations.value)
                        break
                    }
                }
                break
            }
        }

        if (completedAction) return

        val closestOffset = WorldUtil.surroundOffsets.sortedWith { o1, o2 ->
            target.blockPos.add(o1).centeredVec3d.distanceTo(mc.player!!.eyePos)
                .compareTo(target.blockPos.add(o2).centeredVec3d.distanceTo(mc.player!!.eyePos))
        }.getOrNull(0) ?: return

        val closest = target.blockPos.add(closestOffset)

        for (h in 0..fallHeight.intValue) {
            val current = closest.add(0, h, 0)
            if (mc.player!!.canReach(Box(current), range.floatValue.toDouble()) && WorldUtil.isReplaceable(current.block)) {
                mc.player!!.putInHotbar(Items.OBSIDIAN, obbySlot.intValue - 1)
                mc.player!!.inventory.selectedSlot = obbySlot.intValue - 1

                completedAction = WorldUtil.placeBlock(current, Hand.MAIN_HAND, rotations.value)
                break
            }
        }

        if (completedAction) return

        for (h in fallHeight.intValue downTo 3) {
            val current = target.blockPos.add(0, h, 0)
            if (mc.player!!.canReach(Box(current), range.floatValue.toDouble()) && WorldUtil.isReplaceable(current.block) && !WorldUtil.AIR.contains(current.add(closestOffset).block)) {
                mc.player!!.putInHotbar(Items.ANVIL, anvilSlot.intValue - 1)
                mc.player!!.inventory.selectedSlot = anvilSlot.intValue - 1

                if (WorldUtil.placeBlock(current, Hand.MAIN_HAND, rotations.value)) break
            }
        }
    }

}

