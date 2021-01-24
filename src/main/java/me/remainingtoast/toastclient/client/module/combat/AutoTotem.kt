package me.remainingtoast.toastclient.client.module.combat

import me.remainingtoast.toastclient.ToastClient.Companion.EVENT_BUS
import me.remainingtoast.toastclient.api.event.TickEvent
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.type.BooleanSetting
import me.remainingtoast.toastclient.api.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.SlotActionType

object AutoTotem : Module("AutoTotem", Category.COMBAT) {

    var totems = 0
    var totemsTotal = 0
    var moving = false
    var returning = false

    override fun onEnable() {
        EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        totems = mc.player!!.inventory.main.stream().filter { itemStack: ItemStack -> itemStack.item === Items.TOTEM_OF_UNDYING }.mapToInt { obj: ItemStack -> obj.count }.sum()
        totemsTotal = totems + mc.player!!.inventory.offHand.stream().filter { itemStack: ItemStack -> itemStack.item === Items.TOTEM_OF_UNDYING }.mapToInt { obj: ItemStack -> obj.count }.sum()
        if (mc.player!!.offHandStack.item == Items.TOTEM_OF_UNDYING) totems++ else {
            if (!mc.player!!.inventory.offHand.isEmpty() || mc.currentScreen is InventoryScreen) return@EventHook
            when {
                moving -> {
                    mc.interactionManager!!.clickSlot(0, 45, 0, SlotActionType.PICKUP, mc.player)
                    moving = false
                    if (!mc.player!!.inventory.isEmpty) returning = true
                    return@EventHook
                }
                !mc.player!!.inventory.isEmpty -> {
                    if (totems == 0) return@EventHook
                    val t = (0..44).firstOrNull { mc.player!!.inventory.getStack(it).item === Items.TOTEM_OF_UNDYING } ?: -1
                    if (t == -1) return@EventHook
                    mc.interactionManager!!.clickSlot(0, if (t < 9) t + 36 else t, 0, SlotActionType.PICKUP, mc.player)
                    moving = true
                }
            }
        }
    })
}