package dev.toastmc.toastclient.impl.module.client

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.impl.gui.hud.HUDEditorScreen
import org.lwjgl.glfw.GLFW

object HUDEditor : Module("HUDEditor", Category.CLIENT) {

    val SCREEN = HUDEditorScreen()

    init {
        setKey(GLFW.GLFW_KEY_LEFT, -1)
    }

    override fun onToggle() {
        if (isEnabled() && (
                    mc.currentScreen == null ||
                    mc.currentScreen!! == SCREEN ||
                    mc.currentScreen!! == ClickGUI.SCREEN)
        ) {
            if (mc.currentScreen != null && mc.currentScreen!! == SCREEN) {
                mc.setScreen(null)
            } else {
                mc.setScreen(SCREEN)
            }
            disable()
        }
    }

}