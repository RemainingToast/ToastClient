package dev.toastmc.client.module

import dev.toastmc.client.module.combat.*
import dev.toastmc.client.module.misc.Discord
import dev.toastmc.client.module.misc.PortalChat
import dev.toastmc.client.module.misc.SignCopy
import dev.toastmc.client.module.movement.*
import dev.toastmc.client.module.player.*
import dev.toastmc.client.module.render.*

class ModuleManager {

    /**
     * Array containing the instances of all the modules
     */
    val modules: MutableList<Module> = ArrayList()
    var clickguiHasOpened: Boolean = false

    fun initMods() {
        modules.clear()
        register(AutoArmour(), AntiHunger(), AutoReplenish(), AutoTotem(),
            AutoTool(), AutoWalk(), BedAura(), ClickGUI(), CrystalAura(),
            ESP(), FastStop(), Flight(), FullBright(), HUD(), Jesus(),
            KillAura(), NoFall(), NoFog(), NoRender(), PortalChat(), SafeWalk(),
            Sprint(), SignCopy(), Surround(), Tracers(), Velocity(), NoEntityTrace(),
            MiddleClickFriends(), Friends(), Discord()
        )
    }

    private fun register(vararg mods: Module) {
        for (cheat in mods) {
            modules.add(cheat)
        }
    }

    fun <T : Module> getModuleByClass(clazz: Class<T>): Module? {
        for (current in modules) {
            if (current.javaClass == clazz) return current
            if (current::class.isInstance(clazz)) return current as T
        }
        return null
    }


    fun getModuleByName(name: String?): Module? {
        for (current in modules) {
            if (current.alias.contains(name)) return current
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

    fun getModulesNamesInCategory(category: Category): ArrayList<String> {
        val arrayList: ArrayList<String> = arrayListOf("none")
        if (arrayList.contains("none")){
            for(mod in getModulesInCategory(category)){
                arrayList.add(mod.label)
            }
            arrayList.remove("none")
        }
        return arrayList
    }

    fun getModulesInArrayList(): String? {
        val sb: StringBuilder? = null
        val string: String
        sb?.clear()
        for(module in modules){
            if(module.enabled && !module.hidden && module.category != Category.NONE){
                sb?.append("${module.label}\n ")
            }
        }
        string = sb.toString()
        val array = string.split(" ".toRegex()).toTypedArray()
        return array.toString()
    }
}