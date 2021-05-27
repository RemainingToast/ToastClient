package dev.toastmc.toastclient.api.module

import org.lwjgl.glfw.GLFW
import java.util.*
import java.util.stream.Collectors
import kotlin.Comparator

object ModuleManager {

    var modules: ArrayList<Module> = ArrayList()

    init {
        modules = ArrayList()
//        register(ClickGUIModule, Colors, Font, FriendModule, HUDEditor,
//                NoRender, ArrayListModule, MCF, Offhand, Capes, NameTags,
//                FullBright, SafeWalk, NoEntityTrace, NoFog, AutoArmour, AutoRespawn,
//                CustomChat, KillAura, AntiKnockback, NoFall, AntiHunger, Sprint, Jesus,
//                PortalChat
//        )
        Collections.sort(modules, Comparator.comparing(Module::name))
        println("MODULE MANAGER INITIALISED")
    }

    fun getModulesByCategory(category: Module.Category): ArrayList<Module>? {
        return modules.stream().filter { module: Module -> module.getCategory() == category }
            .collect(Collectors.toCollection { ArrayList() })
    }

    fun toggleBind(key: Int) {
        if (key == 0 || key == GLFW.GLFW_KEY_UNKNOWN) return
        modules.filter { it.getBind() == key }.forEach {
            it.toggle()
        }
    }

    private fun register(vararg mods: Module) {
        for (cheat in mods) {
            modules.add(cheat)
        }
    }

    fun onUpdate() {
        modules.stream().filter { obj: Module -> obj.isEnabled() }.forEach { obj: Module -> obj.onUpdate() }
    }

    fun onRender() {
        modules.stream().filter { obj: Module -> obj.isEnabled() }.forEach { obj: Module -> obj.onOverlayRender() }
//        ToastClient.CLICKGUI.render()
    }

}