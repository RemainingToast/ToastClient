package me.remainingtoast.toastclient.api.setting.type

import com.lukflug.panelstudio.settings.Toggleable
import me.remainingtoast.toastclient.api.setting.Setting
import me.remainingtoast.toastclient.api.setting.Type
import me.remainingtoast.toastclient.api.module.Module

class BooleanSetting(name: String, description: String, module: Module, value: Boolean) : Setting<Boolean>(value, name, description, module, Type.BOOLEAN), Toggleable {
    override fun isOn(): Boolean {
        return value
    }

    override fun toggle() {
        value = !value
    }
}