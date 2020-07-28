package dev.toastmc.client.module

import dev.toastmc.client.module.combat.AutoTotem
import net.minecraft.client.MinecraftClient
import java.io.Console
import java.rmi.registry.LocateRegistry.getRegistry
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class ModuleManager (){
    /**
     * Array containing the instances of all the modules
     */
    val modules: HashSet<Module> = HashSet<Module>()

    /**
     * Checks each module for a key-bind and toggles the module if the key-bind matches the key
     */
    fun onKey(key: Int, action: Int) {
        val iter = modules.iterator()
        while (iter.hasNext()) {
            val next = iter.next()
            if (next.key == key && action == -1 && MinecraftClient.getInstance().currentScreen == null) {
                if (MinecraftClient.getInstance().inGameHud.chatHud.isChatFocused) continue
                next.toggle()
            }
        }
    }

    init {
        modules.clear()
        register(AutoTotem())
    }

    private fun register(vararg modules: Module) {
        for (cheat in modules) {
            this.modules.add(cheat)
        }
    }


    fun <T : Module?> getModuleByClass(clazz: Class<T>): Module? {
        for (current in modules) {
            if (current.javaClass == clazz) return current
        }
        return null
    }

    fun getModuleByName(name: String?): Module? {
        for (current in modules) {
            if (current.alias?.contains(name)!!) return current
            if (current.label.equals(name, ignoreCase = true)) return current
        }
        return null
    }

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
    fun getModulesInArrayList(): String? {
        val sb: StringBuilder? = null
        var string: String = "Yeet"
        sb?.clear()
        for(module in modules){
            if(module.enabled!! && !module.hidden!! && !module.category?.equals(Category.NONE)!!){
                sb?.append("${module.label}\n ")
            }
        }
        string = sb.toString()
        val array = string.split(" ".toRegex()).toTypedArray()
        return array.toString()
    }
}