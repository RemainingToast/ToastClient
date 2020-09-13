package dev.toastmc.client.module.player

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.InventoryUtils.getSlotFullInv
import dev.toastmc.client.util.InventoryUtils.getSlotsHotbar
import dev.toastmc.client.util.mc
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.SlotActionType

@ModuleManifest(
        label = "AutoReplenish",
        description = "Refills ur hotbar",
        category = Category.PLAYER
)
class AutoReplenish : Module() {
    @Setting(name = "Threshold") var threshold = 63
    @Setting(name = "Delay") var delay = 2

    private var delayStep = 0

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onTickEvent)
    }

    override fun onEnable() {
        EVENT_BUS.subscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null || mc.currentScreen is InventoryScreen || mc.player!!.inventory.mainHandStack.item == Items.AIR) return@EventHook
        delayStep = if (delayStep < delay) { delayStep++; return@EventHook } else 0
        val (inventorySlot, hotbarSlot) = findReplenishableHotbarSlot(mc.player!!.inventory.mainHandStack.item)
        if (inventorySlot == -1 || inventorySlot == null || hotbarSlot == -1 || hotbarSlot == null || mc.player!!.inventory.mainHandStack.count >= threshold) return@EventHook
        mc.interactionManager!!.clickSlot(0, inventorySlot, 0, SlotActionType.PICKUP, mc.player)
        mc.interactionManager!!.clickSlot(0, hotbarSlot, 0, SlotActionType.PICKUP, mc.player)
        mc.interactionManager!!.clickSlot(0, inventorySlot, 0, SlotActionType.PICKUP, mc.player)
    })

    private fun findReplenishableHotbarSlot(item: Item): Pair<Int?, Int?> {
        val returnPair = Pair(getCompatibleStack(item.stackForRender), getSlotsHotbar(Item.getRawId(item))!!.first())
        if (returnPair.first == null || returnPair.second == -1) return Pair(null, null)
        println("Found: $returnPair")
        return returnPair
    }

    private fun getCompatibleStack(stack: ItemStack): Int? {
        val slot = getSlotFullInv(9, 35, Item.getRawId(stack.item)) ?: return null
        println("$stack and ${mc.player!!.inventory.getStack(slot)} are ${if (isCompatibleStacks(stack, mc.player!!.inventory.getStack(slot))) "" else "NOT"} compatiable")
        return if (isCompatibleStacks(stack, mc.player!!.inventory.getStack(slot))) slot else null
    }

    private fun isCompatibleStacks(stack1: ItemStack, stack2: ItemStack): Boolean {
        return stack1.isItemEqual(stack2) && ItemStack.areTagsEqual(stack2, stack1)
    }

}