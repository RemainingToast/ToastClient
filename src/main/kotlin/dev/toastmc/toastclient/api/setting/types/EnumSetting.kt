package dev.toastmc.toastclient.api.setting.types

/**
 * A setting representing an enumeration.
 * @author lukflug
 */
interface EnumSetting {

    /**
     * Cycle through the values of the enumeration.
     */
    fun increment()

    /**
     * Get the current value.
     * @return the name of the current enum value
     */
    fun getValueName(): String

}