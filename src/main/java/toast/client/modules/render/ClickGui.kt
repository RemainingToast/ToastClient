package toast.client.modules.render

import com.google.common.eventbus.Subscribe
import org.lwjgl.glfw.GLFW
import toast.client.ToastClient
import toast.client.events.network.EventSyncedUpdate
import toast.client.gui.clickgui.ClickGuiScreen
import toast.client.modules.Module

class ClickGui : Module("ClickGui", "The gui for managing modules.", Category.RENDER, GLFW.GLFW_KEY_RIGHT_SHIFT) {
    override fun onEnable() {
        if (mc.player != null) {
            if (ToastClient.clickGui == null) {
                ToastClient.clickGuiHasOpened = false
                ToastClient.clickGui = ClickGuiScreen()
            }
            if (mc.currentScreen == null) {
                mc.openScreen(ToastClient.clickGui)
                ToastClient.clickGuiHasOpened = true
            }
        }
    }

    override fun onDisable() {
        if (mc.currentScreen is ClickGuiScreen && mc.player != null) {
            mc.openScreen(null)
        }
    }

    @Subscribe
    fun onUpdate(e: EventSyncedUpdate?) {
        ClickGuiScreen.descriptions = settings.getBoolean("Descriptions")
    }

    init {
        settings.addBoolean("Descriptions", true)
    }
}