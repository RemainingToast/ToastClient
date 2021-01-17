package me.remainingtoast.toastclient.api.setting.type

import com.lukflug.panelstudio.settings.ColorSetting
import me.remainingtoast.toastclient.api.setting.Setting
import me.remainingtoast.toastclient.api.setting.Type
import me.remainingtoast.toastclient.api.module.Module
import java.awt.Color


class ColorSetting(value: Color, private var rainbow: Boolean, name: String, description: String, module: Module) : Setting<Color>(
    value,
    name,
    description,
    module,
    Type.COLOR
), ColorSetting {

    var rainbowEnabled = false
    var alphaEnabled = false

    constructor(
        value: Color,
        rainbow: Boolean,
        name: String,
        rainbowEnabled: Boolean,
        alphaEnabled: Boolean,
        description: String,
        module: Module
    ) : this(value, rainbow, name, description, module) {
        this.rainbow = rainbow
        this.rainbowEnabled = rainbowEnabled
        this.alphaEnabled = alphaEnabled
    }

    override fun getValue(): Color {
        return if (rainbow) Color.getHSBColor(
            System.currentTimeMillis() % (360 * 32) / (360f * 32),
            1F,
            1F
        ) else super.value
    }

    override fun setValue(color: Color) {
        value = color
    }

    override fun getColor(): Color {
        return super.value
    }

    override fun getRainbow(): Boolean {
        return rainbow
    }

    override fun setRainbow(value: Boolean) {
        rainbow = value
    }
}