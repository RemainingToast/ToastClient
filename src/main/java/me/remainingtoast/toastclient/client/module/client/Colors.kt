package me.remainingtoast.toastclient.client.module.client

import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.Setting.ColorSetting
import me.remainingtoast.toastclient.api.setting.Setting.IntegerSetting
import java.awt.Color

object Colors : Module("Colors", Category.CLIENT, true) {

    var activeColor: ColorSetting = registerColor("Active Color", "The main color.", Color.GREEN, true, true, true)
    var inactiveColor: ColorSetting = registerColor(
        "Inactive Color",
        "The color for inactive modules.",
        Color(28,28,28),
        true,
        false,
        true
    )
    var backgroundColor: ColorSetting = registerColor(
        "Background Color",
        "The background color for settings.",
        Color.BLACK,
        true,
        false,
        true
    )
    var categoryBgColor: ColorSetting = registerColor(
        "Category Background Color",
        "Category title background color",
        Color(0,200,0),
        true,
        false,
        true
    )
    var outlineColor: ColorSetting = registerColor(
        "Outline Color",
        "The color for panel outlines.",
        Color(20,20,20),
        true,
        false,
        true
    )
    var fontColor: ColorSetting = registerColor("Font Color", "The main text color.", Color.WHITE, true, false, true)
    var opacity: IntegerSetting = registerInteger("Opacity", "The GUI opacity", 201, 0, 255, true)

}