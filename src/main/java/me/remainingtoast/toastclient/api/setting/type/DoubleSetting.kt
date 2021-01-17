package me.remainingtoast.toastclient.api.setting.type

import com.lukflug.panelstudio.settings.NumberSetting
import me.remainingtoast.toastclient.api.setting.Setting
import me.remainingtoast.toastclient.api.setting.Type
import me.remainingtoast.toastclient.api.module.Module

class DoubleSetting(
    value: Double,
    name: String,
    description: String,
    module: Module,
    val min: Double,
    val max: Double,
    val isLimited: Boolean
) :
    Setting<Double?>(value, name, description, module, Type.DOUBLE), NumberSetting {

    override fun getMaximumValue(): Double {
        return max
    }

    override fun getMinimumValue(): Double {
        return min
    }

    override fun getNumber(): Double {
        return value ?: 0.0
    }

    override fun getPrecision(): Int {
        return 2
    }

    override fun setNumber(d: Double) {
        value = d
    }
}