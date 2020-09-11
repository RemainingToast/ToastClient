package dev.toastmc.client.module.player

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.PlayerAttackBlockEvent
import dev.toastmc.client.event.PlayerAttackEntityEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.ItemUtil
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

    override fun onDisable() {
        if (mc.player != null) mc.player!!.inventory.selectedSlot = lastSlot
        ToastClient.EVENT_BUS.unsubscribe(leftClickListener)
        ToastClient.EVENT_BUS.unsubscribe(attackListener)
    }

    override fun onEnable() {
        super.onEnable()
        ToastClient.EVENT_BUS.subscribe(leftClickListener)
        ToastClient.EVENT_BUS.subscribe(attackListener)
    }

    @EventHandler
    private val leftClickListener = Listener(EventHook<PlayerAttackBlockEvent> { event: PlayerAttackBlockEvent ->
        mc.world?.getBlockState(event.position)?.let {
            ItemUtil.equipBestTool(it)
        }
    })


    @EventHandler
    private val attackListener = Listener(EventHook<PlayerAttackEntityEvent> { event: PlayerAttackEntityEvent? ->
        ItemUtil.equipBestWeapon()
    })
}
