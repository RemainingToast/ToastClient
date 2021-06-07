package dev.toastmc.toastclient.api.util

import dev.toastmc.toastclient.IToastClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.SlotActionType

object InventoryUtil : IToastClient {

    fun ClientPlayerEntity.hasItem(vararg items: Item): Boolean {
        return inventory.containsAny(items.toSet())
    }

    fun ClientPlayerEntity.count(item: Item): Int {
        return inventory.count(item)
    }

    fun ClientPlayerEntity.getSlotWithItem(item: Item, range: IntRange = (0..44)): Int? {
        return range.firstOrNull { item == inventory.getStack(it).item }
    }

    fun ClientPlayerEntity.switchToHotbarItem(item: Item): Boolean {
        inventory.selectedSlot = getSlotWithItem(item, 0..8) ?: return false
    }

    fun ClientPlayerEntity.pickup(slot: Int) {
        mc.interactionManager!!.clickSlot(0, if (slot < 9) slot + 36 else slot, 0, SlotActionType.PICKUP, this)
    }

    fun ClientPlayerEntity.pickupOffHand() {
        mc.interactionManager!!.clickSlot(0, 45, 0, SlotActionType.PICKUP, this)
    }

    fun ClientPlayerEntity.transferSlot(slot1: Int, slot2: Int) {
        pickup(slot1)
        pickup(slot2)
    }

    fun ClientPlayerEntity.transferItem(item: Item, slot: Int) {
        pickup(getSlotWithItem(item) ?: return)
        pickup(slot)
    }

    fun ClientPlayerEntity.asyncTransferSlot(slot1: Int, slot2: Int, delay: Long) {
        GlobalScope.async {
            pickup(slot1)
            delay(delay)
            pickup(slot2)
        }.start()
    }

    fun ClientPlayerEntity.transferToOffHand(slot: Int) {
        transferSlot(slot, 45)
    }

    fun ClientPlayerEntity.transferToOffHand(item: Item, delay: Long) {
        transferSlot(getSlotWithItem(item) ?: return, 45)
    }

    fun ClientPlayerEntity.asyncTransferToOffHand(slot: Int, delay: Long) {
        asyncTransferSlot(slot, 45, delay)
    }

    fun ClientPlayerEntity.asyncTransferToOffHand(item: Item, delay: Long) {
        asyncTransferSlot(getSlotWithItem(item) ?: return, 45, delay)
    }

    fun ClientPlayerEntity.findEmptySlot(): Int? {
        return getSlotWithItem(Items.AIR)
    }

    fun ClientPlayerEntity.isCursorEmpty(): Boolean {
        return inventory.cursorStack != ItemStack.EMPTY
    }

    fun ClientPlayerEntity.cleanupTransfer(slot: Int): Boolean {
        if (isCursorEmpty()) {
            pickup(slot)
        }
        return true
    }

    fun ClientPlayerEntity.cleanupTransfer(): Boolean {
        return cleanupTransfer(findEmptySlot() ?: return false)
    }

    fun ClientPlayerEntity.putInSlot(item: Item, slot: Int) {
        if (item == inventory.getStack(slot).item) return
        val foundItem = getSlotWithItem(item)
        transferSlot(foundItem ?: return, slot)
        cleanupTransfer(foundItem)
    }

}