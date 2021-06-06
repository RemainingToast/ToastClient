package dev.toastmc.toastclient.impl.module.client

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.ToastColor
import dev.toastmc.toastclient.impl.gui.click.ClickGUIScreen
import org.lwjgl.glfw.GLFW
import java.awt.Color

object ClickGUI : Module("ClickGUI", Category.CLIENT) {

    val SCREEN = ClickGUIScreen()

    private val HUE = number("Hue", 360, 0, 360, 0.1)
    private val SATURATION = number("Saturation", 100, 0, 100, 0.1)
    private val BRIGHTNESS = number("Brightness",44, 0, 100, 0.1)

    val COLOR: ToastColor
        get() {
            return ToastColor.fromHSB(HUE.floatValue, SATURATION.floatValue, BRIGHTNESS.floatValue, 255)
        }

    val COLOR_HOVER: ToastColor
        get() {
            return ToastColor(COLOR.darker())
        }

    var BACKGROUND_COLOR: ToastColor = ToastColor(-0x80000000, true)
    var HOVER_COLOR: ToastColor = ToastColor(-0x64c0c0c0, true)
    var INACTIVE_COLOR: ToastColor = ToastColor(-0x7fdbdbdb, true)
    var FONT_COLOR: ToastColor = ToastColor(Color.WHITE)

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