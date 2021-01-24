package me.remainingtoast.toastclient.api.setting.type

import com.lukflug.panelstudio.settings.EnumSetting
import me.remainingtoast.toastclient.api.setting.Setting
import me.remainingtoast.toastclient.api.setting.Type
import me.remainingtoast.toastclient.api.module.Module

class EnumSetting<T : Enum<T>>(value: T, name: String, description: String, module: Module) :
    Setting<T>(value, name, description, module, Type.ENUM), EnumSetting {
    override fun getValueName(): String {
        return value.toString()
    }

    override fun increment() {
        val array: Array<T> = value.declaringClass.enumConstants
        var index: Int = value.ordinal + 1
        if (index >= array.size) index = 0
        value = array[index]
    }
}