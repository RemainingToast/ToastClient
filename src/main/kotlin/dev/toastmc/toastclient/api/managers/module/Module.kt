package dev.toastmc.toastclient.api.managers.module

import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.ToastClient
import dev.toastmc.toastclient.api.setting.Setting
import dev.toastmc.toastclient.api.setting.Setting.*
import dev.toastmc.toastclient.api.setting.Setting.Boolean
import dev.toastmc.toastclient.api.setting.Setting.Double
import dev.toastmc.toastclient.api.setting.SettingManager
import dev.toastmc.toastclient.api.util.ToastColor
import net.minecraft.client.util.InputUtil

open class Module(private var name: String, private var category: Category) : IToastClient {

    private var description: String = ""

    private var enabled = false

    private var drawn = true

    private var key = InputUtil.UNKNOWN_KEY

    private var persistent = false

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

    open fun isPersistent(): kotlin.Boolean {
        return persistent
    }

    open fun setPersistent(persistent: kotlin.Boolean) {
        this.persistent = persistent
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
        val setting = Group(name, this, settings.asList())
        SettingManager.addSetting(setting)
        return setting
    }

    protected open fun number(name: String, value: kotlin.Double, min: kotlin.Double, max: kotlin.Double): Double {
        val setting = Double(name, this, value, min, max)
        SettingManager.addSetting(setting)
        return setting
    }

    protected open fun bool(name: String, value: kotlin.Boolean): Boolean {
        val setting = Boolean(name, this, value)
        SettingManager.addSetting(setting)
        return setting
    }

    protected open fun mode(name: String, value: String, vararg modes: String): Mode {
        val setting = Mode(name, this, value, modes.asList())
        SettingManager.addSetting(setting)
        return setting
    }

    protected open fun mode(name: String, value: String, modes: List<String>): Mode {
        val setting = Mode(name, this, value, modes)
        SettingManager.addSetting(setting)
        return setting
    }

    protected open fun color(name: String, color: ToastColor): ColorSetting {
        val setting = ColorSetting(name, this, false, color)
        SettingManager.addSetting(setting)
        return setting
    }

    protected open fun color(name: String, color: ToastColor, rainbow: kotlin.Boolean): ColorSetting {
        val setting = ColorSetting(name, this, rainbow, color)
        SettingManager.addSetting(setting)
        return setting
    }

    enum class Category {
        COMBAT, PLAYER, MOVEMENT, MISC, RENDER, CLIENT, HUD, NONE
    }
}