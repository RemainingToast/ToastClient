package me.remainingtoast.toastclient.client.module.client

import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.type.ColorSetting
import me.remainingtoast.toastclient.api.setting.type.IntegerSetting
import java.awt.Color

object Colors : Module("Colors", Category.CLIENT, true) {

    var activeColor: ColorSetting = registerColor("Active Color", "The main color.", true, true, Color.GREEN, true)
    var inactiveColor: ColorSetting = registerColor("Inactive Color", "The color for inactive modules.", true, true, Color(28,28,28), false)
    var backgroundColor: ColorSetting = registerColor("Background Color", "The background color for settings.", true, true, Color.BLACK, false)
    var categoryBgColor: ColorSetting = registerColor("Category Background Color", "Category title background color", true, true, Color(0,200,0), false)
    var outlineColor: ColorSetting = registerColor("Outline Color", "The color for panel outlines.", true, true, Color(20,20,20), false)
    var fontColor: ColorSetting = registerColor("Font Color", "The main text color.", true, true, Color.WHITE, false)
    var opacity: IntegerSetting = registerInteger("Opacity", "The GUI opacity", 201, 0, 255, true)

}