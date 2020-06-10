package toast.client.modules

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import toast.client.ToastClient
import toast.client.modules.combat.AutoRespawn
import toast.client.modules.combat.AutoTotem
import toast.client.modules.combat.BowSpam
import toast.client.modules.combat.KillAura
import toast.client.modules.misc.CustomChat
import toast.client.modules.misc.Panic
import toast.client.modules.misc.Panic.Companion.IsPanicking
import toast.client.modules.misc.PortalChat
import toast.client.modules.misc.Spammer
import toast.client.modules.movement.*
import toast.client.modules.player.AutoTool
import toast.client.modules.combat.Surround
import toast.client.modules.render.ClickGui
import toast.client.modules.render.Fullbright
import toast.client.modules.render.HUD
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

@Environment(EnvType.CLIENT)
class ModuleManager {
    fun onKey(key: Int, action: Int) {
        modules.forEach { module ->
            if (module.key == key && action == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen == null) {
                if (MinecraftClient.getInstance().inGameHud.chatHud.isChatFocused) return@forEach
                if (module.javaClass != Panic::class.java && IsPanicking()) return
                module.toggle()
            }
        }
    }

    fun getModule(moduleClass: Class<out Module?>): Module? = modules.firstOrNull { it.javaClass == moduleClass }

    fun getModule(name: String): Module? = modules.firstOrNull { it.name == name }

    var modules: CopyOnWriteArrayList<Module> = CopyOnWriteArrayList()

    fun getModulesInCategory(category: Module.Category): List<Module> {
        val moduleList: MutableList<Module> = ArrayList()
        modules.forEach { module ->
            if (module.category === category) {
                moduleList.add(module)
            }
        }
        return moduleList
    }

    fun loadModules() {
        modules.clear()
        // alphabetical order please
        modules.add(AutoWalk())
        modules.add(AutoRespawn())
        modules.add(AutoTool())
        modules.add(AutoTotem())
        //modules.add(new BlockESP());
        modules.add(BowSpam())
        modules.add(CustomChat())
        modules.add(ClickGui())
        modules.add(FastStop())
        modules.add(Flight())
        modules.add(Fullbright())
        //modules.add(new HoleESP());
        modules.add(HUD())
        modules.add(KillAura())
        modules.add(NoFall())
        modules.add(Panic())
        modules.add(PortalChat())
        //modules.add(new ShulkerPreview());
        modules.add(Spammer())
        modules.add(Sprint())
        //modules.add(new StorageESP());
        modules.add(Surround())
        modules.add(Velocity())
        ToastClient.CONFIG_MANAGER.enableWrite()
        ToastClient.CONFIG_MANAGER.loadConfig()
        ToastClient.CONFIG_MANAGER.loadKeyBinds()
        ToastClient.CONFIG_MANAGER.loadModules()
        ToastClient.CONFIG_MANAGER.loadMacros()
    }

    companion object {
        val modules = CopyOnWriteArrayList<Module>()
    }
}