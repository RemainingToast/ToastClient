package dev.toastmc.client.module.player

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.PlayerAttackBlockEvent
import dev.toastmc.client.event.PlayerAttackEntityEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.ItemUtil.equipBestTool
import dev.toastmc.client.util.ItemUtil.equipBestWeapon
import dev.toastmc.client.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener

@ModuleManifest(
        label = "AutoTool",
        description = "Picks best tool",
        category = Category.PLAYER
)
class AutoTool : Module(){

    private var lastSlot = 0

    override fun onEnable() {
        EVENT_BUS.subscribe(leftClickListener)
        EVENT_BUS.subscribe(attackListener)
    }

    override fun onDisable() {
        if(lastSlot != -1) mc.player!!.inventory.selectedSlot = lastSlot
        EVENT_BUS.unsubscribe(leftClickListener)
        EVENT_BUS.unsubscribe(attackListener)
    }

    @EventHandler
    private val leftClickListener = Listener(EventHook<PlayerAttackBlockEvent> {
        if (mc.player == null) return@EventHook
        val blockState = mc.world?.getBlockState(it.position) ?: return@EventHook
        equipBestTool(blockState)
    })


    @EventHandler
    private val attackListener = Listener(EventHook<PlayerAttackEntityEvent> {
        equipBestWeapon()
    })
}
