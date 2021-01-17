package me.remainingtoast.toastclient.client.module

import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.type.ColorSetting
import me.remainingtoast.toastclient.api.setting.type.IntegerSetting
import me.remainingtoast.toastclient.client.ToastGUI
import java.awt.Color


class ClickGUIModule(private var clickGui: ToastGUI) : Module("ClickGUI", Category.RENDER, Color.BLUE, 255) {

    companion object {
        lateinit var activeColor: ColorSetting
        lateinit var inactiveColor: ColorSetting
        lateinit var backgroundColor: ColorSetting
        lateinit var outlineColor: ColorSetting
        lateinit var fontColor: ColorSetting
        lateinit var opacity: IntegerSetting
        lateinit var animationSpeed: IntegerSetting
        lateinit var scrollSpeed: IntegerSetting
    }

    init {
        activeColor = registerColor("Active Color", "The main color.", true, false, Color.RED, false)
        inactiveColor = registerColor("Inactive Color", "The color for inactive modules.", true, false, Color.BLACK, false)
        backgroundColor = registerColor("Background Color", "The background color for settings.", true, false, Color(30, 30, 30), false)
        outlineColor = registerColor("Outline Color", "The color for panel outlines.", true, false, Color.RED, false)
        fontColor = registerColor("Font Color", "The main text color.", true, false, Color.WHITE, false)
        opacity = registerInteger("Opacity", "The GUI opacity", 150, 0, 255, true)
        animationSpeed = registerInteger("Animation Speed", "The speed for GUI animations.", 200, 0, 1000, true)
        scrollSpeed = registerInteger("Scroll Speed", "The speed for GUI scrolling", 10, 1, 20, true)
    }

    override fun onEnable() {
        if (mc.player != null) {
            if (mc.currentScreen == null) {
                mc.openScreen(clickGui)
            }
        }
    }

    override fun onDisable() {
        if (mc.currentScreen != null && mc.player != null) {
            mc.openScreen(null)
        }
    }
}
