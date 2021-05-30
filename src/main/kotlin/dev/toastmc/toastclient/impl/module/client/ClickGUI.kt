package dev.toastmc.toastclient.impl.module.client

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.impl.clickgui.ClickGUIScreen

object ClickGUI : Module("ClickGUI", Category.CLIENT) {

    private val GUI = ClickGUIScreen()

//    var color = color("", ToastColor(1234, true))

    init {
        setKey(344, -1)
    }

    override fun onToggle() {
        if (isEnabled() && (mc.currentScreen == null || mc.currentScreen!! == GUI)) {
            if (mc.currentScreen != null && mc.currentScreen!! == GUI) {
                mc.openScreen(null)
            } else {
                mc.openScreen(GUI)
            }
            disable()
        }
    }

}