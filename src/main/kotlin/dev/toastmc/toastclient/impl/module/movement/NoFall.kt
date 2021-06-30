package dev.toastmc.toastclient.impl.module.movement

import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

object NoFall : Module("NoFall", Category.MOVEMENT) {

    override fun onUpdate() {
        if (mc.player!!.isFallFlying && mc.player!!.fallDistance <= 3f) return
        mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket(true))
    }

}