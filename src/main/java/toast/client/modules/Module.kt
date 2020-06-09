package toast.client.modules

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import toast.client.ToastClient
import toast.client.modules.config.ModuleSettings

@Environment(EnvType.CLIENT)
open class Module(var name: String, var description: String, var category: Category, var key: Int) {
    var modIsEnabled = false
    var settings = ModuleSettings()
    var mc: MinecraftClient = MinecraftClient.getInstance()

    fun isEnabled(): Boolean = modIsEnabled

    fun setEnabled(newEnabled: Boolean) {
        modIsEnabled = newEnabled
        if (isEnabled()) {
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
        ToastClient.CONFIG_MANAGER.writeModules()
    }

    val mode: String?
        get() = settings.getMode("Mode")

    fun getDouble(name: String): Double = settings.getValue(name)!!

    fun getBool(name: String): Boolean = settings.getBoolean(name)

    fun disable() = setEnabled(false)

    fun enable() = setEnabled(true)

    fun toggle() = setEnabled(!isEnabled())

    open fun onEnable() {}
    open fun onDisable() {}

    enum class Category {
        PLAYER, MOVEMENT, RENDER, COMBAT, MISC
    }

    init {
        settings.addBoolean("Visible", true)
    }
}