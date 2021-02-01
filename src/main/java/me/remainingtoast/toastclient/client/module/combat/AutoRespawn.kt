package me.remainingtoast.toastclient.client.module.combat

import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.type.DoubleSetting
import me.remainingtoast.toastclient.api.util.TimerUtil
import me.remainingtoast.toastclient.api.util.mc
import net.minecraft.client.gui.screen.DeathScreen

object AutoRespawn : Module("AutoRespawn", Category.COMBAT) {

    val timer = TimerUtil()
    var delay: DoubleSetting = registerDouble("Delay", "", 2.0, 0.1, 20.0, true)

    override fun onEnable() {
        timer.reset()
    }

    override fun onDisable() {
        timer.reset()
    }

    override fun onUpdate() {
        if(mc.currentScreen != null){
            println("Current screen isnt null")
            if(timer.isDelayComplete(delay.value!!.toLong() * 1000L)){
                println("Delay complete")
                timer.setLastMS()
                if(mc.currentScreen is DeathScreen){
                    println("Respawn cock head")
                    mc.player!!.requestRespawn()
                    mc.openScreen(null)
                }
            }
        }
    }
}