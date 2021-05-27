package dev.toastmc.toastclient.api.setting.types

/**
 * Interface representing a boolean value that can be toggled.
 * @author lukflug
 */
interface BooleanSetting {

    /**
     * Toggle the boolean value.
     */
    fun toggle()

    /**
     * Get the boolean value.
     * @return the value
     */
    fun enabled(): Boolean
}