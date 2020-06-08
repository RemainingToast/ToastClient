package toast.client.modules.combat

import net.minecraft.client.gui.screen.DeathScreen
import toast.client.event.EventImpl
import toast.client.event.events.player.EventUpdate
import toast.client.modules.Module
import toast.client.utils.TimerUtil

class AutoRespawn : Module(
    "AutoRespawn",
    "Automatically presses the respawn button for you.",
    Category.COMBAT,
    -1
) {
    private val timer = TimerUtil()
    override fun onEnable() {
        timer.reset()
    }

    override fun onDisable() {
        timer.reset()
    }

    @EventImpl
    fun onEvent(event: EventUpdate?) {
        if (mc.currentScreen != null) {
            if (timer.isDelayComplete((getDouble("Speed") * 1000L).toLong())) {
                timer.setLastMS()
                if (mc.currentScreen is DeathScreen) {
                    mc.player!!.requestRespawn()
                    mc.openScreen(null)
                }
            }
        }
    }

    init {
        settings.addSlider("Speed", 1.0, 2.0, 20.0)
    }
}