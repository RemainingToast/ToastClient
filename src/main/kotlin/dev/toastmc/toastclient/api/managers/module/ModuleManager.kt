package dev.toastmc.toastclient.api.managers.module

import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.ToastClient
import dev.toastmc.toastclient.api.events.OverlayRenderEvent
import dev.toastmc.toastclient.api.events.TickEvent
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import dev.toastmc.toastclient.impl.module.client.FriendModule
import dev.toastmc.toastclient.impl.module.client.HUDEditor
import dev.toastmc.toastclient.impl.module.combat.*
import dev.toastmc.toastclient.impl.module.misc.*
import dev.toastmc.toastclient.impl.module.movement.*
import dev.toastmc.toastclient.impl.module.player.*
import dev.toastmc.toastclient.impl.module.render.*
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import org.quantumclient.energy.Subscribe
import java.util.*
import java.util.stream.Collectors

object ModuleManager : IToastClient {

    var modules: ArrayList<Module> = ArrayList()

    fun init() {
        register(
            AutoArmour,
            AutoRespawn,
            AutoWalk,
            AutoTotem,
            ClickGUI,
            HUDEditor,
            ExtraSign,
            ExtraTab,
            ExtraTooltips,
//            Font,
            FastUtil,
            FriendModule,
            NoRender,
            MCF,
            Offhand,
            Capes,
            NameTags,
            FullBright,
            SafeWalk,
            NoEntityTrace,
            NoFog,
            CustomChat,
            KillAura,
            Velocity,
            NoFall,
            AntiHunger,
            Sprint,
            Jesus,
            PortalChat,
            Particles,
            TestModule
        )

        ToastClient.eventBus.register(this)

        logger.info("Finished loading ${modules.size} modules")
    }

    private fun register(vararg mods: Module) {
        modules = ArrayList()

        for (mod in mods) {
            modules.add(mod)
        }

        Collections.sort(modules, Comparator.comparing(Module::getName))
    }

    fun getModulesByCategory(category: Module.Category): ArrayList<Module>? {
        return modules.stream()
            .filter { module: Module -> module.getCategory() == category }
            .collect(Collectors.toCollection { ArrayList() })
    }

    fun onKey(window: Long, keyCode: Int, scancode: Int) {
        for (mod in modules) {
            if (mod.getKey() === InputUtil.fromKeyCode(keyCode, scancode)) {
                if (GLFW.glfwGetKey(window, keyCode) == 0) {
                    mod.toggle()
                }
            }
        }
    }

    @Subscribe
    fun on(event: TickEvent.Client.InGame) {
        modules.stream().filter { mod: Module -> mod.isEnabled() }.forEach { mod: Module -> mod.onUpdate() }
    }

    @Subscribe
    fun on(event: OverlayRenderEvent) {
        modules.stream().filter { mod: Module -> mod.isEnabled() }.forEach { mod: Module -> mod.onHUDRender() }
        HUDEditor.SCREEN.getComponents().filter { c -> c.enabled }.forEach { c -> c.render(event.matrix) }
    }

}