package toast.client.utils

import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import toast.client.ToastClient
import toast.client.ToastClient.FILE_MANAGER
import toast.client.modules.config.Setting
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*

/**
 * Class containing methods to read and load the mod's configuration from a file and also to write said configuration to a file
 */
class ConfigManager {

    /**
     * Writes the module settings JSON to a file
     */
    fun writeConfig() {
        if (canWrite) {
            val config: MutableMap<String, Map<String, Setting>> = TreeMap()
            for (module in ToastClient.MODULE_MANAGER.modules) {
                config[module.name] = module.settings.getSettings()
            }
            FILE_MANAGER.writeFile(File(configFile), gson.toJson(config, object : TypeToken<LinkedTreeMap<String?, LinkedTreeMap<String?, Setting?>?>?>() {}.type))
        }
    }

    /**
     * Loads the module settings from a file containing JSON
     */
    fun loadConfig() {
        val config: LinkedTreeMap<String?, LinkedTreeMap<String?, Setting?>?>?
        try {
            config = gson.fromJson(FileReader(FILE_MANAGER.createFile(File(configFile))), object : TypeToken<LinkedTreeMap<String?, LinkedTreeMap<String?, Setting?>?>?>() {}.type)
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
                for ((key, value) in config[module.name] ?: continue) {
                    if (key != null && value != null) {
                        module.settings.getSettings().replace(key, value)
                    }
                }
                module.settings.getSettings()
            }
        }
    }

    /**
     * Writes the JSON containing module key-binds to a file
     */
    fun writeKeyBinds() {
        val keyBinds: MutableMap<String, Int>
        if (canWrite) {
            keyBinds = TreeMap()
            for (module in ToastClient.MODULE_MANAGER.modules) {
                keyBinds[module.name] = module.key
            }
            FILE_MANAGER.writeFile(File(keyBindsFile), gson.toJson(keyBinds, object : TypeToken<Map<String?, Int?>?>() {}.type))
        }
    }

    /**
     * Loads the modules' key-binds from a file containing JSON
     */
    fun loadKeyBinds() {
        val keyBinds: Map<String, Int>?
        try {
            keyBinds = gson.fromJson(FileReader(FILE_MANAGER.createFile(File(keyBindsFile))), object : TypeToken<Map<String?, Int?>?>() {}.type)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return
        }
        if (keyBinds == null || keyBinds.isEmpty()) {
            writeKeyBinds()
            return
        }
        for (module in ToastClient.MODULE_MANAGER.modules) {
            if (keyBinds.contains(module.name)) {
                module.key = keyBinds.getValue(module.name)
            }
        }
    }

    /**
     * Writes the macros to a file in the JSON format
     */
    fun writeMacros() {
        if (canWrite) {
            FILE_MANAGER.writeFile(File(macrosFile), gson.toJson(macros, object : TypeToken<Map<String?, Int?>?>() {}.type))
        }
    }

    /**
     * Loads macros from a file containing JSON
     */
    fun loadMacros() {
        try {
            macros = gson.fromJson(FileReader(FILE_MANAGER.createFile(File(macrosFile))), object : TypeToken<Map<String?, Int?>?>() {}.type) ?: mutableMapOf<String?, Int?>()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * Check if a key has a macro bound to it
     */
    fun checkForMacro(key: Int, action: Int) {
        loadMacros()
        if (action == GLFW.GLFW_PRESS) {
            for ((command, _key) in macros ?: return) {
                if (key == _key) {
                    (MinecraftClient.getInstance().player ?: return).sendChatMessage(command)
                }
            }
        }
    }

    /**
     * Writes JSON containing the enabled state of all modules to a file
     */
    fun writeModules() {
        if (canWrite) {
            val modules: MutableMap<String, Boolean> = TreeMap()
            for (module in ToastClient.MODULE_MANAGER.modules) {
                modules[module.name] = module.enabled
            }
            FILE_MANAGER.writeFile(File(modulesFile), gson.toJson(modules, object : TypeToken<Map<String?, Boolean?>?>() {}.type))
        }
    }

    /**
     * Gets the enabled state of all the modules from a file containing JSON data
     */
    fun loadModules() {
        val modules: Map<String, Boolean>?
        try {
            modules = gson.fromJson(FileReader(FILE_MANAGER.createFile(File(modulesFile))), object : TypeToken<Map<String?, Boolean?>?>() {}.type)
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

    /**
     * Enables the writing of config files
     */
    fun enableWrite() {
        canWrite = true
    }

    /**
     * Adds a macro
     */
    fun addMacro(command: String, key: Int) {
        (macros ?: return).putIfAbsent(command, key)
        writeMacros()
    }

    /**
     * Retrieves the Map containing all macros
     */
    fun getMacros(): MutableMap<String?, Int?>? = macros

    companion object {
        /**
         * Name of the file where the module settings json is stored
         */
        const val configFile: String = "toastclient/config.json"

        /**
         * Name of the file where JSON that contains the enabled state of modules is stored
         */
        const val modulesFile: String = "toastclient/modules.json"

        /**
         * Name of the file where the JSON containing the key-binds for each module is stored
         */
        const val keyBindsFile: String = "toastclient/keybinds.json"

        /**
         * Name of the file where the JSON containing macros is stored
         */
        const val macrosFile: String = "toastclient/macros.json"

        /**
         * Map containing macros and the keys they are bound to
         */
        var macros = mutableMapOf<String?, Int?>()

        private val gson = GsonBuilder().setPrettyPrinting().create()
        private const val disabledOnStart = "Panic, ClickGui"
        private var canWrite = false
    }
}