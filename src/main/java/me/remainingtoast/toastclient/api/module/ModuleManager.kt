package me.remainingtoast.toastclient.api.module

import me.remainingtoast.toastclient.ToastClient
import me.remainingtoast.toastclient.client.module.combat.AutoTotem
import me.remainingtoast.toastclient.client.module.gui.ClickGUIModule
import me.remainingtoast.toastclient.client.module.gui.HUDEditor
import me.remainingtoast.toastclient.client.module.hud.ArrayListModule
import me.remainingtoast.toastclient.client.module.render.NoRender
import org.lwjgl.glfw.GLFW
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

class ModuleManager {

    var modules: MutableList<Module> = ArrayList()

    init {
        modules = ArrayList()
        register(ClickGUIModule(), NoRender(), ArrayListModule(), HUDEditor(), AutoTotem())
        println("MODULE MANAGER INITIALISED")
    }

    fun getModulesByCategory(category: Category): ArrayList<Module>? {
        return modules.stream().filter { module: Module -> module.getCategory() == category }
            .collect(Collectors.toCollection { ArrayList() })
    }

    fun getModuleByName(name: String): Module? {
        return modules.stream().filter { module: Module -> module.name == name }
            .findFirst().orElse(null)
    }

    fun <T : Module> getModuleByClass(clazz: Class<T>): Module? {
        for (current in modules) {
            if (current.javaClass == clazz) return current
            if (current::class.isInstance(clazz)) return current
        }
        return null
    }

    fun toggleModule(module: Module){
        module.toggle()
    }

    fun toggleBind(key: Int) {
        if (key == 0 || key == GLFW.GLFW_KEY_UNKNOWN) return
        modules.forEach(Consumer { module: Module ->
            if (module.getBind() == key) {
                module.toggle()
            }
        })
    }

    private fun register(vararg mods: Module) {
        for (cheat in mods) {
            modules.add(cheat)
        }
    }

    fun addModule(module: Module?) {
        if (!modules.contains(module)) {
            modules.add(module!!)
        }
    }

    fun removeModule(module: Module?) {
        modules.remove(module)
    }

    fun isModuleEnabled(module: Module): Boolean {
        return module.isEnabled()
    }

    fun onUpdate() {
        modules.stream().filter { obj: Module -> obj.isEnabled() }.forEach { obj: Module -> obj.onUpdate() }
    }

    fun onRender() {
        modules.stream().filter { obj: Module -> obj.isEnabled() }.forEach { obj: Module -> obj.onOverlayRender() }
        ToastClient.CLICKGUI.render()
    }

}