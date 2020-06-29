package toast.client.modules.player

import com.google.common.eventbus.Subscribe
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ToolItem
import net.minecraft.util.math.BlockPos
import toast.client.events.player.EventAttack
import toast.client.modules.Module

class AutoTool : Module("AutoTool", "Automatically selects the ideal tool for breaking a block.", Category.PLAYER, -1) {
    private var lastSlot = 0

    //TODO: make this also look at enchantments
    private fun setSlot(blockPos: BlockPos?) {
        if (mc.player == null || mc.world == null) return
        var bestSpeed = 0.0f
        var bestSlot = -1
        if (mc.player!!.isCreative) return
        val block = mc.world!!.getBlockState(blockPos).block
        for (i in 0 until PlayerInventory.getHotbarSize()) {
            val item = mc.player!!.inventory.getInvStack(i)
            if (!item.isEmpty) {
                val speed = item.getMiningSpeed(block.defaultState)
                if (speed > bestSpeed) {
                    bestSpeed = speed
                    bestSlot = i
                }
            }
        }
        if (bestSlot != -1 && bestSlot != mc.player!!.inventory.selectedSlot) {
            lastSlot = mc.player!!.inventory.selectedSlot
            mc.player!!.inventory.selectedSlot = bestSlot
        }
    }

    private fun setSlot() {
        if (mc.player == null) return
        var bestValue = 0.0f
        var bestSlot = -1
        if (mc.player!!.isCreative) return
        for (i in 0 until PlayerInventory.getHotbarSize()) {
            val item = mc.player!!.inventory.getInvStack(i)
            if (!item.isEmpty && item.item is ToolItem) {
                val attackValue = (item.item as ToolItem).material.attackDamage
                if (attackValue > bestValue) {
                    bestValue = attackValue
                    bestSlot = i
                }
            }
        }
        if (bestSlot != -1 && bestSlot != mc.player!!.inventory.selectedSlot) {
            lastSlot = mc.player!!.inventory.selectedSlot
            mc.player!!.inventory.selectedSlot = bestSlot
        }
    }

    override fun onDisable() {
        if (mc.player != null) mc.player!!.inventory.selectedSlot = lastSlot
    }

    @Subscribe
    fun onEvent(event: EventAttack) {
        when {
            event.isAttackingBlock -> setSlot(event.getBlock())
            event.isAttackingEntity -> setSlot()
        }
    }
}