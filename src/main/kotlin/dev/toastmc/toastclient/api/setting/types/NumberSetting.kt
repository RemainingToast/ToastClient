package dev.toastmc.toastclient.api.setting.types

/**
 * Setting representing an adjustable number.
 * @author lukflug
 */
interface NumberSetting {

    /**
     * Get the number as double.
     * @return the current setting
     */
    fun getNumber(): Double

    /**
     * Set the number.
     * @param value the new number
     */
    fun setNumber(value: Double)

    /**
     * Get the maximum allowed value for the setting.
     * @return maximum value
     */
    fun getMaximumValue(): Double

    /**
     * Get the minimum allowed value for the setting.
     * @return minimum value
     */
    fun getMinimumValue(): Double

    /**
     * Get the setting's precision.
     * @return decimal precision
     */
    fun getPrecision(): Int

}