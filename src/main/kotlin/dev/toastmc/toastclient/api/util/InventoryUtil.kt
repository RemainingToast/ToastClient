package dev.toastmc.toastclient.api.util

import dev.toastmc.toastclient.IToastClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.SlotActionType

object InventoryUtil : IToastClient {

    fun hasItem(vararg items: Item): Boolean {
        return mc.player?.inventory?.containsAny(items.toSet()) ?: false
    }

    fun itemCount(item: Item): Int {
        return mc.player?.inventory?.count(item) ?: 0
    }

    fun getSlotWithItem(item: Item): Int? {
        if (mc.player == null) return null
        return (0..44).firstOrNull { item == mc.player!!.inventory.getStack(it).item }
    }

    fun getHotbarSlot(item: Item): Int? {
        if (mc.player == null) return null
        return (0..8).firstOrNull { item == mc.player!!.inventory.getStack(it).item }
    }

    fun switchToHotbarItem(item: Item): Boolean {
        if (mc.player == null) return false
        mc.player!!.inventory.selectedSlot = getHotbarSlot(item) ?: return false
        return false
    }

    fun pickup(slot: Int) {
        if (mc.player == null) return
        mc.interactionManager!!.clickSlot(0, if (slot < 9) slot + 36 else slot, 0, SlotActionType.PICKUP, mc.player)
    }

    fun pickupOffHand() {
        if (mc.player == null) return
        mc.interactionManager!!.clickSlot(0, 45, 0, SlotActionType.PICKUP, mc.player)
    }

    fun getOffHandStack(): ItemStack {
        if (mc.player == null) return ItemStack.EMPTY
        return mc.player!!.inventory.offHand.firstOrNull() ?: ItemStack.EMPTY
    }

    fun transferSlot(slot1: Int, slot2: Int) {
        pickup(slot1)
        pickup(slot2)
    }

    fun transferItem(item: Item, slot: Int) {
        pickup(getSlotWithItem(item) ?: return)
        pickup(slot)
    }

    fun asyncTransferSlot(slot1: Int, slot2: Int, delay: Long) {
        GlobalScope.async {
            pickup(slot1)
            delay(delay)
            pickup(slot2)
        }.start()
    }

    fun transferToOffHand(slot: Int) {
        transferSlot(slot, 45)
    }

    fun transferToOffHand(item: Item, delay: Long) {
        transferSlot(getSlotWithItem(item) ?: return, 45)
    }

    fun asyncTransferToOffHand(slot: Int, delay: Long) {
        asyncTransferSlot(slot, 45, delay)
    }

    fun asyncTransferToOffHand(item: Item, delay: Long) {
        asyncTransferSlot(getSlotWithItem(item) ?: return, 45, delay)
    }

    fun findEmptySlot(): Int? {
        return getSlotWithItem(Items.AIR) ?: null
    }

    fun isCursorEmpty(): Boolean {
        if (mc.player == null) return true
        logger.info(mc.player!!.inventory.cursorStack.toString())
        return mc.player!!.inventory.cursorStack != ItemStack.EMPTY
    }

    fun cleanupTransfer(slot: Int): Boolean {
        if (mc.player == null) return true
        if (isCursorEmpty()) {
            pickup(slot)
        }
        return true;
    }

    fun cleanupTransfer(): Boolean {
        return cleanupTransfer(findEmptySlot() ?: return false)
    }

    fun putInHotbar(item: Item, slot: Int) {
        if (mc.player == null || item == mc.player!!.inventory.getStack(slot).item) return
        val foundItem = getSlotWithItem(item)
        transferSlot(foundItem ?: return, slot)
        cleanupTransfer(foundItem)
    }

}