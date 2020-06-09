package toast.client.modules.config

import com.google.gson.annotations.SerializedName
import toast.client.ToastClient

class Setting {
    @SerializedName("Current Mode")
    private var mode: String? = null

    @SerializedName("Enabled")
    private var enabled: Boolean? = null

    @SerializedName("Current Value")
    private var value: Double? = null

    constructor(mode: String) {
        setMode(mode)
    }

    constructor(value: Double) {
        setValue(value)
    }

    constructor(enabled: Boolean) {
        setEnabled(enabled)
    }

    fun getMode(): String? {
        return mode
    }

    fun setMode(newMode: String) {
        mode = newMode
        value = null
        enabled = null
        ToastClient.CONFIG_MANAGER.writeConfig()
    }

    fun getValue(): Double = value!!

    fun setValue(newValue: Double) {
        mode = null
        value = newValue
        enabled = null
        ToastClient.CONFIG_MANAGER.writeConfig()
    }

    fun isEnabled(): Boolean = enabled!!

    fun setEnabled(enabled: Boolean) {
        mode = null
        value = null
        this.enabled = enabled
        ToastClient.CONFIG_MANAGER.writeConfig()
    }

    val type: Int
        get() = if (mode != null) 0 else if (value != null) 1 else if (enabled != null) 2 else 3
}