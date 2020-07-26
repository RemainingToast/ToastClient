package dev.toastmc.client.modules

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import dev.toastmc.client.ToastClient
import dev.toastmc.client.modules.config.ModuleSettings

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

    val mode: String? get() = settings.getMode("Mode")

    fun getDouble(name: String): Double = settings.getValue(name)!!

    fun getBool(name: String): Boolean = settings.getBoolean(name)

    fun disable(): Boolean = setEnabled(false)

    fun enable(): Boolean = setEnabled(true)

    fun toggle(): Boolean = setEnabled(!enabled)

    open fun onEnable() {}

    open fun onDisable() {}

    enum class Category {
        PLAYER,
        MOVEMENT,
        RENDER,
        COMBAT,
        MISC
    }

    init {
        settings.addBoolean("Visible", true)
    }
}