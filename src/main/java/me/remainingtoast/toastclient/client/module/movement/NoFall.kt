package me.remainingtoast.toastclient.client.module.movement

import me.remainingtoast.toastclient.ToastClient.Companion.EVENT_BUS
import me.remainingtoast.toastclient.api.event.TickEvent
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

object NoFall : Module("NoFall", Category.MOVEMENT) {

    override fun onEnable() {
        EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        if (mc.player!!.isFallFlying && mc.player!!.fallDistance <= 3f) return@EventHook
        mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket(true))
    })

}