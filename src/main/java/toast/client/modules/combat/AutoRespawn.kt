package toast.client.modules.combat

import com.google.common.eventbus.Subscribe
import net.minecraft.client.gui.screen.DeathScreen
import toast.client.events.network.EventSyncedUpdate
import toast.client.modules.Module
import toast.client.utils.TimerUtil

/**
 * Module to automatically press the respawn button when the player dies
 */
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

    @Subscribe
    fun onEvent(event: EventSyncedUpdate?) {
        if (timer.isDelayComplete((getDouble("Speed").times(1000L)))) {
            timer.reset()
            if (mc.currentScreen ?: return is DeathScreen) {
                (mc.player ?: return).requestRespawn()
                mc.openScreen(null)
            }
        }
    }

    init {
        settings.addSlider("Speed", 1.0, 2.0, 20.0)
    }
}