package dev.toastmc.client.module.combat


import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.events.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.MessageUtil
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.SlotActionType

@ModuleManifest(
        label = "AutoTotem",
        description = "Put Totem into Offhand",
        category = Category.COMBAT,
        aliases = ["totem"]
)
class AutoTotem : Module(){
    var totems = 0
    var totemsTotal = 0
    var moving = false
    var returning = false
        
    private var force = true

    override fun onEnable() {
        super.onEnable()
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        super.onDisable()
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        totems = mc.player!!.inventory.main.stream().filter { itemStack: ItemStack -> itemStack.item === Items.TOTEM_OF_UNDYING }.mapToInt { obj: ItemStack -> obj.count }.sum()
        totemsTotal = totems + mc.player!!.inventory.offHand.stream().filter { itemStack: ItemStack -> itemStack.item === Items.TOTEM_OF_UNDYING }.mapToInt { obj: ItemStack -> obj.count }.sum()
        if (mc.player!!.offHandStack.item == Items.TOTEM_OF_UNDYING) totems++ else {
            if (!mc.player!!.inventory.offHand.isEmpty() && !force) return@EventHook
            when {
                moving -> {
                    mc.interactionManager!!.clickSlot(0, 45, 0, SlotActionType.PICKUP, mc.player)
                    moving = false
                    if (!mc.player!!.inventory.isEmpty) returning = true
                    return@EventHook
                }
                !mc.player!!.inventory.isEmpty -> {
                    if (totems == 0) return@EventHook
                    val t = (0..44).firstOrNull { mc.player!!.inventory.getStack(it).item === Items.TOTEM_OF_UNDYING }
                            ?: -1
                    if (t == -1) return@EventHook
                    mc.interactionManager!!.clickSlot(0, if (t < 9) t + 36 else t, 0, SlotActionType.PICKUP, mc.player)
                    moving = true
                }
            }
        }
    })
}
