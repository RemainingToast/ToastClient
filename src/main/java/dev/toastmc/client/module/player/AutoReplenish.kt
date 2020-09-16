package dev.toastmc.client.module.player

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.mc
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
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

    override fun onEnable() {
        EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null || mc.currentScreen is InventoryScreen || mc.currentScreen is CreativeInventoryScreen || mc.player!!.inventory.mainHandStack.item == Items.AIR) return@EventHook
        delayStep = if (delayStep < delay) { delayStep++; return@EventHook } else 0
        val (inventorySlot, hotbarSlot) = findReplenishableHotbarSlot(mc.player!!.inventory.mainHandStack)
        if (inventorySlot == -1 || inventorySlot == null || hotbarSlot == -1 || hotbarSlot == null) return@EventHook
        mc.interactionManager!!.clickSlot(0, inventorySlot, 0, SlotActionType.PICKUP, mc.player)
        mc.interactionManager!!.clickSlot(0, hotbarSlot, 0, SlotActionType.PICKUP, mc.player)
        mc.interactionManager!!.clickSlot(0, inventorySlot, 0, SlotActionType.PICKUP, mc.player)
    })

    private fun findReplenishableHotbarSlot(itemStack: ItemStack): Pair<Int?, Int?> {
        val t = (0..8).firstOrNull { mc.player!!.inventory.getStack(it).item == itemStack.item } ?: -1
        val returnPair = Pair(getCompatibleStack(itemStack), t)
        println("Found: $returnPair")
        return returnPair
    }

    private fun getCompatibleStack(itemStack: ItemStack): Int? {
        val t = (9..35).firstOrNull { mc.player!!.inventory.getStack(it).item == itemStack.item } ?: return -1
        println("$itemStack and ${mc.player!!.inventory.getStack(t)} are ${if (isCompatibleStacks(itemStack, mc.player!!.inventory.getStack(t))) "" else "NOT"} compatiable")
        return if (isCompatibleStacks(itemStack, mc.player!!.inventory.getStack(t))) t else null
    }

    private fun isCompatibleStacks(stack1: ItemStack, stack2: ItemStack): Boolean {
        return stack1.isItemEqual(stack2) && ItemStack.areTagsEqual(stack2, stack1)
    }

}