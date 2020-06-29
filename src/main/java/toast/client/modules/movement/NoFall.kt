package toast.client.modules.movement

import com.google.common.eventbus.Subscribe
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import toast.client.events.network.EventSyncedUpdate
import toast.client.modules.Module

class NoFall : Module("NoFall", "Stops the player form taking fall damage", Category.MOVEMENT, -1) {
    @Subscribe
    fun onUpdate(event: EventSyncedUpdate?) {
        if (mc.player == null) return
        if (mc.player!!.isFallFlying && mc.player!!.fallDistance <= 3f) return
        mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket(true))
    }
}