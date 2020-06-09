package toast.client.utils

import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import toast.client.ToastClient
import toast.client.modules.config.Setting
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*

class ConfigManager {
    @JvmField
    var macros: MutableMap<String, Int>? = null
    private var canWrite = false
    fun writeConfig() {
        if (canWrite) {
            val config: MutableMap<String, Map<String, Setting>> = TreeMap()
            for (module in ToastClient.MODULE_MANAGER.modules) {
                config[module.name] = module.settings.getSettings()
            }
            FileManager.writeFile(configFile, gson.toJson(config, object : TypeToken<MutableMap<String, Map<String, Setting>>>() {}.type))
        }
    }

    fun loadConfig() {
        val config: LinkedTreeMap<String, LinkedTreeMap<String, Setting>>
        config = try {
            gson.fromJson<LinkedTreeMap<String, LinkedTreeMap<String, Setting>>>(FileReader(FileManager.createFile(configFile)), object : TypeToken<LinkedTreeMap<String, LinkedTreeMap<String, Setting>>>() {}.type)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return
        }
        if (config == null || config.isEmpty()) {
            writeConfig()
            return
        }
        for (module in ToastClient.MODULE_MANAGER.modules) {
            if (config.containsKey(module.name)) {
                for (Entry in config[module.name]!!) {
                    module.settings.getSettings().replace(Entry.key, Entry.value)
                }
                module.settings.getSettings()
            }
        }
    }

    fun writeKeyBinds() {
        val keybinds: MutableMap<String, Int>
        if (canWrite) {
            keybinds = TreeMap()
            for (module in ToastClient.MODULE_MANAGER.modules) {
                keybinds[module.name] = module.key
            }
            FileManager.writeFile(keybindsFile, gson.toJson(keybinds, object : TypeToken<MutableMap<String, Int>>(){}.type))
        }
    }

    fun loadKeyBinds() {
        val keybinds: Map<String, Int>?
        keybinds = try {
            gson.fromJson<Map<String, Int>>(FileReader(FileManager.createFile(keybindsFile)), object : TypeToken<Map<String?, Int?>?>() {}.type)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return
        }
        if (keybinds == null || keybinds.isEmpty()) {
            writeKeyBinds()
            return
        }
        for (module in ToastClient.MODULE_MANAGER.modules) {
            if (keybinds.contains(module.name)) {
                module.key = keybinds.getValue(module.name)
            }
        }
    }

    fun writeMacros() {
        if (canWrite) {
            FileManager.writeFile(macrosFile, gson.toJson(macros, object : TypeToken<MutableMap<String, Int>>(){}.type))
        }
    }

    fun loadMacros() {
        try {
            macros = gson.fromJson(FileReader(FileManager.createFile(macrosFile)), object : TypeToken<MutableMap<String?, Int?>?>() {}.type)
            if (macros == null) macros = TreeMap()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    fun checkForMacro(key: Int, action: Int) {
        if (MinecraftClient.getInstance().player != null) {
            loadMacros()
            if (action == GLFW.GLFW_PRESS) {
                for ((command, _key) in macros!!) {
                    if (key == _key) {
                        MinecraftClient.getInstance().player!!.sendChatMessage(command)
                    }
                }
            }
        }
    }

    fun writeModules() {
        if (canWrite) {
            val modules: MutableMap<String, Boolean> = TreeMap()
            for (module in ToastClient.MODULE_MANAGER.modules) {
                modules[module.name] = module.isEnabled()
            }
            FileManager.writeFile(modulesFile, gson.toJson(modules, object : TypeToken<MutableMap<String, Boolean>>(){}.type))
        }
    }

    fun loadModules() {
        val modules: Map<String, Boolean>?
        modules = try {
            gson.fromJson<Map<String, Boolean>>(FileReader(FileManager.createFile(modulesFile)), object : TypeToken<Map<String?, Boolean?>?>() {}.type)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return
        }
        if (modules == null || modules.isEmpty()) {
            writeModules()
            return
        }
        for (module in ToastClient.MODULE_MANAGER.modules) {
            if (!disabledOnStart.contains(module.name)) {
                if (modules.contains(module.name)) {
                    when (modules.getValue(module.name)) {
                        true -> module.enable()
                        false -> module.disable()
                    }
                }
            }
        }
    }

    fun enableWrite() {
        canWrite = true
    }

    companion object {
        const val configFile = "config.json"
        const val modulesFile = "modules.json"
        const val keybindsFile = "keybinds.json"
        const val macrosFile = "macros.json"
        private val gson = GsonBuilder().setPrettyPrinting().create()
        private const val disabledOnStart = "Panic, ClickGui"
    }
}