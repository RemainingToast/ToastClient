package dev.toastmc.toastclient.api.managers.module

import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.ToastClient
import dev.toastmc.toastclient.api.managers.SettingManager
import dev.toastmc.toastclient.api.setting.Setting
import dev.toastmc.toastclient.api.setting.Setting.*
import dev.toastmc.toastclient.api.setting.Setting.Boolean
import dev.toastmc.toastclient.api.setting.Setting.Number
import dev.toastmc.toastclient.api.util.ToastColor
import net.minecraft.client.util.InputUtil

open class Module(private var name: String, private var category: Category) : IToastClient {

    private var description: String = ""

    private var enabled = false

    private var drawn = true

    private var key = InputUtil.UNKNOWN_KEY

    protected open fun onEnable() {
        ToastClient.eventBus.register(this)
    }

    protected open fun onDisable() {
        ToastClient.eventBus.unregister(this)
    }

    protected open fun onToggle() {

    }

    open fun onUpdate() {

    }

    open fun onHUDRender() {

    }

    open fun isEnabled(): kotlin.Boolean {
        return enabled
    }

    open fun disable() {
        setEnabled(false)
    }

    open fun enable() {
        setEnabled(true)
    }

    open fun setEnabled(newVal: kotlin.Boolean) {
        if (newVal != this.enabled) {
            if (newVal) {
                onEnable()
            } else {
                onDisable()
            }
        }
        this.enabled = newVal
    }

    open fun isDrawn(): kotlin.Boolean {
        return drawn
    }

    open fun setDrawn(drawn: kotlin.Boolean) {
        this.drawn = drawn
    }

    open fun toggle() {
        setEnabled(!isEnabled())
        onToggle()
    }

    open fun toggleDrawn(){
        setDrawn(!isDrawn())
    }

    open fun getKey(): InputUtil.Key {
        return key
    }

    open fun setKey(key: InputUtil.Key) {
        this.key = key
    }

    open fun setKey(key: Int, scancode: Int) {
        this.key = InputUtil.fromKeyCode(key, scancode)
    }

    open fun getCategory(): Category {
        return category
    }

    open fun getName(): String {
        return name
    }

    open fun getDescription(): String {
        return description
    }

    open fun setDescription(description: String) {
        this.description = description
    }

    protected open fun group(name: String, vararg settings: Setting<*>): Group {
        return SettingManager.addSetting(Group(name, this, settings.asList())) as Group
    }

    protected open fun number(name: String, value: kotlin.Number, min: kotlin.Number, max: kotlin.Number): Number {
        return SettingManager.addSetting(Number(name, this, value.toDouble(), min.toDouble(), max.toDouble())) as Number
    }

    protected open fun bool(name: String, value: kotlin.Boolean): Boolean {
        return SettingManager.addSetting(Boolean(name, this, value)) as Boolean
    }

    protected open fun mode(name: String, value: String, vararg modes: String): Mode {
        return SettingManager.addSetting(Mode(name, this, value, modes.asList())) as Mode
    }

    protected open fun mode(name: String, value: String, modes: List<String>): Mode {
        return SettingManager.addSetting(Mode(name, this, value, modes)) as Mode
    }

    protected open fun color(name: String, color: ToastColor): ColorSetting {
        return SettingManager.addSetting(ColorSetting(name, this, false, color)) as ColorSetting
    }

    protected open fun color(name: String, color: ToastColor, rainbow: kotlin.Boolean): ColorSetting {
        return SettingManager.addSetting(ColorSetting(name, this, rainbow, color)) as ColorSetting
    }

    enum class Category {
        COMBAT,
        PLAYER,
        MOVEMENT,
        MISC,
        RENDER,
        CLIENT,
        NONE
    }
}