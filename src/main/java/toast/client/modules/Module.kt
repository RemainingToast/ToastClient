package toast.client.modules

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import toast.client.ToastClient
import toast.client.modules.config.ModuleSettings

/**
 * Superclass for creating modules
 */
@Environment(EnvType.CLIENT)
open class Module(
    /**
     * The name of the module
     */
    var name: String,

    /**
     * A brief description of what the module does
     */
    var description: String,

    /**
     * The category the module should appear in
     */
    var category: Category,

    /**
     * What key-bind the module should have by default
     */
    var key: Int
) {
    /**
     *
     */

    /**
     * Variable containing the module's configuration options
     */
    var settings: ModuleSettings = ModuleSettings()

    /**
     * Shorthand definition for minecraft instance
     */
    var mc: MinecraftClient = MinecraftClient.getInstance()

    /**
     * Whether or not the module is currently enabled
     */
    var enabled: Boolean = false

    /**
     * Enables or disables a module
     */
    private fun setEnabled(newEnabled: Boolean): Boolean {
        enabled = newEnabled
        if (enabled) {
            try {
                ToastClient.eventBus.register(this@Module)
            } catch (ignored: IllegalArgumentException) {
            }
            onEnable()
        } else {
            try {
                ToastClient.eventBus.unregister(this@Module)
            } catch (ignored: IllegalArgumentException) {
            }
            onDisable()
        }
        return enabled
    }

    /**
     * The current mode of the configuration option called "Mode"
     */
    val mode: String?
        get() = settings.getMode("Mode")

    /**
     * Gets the current value of a configuration option of type Double
     */
    fun getDouble(name: String): Double = settings.getValue(name)!!

    /**
     * Gets the current value of a configuration option of type Boolean
     */
    fun getBool(name: String): Boolean = settings.getBoolean(name)

    /**
     * Disables the module
     */
    fun disable(): Boolean = setEnabled(false)

    /**
     * Enables the module
     */
    fun enable(): Boolean = setEnabled(true)

    /**
     * Toggles the module's enabled state
     */
    fun toggle(): Boolean = setEnabled(!enabled)

    /**
     * Function that gets called whenever the module is enabled
     */
    open fun onEnable() {}

    /**
     * Function that gets called whenever the module is disabled
     */
    open fun onDisable() {}

    /**
     * Enum containing the categories available for modules
     */
    enum class Category {
        /**
         * Modules that aid the player in a none movement or combat related way
         */
        PLAYER,

        /**
         * Modules that help the player move around better
         */
        MOVEMENT,

        /**
         * Modules that affect or add to the game's rendering
         */
        RENDER,

        /**
         * Modules that aid the player in combat
         */
        COMBAT,

        /**
         * Modules that don't fit into any of the other categories
         */
        MISC
    }

    init {
        settings.addBoolean("Visible", true)
    }
}