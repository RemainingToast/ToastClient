package dev.toastmc.toastclient.impl.module.combat

import dev.toastmc.toastclient.api.managers.module.Module
import me.remainingtoast.toastclient.api.util.TimerUtil
import net.minecraft.client.gui.screen.DeathScreen

object AutoRespawn : Module("AutoRespawn", Category.COMBAT) {

    val timer = TimerUtil()
    var delay = number("Delay", 2.0, 0.1, 20.0)

    override fun onEnable() {
        timer.reset()
    }

    override fun onDisable() {
        timer.reset()
    }

    override fun onUpdate() {
        if(mc.currentScreen != null){
             if(timer.isDelayComplete(delay.value.toLong() * 1000L)){
                timer.setLastMS()
                if(mc.currentScreen is DeathScreen){
                    mc.player!!.requestRespawn()
                    mc.openScreen(null)
                }
            }
        }
    }
}