package dev.toastmc.toastclient.api.module

import dev.toastmc.toastclient.impl.module.client.*
import dev.toastmc.toastclient.impl.module.combat.*
import dev.toastmc.toastclient.impl.module.hud.*
import dev.toastmc.toastclient.impl.module.misc.*
import dev.toastmc.toastclient.impl.module.movement.*
import dev.toastmc.toastclient.impl.module.player.*
import dev.toastmc.toastclient.impl.module.render.*

import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import java.util.*
import java.util.stream.Collectors
import kotlin.Comparator

object ModuleManager {

    var modules: ArrayList<Module> = ArrayList()

    init {
        modules = ArrayList()
        register(Font, FriendModule, NoRender, MCF, Offhand, Capes, NameTags,
                FullBright, SafeWalk, NoEntityTrace, NoFog, AutoArmour, AutoRespawn,
                CustomChat, KillAura, AntiKnockback, NoFall, AntiHunger, Sprint, Jesus,
                PortalChat
        )
        Collections.sort(modules, Comparator.comparing(Module::getName))
        println("MODULE MANAGER INITIALISED")
    }

    fun getModulesByCategory(category: Module.Category): ArrayList<Module>? {
        return modules.stream().filter {
                module: Module -> module.getCategory() == category
        }.collect(Collectors.toCollection { ArrayList() })
    }

    fun onKeyRelease(window: Long, keyCode: Int, scancode: Int) {
        for (mod in modules) {
            if (mod.getKey() === InputUtil.fromKeyCode(keyCode, scancode)) {
                if (GLFW.glfwGetKey(window, keyCode) == 0) {
                    mod.toggle()
                }
            }
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

    fun onHUDRender() {
        modules.stream().filter { obj: Module -> obj.isEnabled() }.forEach { obj: Module -> obj.onHUDRender() }
    }

}