package dev.toastmc.toastclient.api.managers

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.setting.Setting
import java.util.stream.Collectors

object SettingManager {

    @JvmStatic
    val settings: MutableList<Setting<*>> = ArrayList()

    fun addSetting(setting: Setting<*>): Setting<*> {
        setting.name.replace(" ", "")
        settings.add(setting)
        return setting
    }

    fun getSettingByNameAndMod(name: String, parent: Module): Setting<*> {
        return settings.stream().filter { s: Setting<*> -> s.parent == parent }.filter { s: Setting<*> -> s.name.equals(name, ignoreCase = true) }.findFirst().orElse(null)
    }

    fun getSettingsForMod(parent: Module): List<Setting<*>> {
        return settings.stream().filter { s: Setting<*>? -> s!!.parent == parent }.collect(Collectors.toList())
    }

    fun getSettingsByCategory(category: Module.Category): List<Setting<*>> {
        return settings.stream().filter { s: Setting<*> -> s.category == category }.collect(Collectors.toList())
    }

    fun getSettingByName(name: String): Setting<*> {
        return settings.stream().filter { s: Setting<*> -> s.name.equals(name, ignoreCase = true) }.findFirst().orElse(null)
    }

}