package dev.toastmc.toastclient.impl.module.client

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.ToastColor
import dev.toastmc.toastclient.impl.clickgui.ClickGUIScreen

object ClickGUI : Module("ClickGUI", Category.CLIENT) {

    private var hue = number("Hue", 100, 0, 360)
    private var saturation = number("Saturation",100, 0, 100)
    private var brightness = number("Brightness",100, 0, 100)

    private val GUI = ClickGUIScreen()

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
        } else {
//            GUI.onClose()
        }
    }

    fun getMainColor(): ToastColor {
        return ToastColor.fromHSB(hue.floatValue, saturation.floatValue, brightness.floatValue)
    }

    fun getModuleColor(): ToastColor {
        return ToastColor(0x50000000, true)
    }

    fun getAccentColor(): ToastColor {
        return ToastColor(50, 50, 50, 200)
    }

}