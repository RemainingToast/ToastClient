package me.remainingtoast.toastclient.api.setting.type

import com.lukflug.panelstudio.settings.NumberSetting
import me.remainingtoast.toastclient.api.setting.Setting
import me.remainingtoast.toastclient.api.setting.Type
import me.remainingtoast.toastclient.api.module.Module
import kotlin.math.roundToInt

class IntegerSetting(
    value: Int,
    name: String,
    description: String,
    module: Module,
    val min: Int,
    val max: Int,
    val isLimited: Boolean
) :
    Setting<Int>(value, name, description, module, Type.INTEGER), NumberSetting {

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