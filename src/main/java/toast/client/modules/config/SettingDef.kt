package toast.client.modules.config

import java.util.*

class SettingDef {
    val modes: ArrayList<String>?
    private val isBool: Boolean
    val maxValue: Double?
    val minValue: Double?

    constructor(modes: ArrayList<String>) {
        this.modes = modes
        isBool = false
        minValue = null
        maxValue = minValue
    }

    constructor(minvalue: Double, maxvalue: Double) {
        modes = null
        isBool = false
        minValue = minvalue
        maxValue = maxvalue
    }

    constructor() {
        modes = null
        isBool = true
        minValue = null
        maxValue = minValue
    }

    val type: String
        get() = (if (modes != null) "mode" else if (minValue != null && maxValue != null) "value" else if (isBool) "boolean" else null) as String
}