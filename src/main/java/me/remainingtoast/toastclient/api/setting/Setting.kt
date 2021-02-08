package me.remainingtoast.toastclient.api.setting

import com.lukflug.panelstudio.settings.NumberSetting
import com.lukflug.panelstudio.settings.Toggleable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.remainingtoast.toastclient.api.config.CustomSerializers
import net.minecraft.client.util.InputUtil
import net.minecraft.text.TranslatableText
import java.awt.Color
import kotlin.math.roundToInt

@Serializable
sealed class Setting {
    abstract val name: String
    abstract val description: String

    @Serializable
    @SerialName("boolean")
    data class BooleanSetting(
        override val name: String,
        override val description: String = "",
        var value: Boolean
    ) : Setting(), Toggleable {

        override fun isOn(): Boolean = value

        override fun toggle() {
            value = !value
        }
    }

    @Serializable
    @SerialName("key")
    data class KeybindSetting(
        override val name: String,
        override val description: String = "",
        var value: Int = 0
        ) : Setting(), com.lukflug.panelstudio.settings.KeybindSetting {

        val defaultKey = value

        override fun getKey(): Int {
            return value
        }

        override fun setKey(key: Int) {
            this.value = key
        }

        override fun getKeyName(): String {
            return getKeyName(value)
        }

        companion object {
            fun getKeyName(key: Int): String {
                val translationKey = InputUtil.Type.KEYSYM.createFromCode(key).translationKey
                val translation = TranslatableText(translationKey).string
                return if (translation != translationKey) translation else InputUtil.Type.KEYSYM.createFromCode(key).localizedText.string
            }
        }
    }

    @Serializable
    @SerialName("double")
    data class DoubleSetting(
        override val name: String,
        override val description: String = "",
        var value: Double,
        val min: Double,
        val max: Double,
        val isLimited: Boolean
    ) :
        Setting(), NumberSetting {

        override fun getMaximumValue(): Double {
            return max
        }

        override fun getMinimumValue(): Double {
            return min
        }

        override fun getNumber(): Double {
            return value
        }

        override fun getPrecision(): Int {
            return 2
        }

        override fun setNumber(d: Double) {
            value = d
        }
    }

    @Serializable
    @SerialName("int")
    data class IntegerSetting(
        override val name: String,
        override val description: String = "",
        var value: Int,
        val min: Int,
        val max: Int,
        val isLimited: Boolean
    ) :
        Setting(), NumberSetting {

        override fun getMaximumValue(): Double {
            return max.toDouble()
        }

        override fun getMinimumValue(): Double {
            return min.toDouble()
        }

        override fun getNumber(): Double {
            return value.toDouble()
        }

        override fun getPrecision(): Int {
            return 0
        }

        override fun setNumber(d: Double) {
            value = d.roundToInt()
        }
    }

    @Serializable
    @SerialName("list")
    data class ListSetting(
        override val name: String,
        override val description: String = "",
        val list: List<String>,
        var index: Int,
    ) :
        Setting(), com.lukflug.panelstudio.settings.EnumSetting {

        private var value: String = list[index]

        fun getValue() = value

        override fun getValueName(): String {
            return if (list.size > index) list[index] else ""
        }

        override fun increment() {
            index = (index + 1) % list.size
            value = list[index]
        }
    }

    @Serializable
    @SerialName("color")
    data class ColorSetting(
        override val name: String,
        override val description: String = "",
        @Serializable(with = CustomSerializers.ColorSerializer::class)
        private var color: Color,
        private var rainbow: Boolean
        ) :
        Setting(), com.lukflug.panelstudio.settings.ColorSetting {

        var rainbowEnabled = false
        var alphaEnabled = false

        constructor(
            name: String,
            description: String,
            color: Color,
            alphaEnabled: Boolean,
            rainbow: Boolean,
            rainbowEnabled: Boolean,
        ) : this(name, description, color, rainbow) {
            this.rainbow = rainbow
            this.rainbowEnabled = rainbowEnabled
            this.alphaEnabled = alphaEnabled
        }

        override fun getValue(): Color {
            return if (rainbow) Color.getHSBColor(
                System.currentTimeMillis() % (360 * 32) / (360f * 32),
                1F,
                1F
            ) else color
        }

        override fun setValue(color: Color) {
            this.color = color
        }

        override fun getColor(): Color {
            return color
        }

        override fun getRainbow(): Boolean {
            return rainbow
        }

        override fun setRainbow(value: Boolean) {
            rainbow = value
        }

        fun toInteger(): Int {
            return value.rgb and 0xFFFFFF + (if (rainbow) 1 else 0) * 0x1000000
        }

        fun fromInteger(number: Int) {
            value = Color(number and 0xFFFFFF)
            rainbow = number and 0x1000000 != 0
        }
    }

}