package dev.toastmc.client.util

import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType
import java.util.*


// Orginally made for 1.12.2 forge by Dominkaa https://github.com/kami-blue/client/blob/master/src/main/java/me/zeroeightsix/kami/util/InventoryUtils.kt
// Ported to 1.16.2 fabric by RemainingToast

object InventoryUtils {

    private fun getInventory(): Map<Int, ItemStack> {
        return getInventorySlots(9, 35)
    }

    fun getHotbar(): Map<Int, ItemStack> {
        return getInventorySlots(36, 44)
    }

    private fun getInventorySlots(current: Int, last: Int): Map<Int, ItemStack> {
        var current = current
        val fullInventorySlots: MutableMap<Int, ItemStack> = HashMap()
        while (current <= last) {
            fullInventorySlots[current] = mc.player!!.inventory.getStack(current)
            current++
        }
        return fullInventorySlots
    }

    /**
     * Returns slots contains item with given item id in player inventory
     *
     * @return Array contains slot index, null if no item found
     */
    fun getSlots(min: Int, max: Int, itemId: Int): Array<Int>? {
        val slots = arrayListOf<Int>()
        for (i in min..max) {
            if (Item.getRawId(mc.player!!.inventory.getStack(i).item) == itemId) {
                slots.add(i)
            }
        }
        return if (slots.isNotEmpty()) slots.toTypedArray() else null
    }

    fun getSlots(min: Int, max: Int, itemStack: ItemStack): Array<Int>? {
        return getSlots(min, max, Item.getRawId(itemStack.item))
    }

    /**
     * Returns slots contains item with given item id in player hotbar
     *
     * @return Array contains slot index, null if no item found
     */
    fun getSlotsHotbar(itemId: Int): Array<Int>? {
        return getSlots(0, 8, itemId)!!
    }

    /**
     * Returns slots contains with given item id in player inventory (without hotbar)
     *
     * @return Array contains slot index, null if no item found
     */
    fun getSlotsNoHotbar(itemId: Int): Array<Int>? {
        return getSlots(9, 35, itemId)
    }

    fun getEmptySlotContainer(min: Int, max: Int): Int? {
        return getSlotsContainer(min, max, 0)?.get(0)
    }

    fun getEmptySlotFullInv(min: Int, max: Int): Int? {
        return getSlotsFullInv(min, max, 0)?.get(0)
    }

    /**
     * Returns slots in full inventory contains item with given [itemId] in current open container
     *
     * @return Array contains full inventory slot index, null if no item found
     */
    fun getSlotsContainer(min: Int, max: Int, itemId: Int): Array<Int>? {
        val slots = arrayListOf<Int>()
        for (i in min..max) {
            if (Item.getRawId(mc.player!!.inventory.getStack(i).item) == itemId) {
                slots.add(i)
            }
        }
        return if (slots.isNotEmpty()) slots.toTypedArray() else null
    }

    /**
     * Returns slots in full inventory contains item with given [itemId] in player inventory
     * This is same as [getSlots] but it returns full inventory slot index
     *
     * @return Array contains full inventory slot index, null if no item found
     */
    fun getSlotsFullInv(min: Int, max: Int, itemId: Int): Array<Int>? {
        val slots = arrayListOf<Int>()
        for (i in min..max) {
            if (Item.getRawId(mc.player!!.inventory.getStack(i).item) == itemId) {
                slots.add(i)
            }
        }
        return if (slots.isNotEmpty()) slots.toTypedArray() else null
    }

    fun getSlotFullInv(min: Int, max: Int, itemId: Int): Int? {
        val i = getSlotsFullInv(min, max, itemId)?.getOrNull(0)
        return if (i != -1) i else null
    }

    /**
     * Returns slots contains item with given [itemId] in player hotbar
     * This is same as [getSlots] but it returns full inventory slot index
     *
     * @return Array contains slot index, null if no item found
     */
    fun getSlotsFullInvHotbar(itemId: Int): Array<Int>? {
        return getSlotsFullInv(36, 44, itemId)
    }

    /**
     * Returns slots contains with given [itemId] in player inventory (without hotbar)
     * This is same as [getSlots] but it returns full inventory slot index
     *
     * @return Array contains slot index, null if no item found
     */
    fun getSlotsFullInvNoHotbar(itemId: Int): Array<Int>? {
        return getSlotsFullInv(9, 35, itemId)
    }

    /**
     * Counts number of item in range of slots
     *
     * @return Number of item with given [itemId] from slot [min] to slot [max]
     */
    fun countItem(min: Int, max: Int, itemId: Int): Int {
        val itemList = getSlots(min, max, itemId)
        var currentCount = 0
        if (itemList != null) {
            for (i in itemList) {
                currentCount += mc.player!!.inventory.getStack(i).count
            }
        }
        return currentCount
    }

    /* Inventory management */
    var inProgress = false

    /**
     * Swap current held item to given [slot]
     */
    fun swapSlot(slot: Int) {
        mc.player!!.inventory.selectedSlot = slot
//        mc.playerController.syncCurrentPlayItem()
    }

    /**
     * Try to swap current held item to item with given [itemID]
     */
    fun swapSlotToItem(itemID: Int) {
        if (getSlotsHotbar(itemID) != null) {
            swapSlot(getSlotsHotbar(itemID)!![0])
        }
//        mc.playerController.syncCurrentPlayItem()
    }

    private fun inventoryClick(slot: Int, type: SlotActionType) {
        inventoryClick(0, slot, type)
    }

    private fun inventoryClick(windowID: Int, slot: Int, type: SlotActionType) {
        mc.interactionManager!!.clickSlot(windowID, slot, 0, type, mc.player)
    }

    /**
     * Try to move item with given [itemID] to empty hotbar slot or slot contains no exception [exceptionID]
     * If none of those found, then move it to slot 0
     */
    fun moveToHotbar(itemID: Int, exceptionID: Int, delayMillis: Long) {
        val slot1 = getSlotsFullInvNoHotbar(itemID)!![0]
        var slot2 = 36
        for (i in 36..44) { /* Finds slot contains no exception item first */
            val currentItemStack = mc.player!!.inventory.getStack(i)
            if (currentItemStack.isEmpty) {
                slot2 = i
                break
            }
            if (Item.getRawId(currentItemStack.item) != exceptionID) {
                slot2 = i
                break
            }
        }
        moveToSlot(slot1, slot2, delayMillis)
    }

    /**
     * Move the item in [slotFrom] in player inventory to [slotTo] in player inventory , if [slotTo] contains an item,
     * then move it to [slotFrom]
     */
    fun moveToSlot(slotFrom: Int, slotTo: Int, delayMillis: Long) {
        moveToSlot(0, slotFrom, slotTo, delayMillis)
    }

    /**
     * Move the item in [slotFrom] in [windowId] to [slotTo] in [windowId],
     * if [slotTo] contains an item, then move it to [slotFrom]
     */
    fun moveToSlot(windowId: Int, slotFrom: Int, slotTo: Int, delayMillis: Long) {
        while (inProgress) {
            inProgress = true
//            val prevScreen = mc.currentScreen
//            if (prevScreen !is InventoryScreen) mc.openScreen(InventoryScreen(mc.player))
            inventoryClick(windowId, slotFrom, SlotActionType.PICKUP)
            inventoryClick(windowId, slotTo, SlotActionType.PICKUP)
            inventoryClick(windowId, slotFrom, SlotActionType.PICKUP)
//            if (prevScreen !is InventoryScreen) mc.openScreen(prevScreen)
            inProgress = false
        }
    }

    /**
     * Move all the item that equals to the item in [slotFrom] to [slotTo],
     * if [slotTo] contains an item, then move it to [slotFrom]
     * Note: Not working
     */
    fun moveAllToSlot(slotFrom: Int, slotTo: Int, delayMillis: Long) {
        if (inProgress) return
        Thread(Runnable {
            inProgress = true
            val prevScreen = mc.currentScreen
            mc.openScreen(InventoryScreen(mc.player))
            Thread.sleep(delayMillis)
            inventoryClick(slotTo, SlotActionType.PICKUP_ALL)
            Thread.sleep(delayMillis)
            inventoryClick(slotTo, SlotActionType.PICKUP)
            mc.openScreen(prevScreen)
            inProgress = false
        }).start()
    }

    /**
     * Quick move (Shift + Click) the item in [slotFrom] in player inventory
     */
    fun quickMoveSlot(slotFrom: Int, delayMillis: Long) {
        quickMoveSlot(0, slotFrom, delayMillis)
    }

    /**
     * Quick move (Shift + Click) the item in [slotFrom] in specified [windowID]
     */
    fun quickMoveSlot(windowID: Int, slotFrom: Int, delayMillis: Long) {
        if (inProgress) return
        Thread(Runnable {
            inProgress = true
            inventoryClick(windowID, slotFrom, SlotActionType.QUICK_MOVE)
            Thread.sleep(delayMillis)
            inProgress = false
        }).start()
    }

    /**
     * Throw all the item in [slot] in player inventory
     */
    fun throwAllInSlot(slot: Int, delayMillis: Long) {
        if (inProgress) return
        Thread(Runnable {
            inProgress = true
            mc.interactionManager!!.clickSlot(0, slot, 1, SlotActionType.THROW, mc.player)
            Thread.sleep(delayMillis)
            inProgress = false
        }).start()
    }
}