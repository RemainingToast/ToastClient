package me.remainingtoast.toastclient.api.setting

import java.util.*
import java.util.stream.Collectors
import me.remainingtoast.toastclient.api.module.Module


object SettingManager {
    var settings: ArrayList<Setting<*>> = ArrayList()

    init {
        println("SETTINGS MANAGER INITIALISED")
    }

    fun getSettingsForModule(module: Module): ArrayList<Setting<*>> {
        return settings.stream().filter {
            it!!.module.javaClass.isAssignableFrom(
                module.javaClass
            )
        }.collect(Collectors.toCollection { ArrayList() })
    }

    fun addSetting(setting: Setting<*>) {
        if (!settings.contains(setting)) {
            settings.add(setting)
        }
    }

    fun removeSetting(setting: Setting<*>) {
        settings.remove(setting)
    }

    fun getSettingByName(name: String): Setting<*> {
        return settings.stream().filter { setting: Setting<*>? -> setting!!.name == name }
            .findFirst().orElse(null)
    }

    fun getSettingByConfigName(configName: String): Setting<*> {
        return settings.stream().filter { setting: Setting<*>? -> setting!!.configName == configName }
            .findFirst().orElse(null)
    }

    fun getSettingByType(type: Type): ArrayList<Setting<*>> {
        return settings.stream().filter { setting: Setting<*> -> setting.getType() === type }
            .collect(Collectors.toCollection { ArrayList() })
    }
}