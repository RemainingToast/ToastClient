package dev.toastmc.client.module.player

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.InventoryUtils
import dev.toastmc.client.util.InventoryUtils.getSlotsFullInvNoHotbar
import dev.toastmc.client.util.InventoryUtils.moveToSlot
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import kotlin.math.ceil

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
        if (mc.player == null) return
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
    }

    override fun onEnable() {
        if (mc.player == null) return
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null || mc.currentScreen is InventoryScreen || InventoryUtils.inProgress) return@EventHook
        delayStep = if (delayStep < delay) {delayStep++; return@EventHook} else 0

        val slotTo = getRefillableSlot() ?: return@EventHook
        val stackTo = mc.player!!.inventory.getStack(slotTo)
        val slotFrom = getCompatibleStack(stackTo) ?: return@EventHook

        moveToSlot(slotFrom, slotTo, (delay * 50).toLong())
    })

    private fun getRefillableSlot(): Int? {
        for (i in 0..8) {
            val currentStack = mc.player!!.inventory.getStack(i)
            val stackTarget = ceil(currentStack.maxCount / 64.0f * threshold).toInt()
            if (currentStack.isEmpty) continue
            if (!currentStack.isStackable || currentStack.count > stackTarget) continue
            if (getCompatibleStack(currentStack) == null) continue
            println("Refillable Slot at $i")
            return i
        }
        return null
    }

    private fun getCompatibleStack(stack: ItemStack): Int? {
        val slots = getSlotsFullInvNoHotbar(Item.getRawId(stack.item)) ?: return null
        for (i in slots.indices) {
            val currentSlot = slots[i]
            println("$stack and ${mc.player!!.inventory.getStack(currentSlot)} are ${if(isCompatibleStacks(stack, mc.player!!.inventory.getStack(currentSlot))) "" else "NOT"} compatiable")
            if (isCompatibleStacks(stack, mc.player!!.inventory.getStack(currentSlot))) return currentSlot
        }
        return null
    }

    private fun isCompatibleStacks(stack1: ItemStack, stack2: ItemStack): Boolean {
        return stack1.isItemEqual(stack2) && ItemStack.areTagsEqual(stack2, stack1)
    }
}