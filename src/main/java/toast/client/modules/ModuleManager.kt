package toast.client.modules

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import org.reflections.Reflections
import toast.client.ToastClient
import toast.client.modules.misc.Panic
import toast.client.modules.misc.Panic.Companion.IsPanicking
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList


/**
 * Manages the loading and accessing of module instances
 */
@Environment(EnvType.CLIENT)
class ModuleManager {
    /**
     * Array containing the instances of all the modules
     */
    val modules: CopyOnWriteArrayList<Module> = CopyOnWriteArrayList<Module>()

    /**
     * Checks each module for a key-bind and toggles the module if the key-bind matches the key
     */
    fun onKey(key: Int, action: Int) {
        modules.forEach { module ->
            if (module.key == key && action == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen == null) {
                if (MinecraftClient.getInstance().inGameHud.chatHud.isChatFocused) return@forEach
                if (module.javaClass != Panic::class.java && IsPanicking()) return
                module.toggle()
            }
        }
    }

    /**
     * Gets a module from it's class
     */
    fun getModule(moduleClass: Class<out Module?>): Module? = modules.firstOrNull { it.javaClass == moduleClass }

    /**
     * Gets a module from it's name
     */
    fun getModule(name: String): Module? = modules.firstOrNull { it.name == name }

    /**
     * Gets an array of the modules in a category
     */
    fun getModulesInCategory(category: Module.Category): List<Module> {
        val moduleList: MutableList<Module> = ArrayList()
        modules.forEach { module ->
            if (module.category === category) {
                moduleList.add(module)
            }
        }
        return moduleList
    }

    /**
     * Loads and initializes all of the modules
     */
    fun loadModules() {
        modules.clear()
        val reflections = Reflections("toast.client.modules")
        val moduleClasses =
                reflections.getSubTypesOf(
                        Module::class.java
                )
        for (moduleClass in moduleClasses) {
            val module = moduleClass.getConstructor().newInstance()
            modules.add(module)
        }
        ToastClient.CONFIG_MANAGER.enableWrite()
        ToastClient.CONFIG_MANAGER.loadConfig()
        ToastClient.CONFIG_MANAGER.loadKeyBinds()
        ToastClient.CONFIG_MANAGER.loadModules()
        ToastClient.CONFIG_MANAGER.loadMacros()
    }
}