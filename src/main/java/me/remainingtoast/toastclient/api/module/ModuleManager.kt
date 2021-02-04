package me.remainingtoast.toastclient.api.module

import me.remainingtoast.toastclient.ToastClient
import me.remainingtoast.toastclient.client.module.client.*
import me.remainingtoast.toastclient.client.module.combat.AutoArmour
import me.remainingtoast.toastclient.client.module.combat.AutoRespawn
import me.remainingtoast.toastclient.client.module.combat.Offhand
import me.remainingtoast.toastclient.client.module.hud.ArrayListModule
import me.remainingtoast.toastclient.client.module.movement.SafeWalk
import me.remainingtoast.toastclient.client.module.player.MCF
import me.remainingtoast.toastclient.client.module.player.NoEntityTrace
import me.remainingtoast.toastclient.client.module.render.*
import org.lwjgl.glfw.GLFW
import java.util.*
import java.util.stream.Collectors

object ModuleManager {

    var modules: ArrayList<Module> = ArrayList()

    init {
        modules = ArrayList()
        register(ClickGUIModule, Colors, Font, FriendModule, HUDEditor,
                NoRender, ArrayListModule, MCF, Offhand, Capes, NameTags,
                FullBright, SafeWalk, NoEntityTrace, NoFog, AutoArmour, AutoRespawn,
                CustomChat
        )
        println("MODULE MANAGER INITIALISED")
    }

    fun getModulesByCategory(category: Category): ArrayList<Module>? {
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
        ToastClient.CLICKGUI.render()
    }

}