package toast.client.modules.movement

import com.google.common.eventbus.Subscribe
import org.lwjgl.glfw.GLFW
import toast.client.events.network.EventSyncedUpdate
import toast.client.modules.Module

class Flight : Module("Fly", "Lets you fly.", Category.MOVEMENT, GLFW.GLFW_KEY_G) {
    @Subscribe
    fun onUpdate(event: EventSyncedUpdate?) {
        if (mc.player == null) return
        if ((mc.player!!.onGround || mc.player!!.fallDistance <= 0) && this.enabled) {
            mc.player!!.abilities.allowFlying = true
            mc.player!!.abilities.flying = true
            mc.player!!.abilities.flySpeed = (0.05f * getDouble("Speed")).toFloat()
        }
    }

    override fun onEnable() {
        if (mc.player == null) return
        mc.player!!.abilities.allowFlying = true
        mc.player!!.abilities.flying = true
        mc.player!!.abilities.flySpeed = (0.05f * getDouble("Speed")).toFloat()
    }

    override fun onDisable() {
        if (mc.player == null) return
        if (!mc.player!!.abilities.creativeMode) {
            mc.player!!.abilities.allowFlying = false
        }
        if (mc.player!!.isSpectator) {
            mc.player!!.abilities.allowFlying = true
            mc.player!!.abilities.flying = true
            mc.player!!.abilities.flySpeed = 0.05f
        } else {
            mc.player!!.abilities.flying = false
            mc.player!!.abilities.flySpeed = 0.05f
        }
    }

    init {
        settings.addMode("Mode", "Vanilla", "Vanilla")
        settings.addSlider("Speed", 0.0, 2.0, 10.0)
    }
}