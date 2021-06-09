package dev.toastmc.toastclient.impl.module.movement

import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket

object Sprint : Module("Sprint", Category.MOVEMENT) {

    val mode = mode("Mode", "Rage", "Rage", "Legit")

    override fun onUpdate() {
        if (mc.player == null) return
        if (mode.value.equals("Rage")) {
            mc.player!!.networkHandler.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING))
        } else {
            mc.player!!.isSprinting = mc.player!!.input.movementForward > 0 &&
                    mc.player!!.input.movementSideways != 0f ||
                    mc.player!!.input.movementForward > 0 && !mc.player!!.isSneaking
        }
    }

}