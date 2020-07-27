package dev.toastmc.client.module

import net.minecraft.client.MinecraftClient
import java.util.concurrent.CopyOnWriteArrayList
import org.lwjgl.glfw.GLFW

class ModuleManager (){
    /**
     * Array containing the instances of all the modules
     */
    val modules: CopyOnWriteArrayList<Module> = CopyOnWriteArrayList<Module>()

    /**
     * Checks each module for a key-bind and toggles the module if the key-bind matches the key
     */
    fun onKey(key: Int, action: Int) {
        val iter = modules.iterator()
        while (iter.hasNext()) {
            val next = iter.next()
            if (next.key == key && action == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen == null) {
                if (MinecraftClient.getInstance().inGameHud.chatHud.isChatFocused) continue
                next.toggle()
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
    fun getModule(name: String): Module? = modules.firstOrNull { it.label == name }

    /**
     * Gets an array of the modules in a category
     */
    fun getModulesInCategory(category: Category): List<Module> {
        val moduleList: MutableList<Module> = ArrayList()
        val iter = modules.iterator()
        while (iter.hasNext()) {
            val next = iter.next()
            if (next.category === category) {
                moduleList.add(next)
            }
        }
        return moduleList
    }
}