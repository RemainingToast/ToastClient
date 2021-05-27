package dev.toastmc.toastclient.api.setting.types

import java.awt.Color

/**
 * Setting representing a color.
 * @author lukflug
 */
interface ColorSetting {

    /**
     * Get the current value for the color setting.
     * @return the current color
     */
    fun getValue(): Color

    /**
     * Set the non-rainbow color.
     * @param value the value
     */
    fun setValue(value: Color)

    /**
     * Get the color, ignoring the rainbow.
     * @return the color ignoring the rainbow
     */
    fun getColor(): Color

    /**
     * Check if rainbow is enabled.
     * @return set, if the rainbow is enabled
     */
    fun getRainbow(): Boolean

    /**
     * Enable or disable the rainbow.
     * @param rainbow set, if rainbow should be enabled
     */
    fun setRainbow(rainbow: Boolean)

}