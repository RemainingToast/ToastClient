package dev.toastmc.toastclient.impl.module.client

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.impl.gui.click.ClickGUIScreen
import org.lwjgl.glfw.GLFW

object ClickGUI : Module("ClickGUI", Category.CLIENT) {

    val SCREEN = ClickGUIScreen()

    init {
        setKey(GLFW.GLFW_KEY_RIGHT_SHIFT, -1)
    }

    override fun onToggle() {
        if (isEnabled() && (
                    mc.currentScreen == null ||
                            mc.currentScreen!! == SCREEN ||
                            mc.currentScreen!! == HUDEditor.SCREEN)
        ) {
            if (mc.currentScreen != null && mc.currentScreen!! == SCREEN) {
                mc.openScreen(null)
            } else {
                mc.openScreen(SCREEN)
            }
            disable()
        }
    }
}