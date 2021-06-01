package dev.toastmc.toastclient.impl.module.client

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.impl.gui.click.ClickGUIScreen

object ClickGUI : Module("ClickGUI", Category.CLIENT) {

    @JvmStatic
    val SCREEN = ClickGUIScreen()

    init {
        setKey(344, -1)
    }

    override fun onToggle() {
        if (isEnabled() && (mc.currentScreen == null || mc.currentScreen!! == SCREEN)) {
            if (mc.currentScreen != null && mc.currentScreen!! == SCREEN) {
                mc.openScreen(null)
            } else {
                mc.openScreen(SCREEN)
            }
            disable()
        }
    }
}