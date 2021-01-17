package me.remainingtoast.toastclient.api.module

import me.remainingtoast.toastclient.client.module.ClickGUIModule
import java.util.*
import java.util.stream.Collectors


class ModuleManager {

    var modules: MutableList<Module> = ArrayList()

    init {
        modules = ArrayList()
        register(ClickGUIModule())
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
            if (current::class.isInstance(clazz)) return current as T
        }
        return null
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

}