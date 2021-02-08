package me.remainingtoast.toastclient.api.module

import com.lukflug.panelstudio.settings.Toggleable
import kotlinx.serialization.Serializable
import me.remainingtoast.toastclient.api.config.CustomSerializers
import me.remainingtoast.toastclient.api.setting.Setting
import me.remainingtoast.toastclient.api.setting.Setting.*
import java.awt.Color
import java.util.*

@Serializable
open class Module : com.lukflug.panelstudio.settings.KeybindSetting,Toggleable {

    var name: String = ""
    private var category: Category = Category.NONE
    @Serializable(with = CustomSerializers.ColorSerializer::class)
    private var color: Color = Color.BLACK
    var settings: ArrayList<Setting> = ArrayList()

    private var enabled = false
    private var drawn = false
    private var bind = 0

    private var alwaysEnabled = false

    constructor(name: String, category: Category) {
        this.name = name
        this.category = category
        enabled = false
        drawn = true
    }

    constructor(name: String, category: Category, alwaysEnabled: Boolean) {
        this.name = name
        this.category = category
        this.alwaysEnabled = alwaysEnabled
        enabled = false
        drawn = true
    }

    constructor(name: String, category: Category, alwaysEnabled: Boolean, drawnBoolean: Boolean) {
        this.name = name
        this.category = category
        this.alwaysEnabled = alwaysEnabled
        enabled = false
        drawn = drawnBoolean
    }

    constructor(name: String, category: Category, alwaysEnabled: Boolean, key: Int, drawnBoolean: Boolean) {
        this.name = name
        this.category = category
        this.alwaysEnabled = alwaysEnabled
        this.bind = key
        enabled = false
        drawn = drawnBoolean
    }

    constructor(name: String, category: Category, key: Int) {
        this.name = name
        this.category = category
        this.bind = key
        enabled = false
        drawn = true
    }

    open fun setColor(newColor: Color) {
        color = newColor
    }

    open fun getCategory(): Category {
        return this.category
    }

    open fun getColor(): Color {
        return color
    }

    open fun isEnabled(): Boolean {
        return enabled
    }

    open fun disable() {
        setEnabled(false)
    }

    open fun enable() {
        setEnabled(true)
    }

    open fun setEnabled(newVal: Boolean) {
        if (newVal != this.enabled) {
            if (newVal) {
                onEnable()
            } else {
                onDisable()
            }
        }
        this.enabled = newVal
    }

    open fun isDrawn(): Boolean {
        return drawn
    }

    open fun setDrawn(drawn: Boolean) {
        this.drawn = drawn
    }

    open fun getBind(): Int {
        return bind
    }

    open fun setBind(bind: Int) {
        this.bind = bind
    }

    override fun toggle() {
        setEnabled(!isEnabled())
    }

    open fun getHudInfo(): String {
        return ""
    }

    protected open fun onEnable() {}

    protected open fun onDisable() {}

    open fun onUpdate() {}

    open fun onOverlayRender() {}

    /** Setting registry functions below!  */
    protected open fun registerBoolean(name: String, description: String, value: Boolean): BooleanSetting {
        val setting = BooleanSetting(name, description, value)
        settings.add(setting)
        return setting
    }

    protected open fun registerBoolean(name: String, value: Boolean): BooleanSetting {
        val setting = BooleanSetting(name, "", value)
        settings.add(setting)
        return setting
    }

    protected open fun registerColor(
        name: String,
        description: String,
        value: Color,
        alphaEnabled: Boolean,
        rainbow: Boolean,
        rainbowEnabled: Boolean
    ): ColorSetting {
        val setting = ColorSetting(name, description, value, alphaEnabled, rainbow, rainbowEnabled)
        settings.add(setting)
        return setting
    }

    protected open fun registerDouble(
        name: String,
        description: String,
        value: Double,
        min: Double,
        max: Double,
        isLimited: Boolean
    ): DoubleSetting {
        val setting = DoubleSetting(name, description, value, min, max, isLimited)
        settings.add(setting)
        return setting
    }

    protected open fun registerList(name: String, description: String, list: List<String>, index: Int): ListSetting {
        val setting: ListSetting = ListSetting(name, description, list, index)
        settings.add(setting)
        return setting
    }

    protected open fun registerInteger(
        name: String,
        description: String,
        value: Int,
        min: Int,
        max: Int,
        isLimited: Boolean
    ): IntegerSetting {
        val setting = IntegerSetting(name, description, value, min, max, isLimited)
        settings.add(setting)
        return setting
    }

    protected open fun registerKeybind(name: String, description: String, value: Int): KeybindSetting {
        val setting: KeybindSetting = KeybindSetting(name, description, value)
        settings.add(setting)
        return setting
    }

    override fun getKey(): Int {
        return bind
    }

    override fun getKeyName(): String {
        return KeybindSetting.getKeyName(bind)
    }

    override fun setKey(key: Int) {
        bind = key
    }

    override fun isOn(): Boolean {
        return if(alwaysEnabled) true else enabled
    }
}