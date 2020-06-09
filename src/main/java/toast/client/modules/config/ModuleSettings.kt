package toast.client.modules.config

import toast.client.ToastClient
import java.util.*

class ModuleSettings {
    private var settings: MutableMap<String, Setting> = TreeMap()
    private var settingsDef: MutableMap<String, SettingDef> = TreeMap()
    fun addBoolean(name: String, defaultValue: Boolean) {
        settings[name] = Setting(defaultValue)
        settingsDef[name] = SettingDef()
    }

    fun addMode(name: String, defaultMode: String, vararg modes: String) {
        settings[name] = Setting(defaultMode)
        val modeList = ArrayList(listOf(*modes))
        settingsDef[name] = SettingDef(modeList)
    }

    fun addSlider(name: String, minimumValue: Double, defaultValue: Double, maximumValue: Double) {
        settings[name] = Setting(defaultValue)
        settingsDef[name] = SettingDef(minimumValue, maximumValue)
    }

    fun getSetting(name: String): Setting? = settings[name]

    fun getSettingDef(name: String): SettingDef? = settingsDef[name]

    fun getBoolean(name: String?): Boolean = settings[name]!!.isEnabled()

    fun getMode(name: String): String? = settings[name]!!.getMode()

    fun getValue(name: String?): Double? = settings[name]!!.getValue()

    fun getModes(name: String): ArrayList<String>? = settingsDef[name]!!.modes

    fun getMax(name: String): Double? = settingsDef[name]!!.maxValue

    fun getMin(name: String): Double? = settingsDef[name]!!.minValue

    fun getSettings(): MutableMap<String, Setting> = settings
}