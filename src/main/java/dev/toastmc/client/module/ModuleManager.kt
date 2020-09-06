package dev.toastmc.client.module

import dev.toastmc.client.module.combat.AutoArmour
import dev.toastmc.client.module.combat.AutoCrystal
import dev.toastmc.client.module.combat.AutoTotem
import dev.toastmc.client.module.misc.PortalChat
import dev.toastmc.client.module.movement.AutoWalk
import dev.toastmc.client.module.movement.Flight
import dev.toastmc.client.module.movement.Jesus
import dev.toastmc.client.module.movement.Sprint
import dev.toastmc.client.module.player.AutoTool
import dev.toastmc.client.module.player.NoFall
import dev.toastmc.client.module.player.SafeWalk
import dev.toastmc.client.module.player.Velocity
import dev.toastmc.client.module.render.ESP
import dev.toastmc.client.module.render.FullBright
import dev.toastmc.client.module.render.HUD
import dev.toastmc.client.module.render.NoRender
import kotlin.reflect.KClass


class ModuleManager {
    /**
     * Array containing the instances of all the modules
     */
    val modules: HashSet<Module> = HashSet()

    /**
     * Checks each module for a key-bind and toggles the module if the key-bind matches the key
     */
    //TODO: fun onKey
//    fun onKey(key: Int, action: Int) {
//        val iter = modules.iterator()
//        while (iter.hasNext()) {
//            val next = iter.next()
//            if (next.key == key && action == -1 && MinecraftClient.getInstance().currentScreen == null) {
//                if (MinecraftClient.getInstance().inGameHud.chatHud.isChatFocused) continue
//                next.toggle()
//            }
//        }
//    }

    /**
     * Register modules to function with commands and gui etc
     */
    //TOOD: When we have config file enable modules here

    init {
        modules.clear()
        register(
            AutoArmour(),
            AutoTotem(),
            AutoTool(),
            AutoWalk(),
            ESP(),
            Flight(),
            FullBright(),
            NoFall(),
            NoRender(),
            PortalChat(),
            SafeWalk(),
            Sprint(),
            Velocity(),
            HUD(),
            AutoCrystal(),
            Jesus()
        )
    }

    private fun register(vararg modules: Module) {
        for (cheat in modules) {
            this.modules.add(cheat)
        }
    }

    fun <T : Module> getModuleByClass(clazz: KClass<T>): Module? {
        return getModuleByClass(clazz.java)
    }

    fun <T : Module> getModuleByClass(clazz: Class<T>): Module? {
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
        val string: String
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