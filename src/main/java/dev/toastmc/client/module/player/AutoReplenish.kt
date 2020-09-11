package dev.toastmc.client.module.player

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.InventoryUtils.getSlotsFullInv
import dev.toastmc.client.util.InventoryUtils.getSlotsHotbar
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
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
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
    }

    override fun onEnable() {
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null || mc.currentScreen is InventoryScreen) return@EventHook
        delayStep = if (delayStep < delay) { delayStep++; return@EventHook } else 0
        val (inventorySlot, hotbarSlot) = findReplenishableHotbarSlot(mc.player!!.inventory.mainHandStack.item)
        if (inventorySlot == -1 || inventorySlot == null || hotbarSlot == -1 || mc.player!!.inventory.mainHandStack.count >= threshold) return@EventHook
        mc.interactionManager!!.clickSlot(0, inventorySlot, 0, SlotActionType.PICKUP, mc.player)
        mc.interactionManager!!.clickSlot(0, hotbarSlot, 0, SlotActionType.PICKUP, mc.player)
        mc.interactionManager!!.clickSlot(0, inventorySlot, 0, SlotActionType.PICKUP, mc.player)
    })

    private fun findReplenishableHotbarSlot(item: Item): Pair<Int?, Int> {
        println("Trying to find replenishable slot")
        val returnPair = Pair(getCompatibleStack(item.stackForRender), getSlotsHotbar(Item.getRawId(item))!!.first())
        println("Found: $returnPair and inventory slot is ${getCompatibleStack(item.stackForRender)} and the hotbar slot is ${getSlotsHotbar(Item.getRawId(item))!!.first()}")
        return returnPair
    }

    private fun getCompatibleStack(stack: ItemStack): Int? {
        val slots = getSlotsFullInv(9, 44, Item.getRawId(stack.item)) ?: return null
        for (i in slots.indices) {
            val currentSlot = slots[i]
            println("${stack.item.name} and ${mc.player!!.inventory.getStack(currentSlot).item.name} are ${if (isCompatibleStacks(stack, mc.player!!.inventory.getStack(currentSlot))) "" else "NOT"} compatiable")
            if (isCompatibleStacks(stack, mc.player!!.inventory.getStack(currentSlot))) return currentSlot
        }
        return null
    }

    private fun isCompatibleStacks(stack1: ItemStack, stack2: ItemStack): Boolean {
        return stack1.isItemEqual(stack2) && ItemStack.areTagsEqual(stack2, stack1)
    }

}