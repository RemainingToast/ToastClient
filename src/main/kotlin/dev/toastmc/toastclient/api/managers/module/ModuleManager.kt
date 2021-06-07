package dev.toastmc.toastclient.api.managers.module

import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.ToastClient
import dev.toastmc.toastclient.api.events.KeyEvent
import dev.toastmc.toastclient.api.events.OverlayRenderEvent
import dev.toastmc.toastclient.api.events.TickEvent
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import dev.toastmc.toastclient.impl.module.client.FriendModule
import dev.toastmc.toastclient.impl.module.client.HUDEditor
import dev.toastmc.toastclient.impl.module.combat.*
import dev.toastmc.toastclient.impl.module.misc.ExtraSign
import dev.toastmc.toastclient.impl.module.misc.ExtraTab
import dev.toastmc.toastclient.impl.module.misc.ExtraTooltips
import dev.toastmc.toastclient.impl.module.misc.PortalChat
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

    /**
     * A-Z
     * Commented Modules are either incomplete/broken/dev module
     */
    fun init() {
        // Client
        register(
            ClickGUI,
//            Font,
            FriendModule,
            HUDEditor
        )

        // Combat
        register(
            AutoAnvil,
            AutoArmour,
            AutoRespawn,
            AutoTotem,
            CrystalAura,
            KillAura
//            Offhand
        )

        // Misc
        register(
//            CustomChat,
            ExtraSign,
            ExtraTab,
            ExtraTooltips,
            PortalChat
        )

        // Movement
        register(
            AutoWalk,
            FastStop,
            Jesus,
            NoFall,
            SafeWalk,
            Sprint
        )

        // Player
        register(
            AntiHunger,
            FastUtil,
            MCF,
            NoEntityTrace,
            Velocity
        )

        // Render
        register(
//            Capes,
            FullBright,
            NameTags,
            NoFog,
            NoRender,
            Particles,
            TestModule
//            TestModule,
            Tracers,
            ViewModel
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

    @Subscribe
    fun on(event: KeyEvent) {
        if (mc.currentScreen == ClickGUI.SCREEN || mc.currentScreen == HUDEditor.SCREEN) return
        for (mod in modules) {
            if (mod.getKey() === InputUtil.fromKeyCode(event.key, event.scancode)) {
                if (GLFW.glfwGetKey(event.window, event.key) == GLFW.GLFW_RELEASE) {
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