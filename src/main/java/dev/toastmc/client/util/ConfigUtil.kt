package dev.toastmc.client.util

import dev.toastmc.client.ToastClient
import dev.toastmc.client.ToastClient.Companion.MODULE_MANAGER
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.NumberConfigType
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree
import net.minecraft.client.MinecraftClient
import java.io.*
import java.math.BigDecimal
import java.nio.file.Files

object ConfigUtil {
    private var configFile = File("toastclient/modules.json")
    private var module: File? = null
    private val annotationSetting = AnnotatedSettings.builder().collectOnlyAnnotatedMembers().collectMembersRecursively().build()

    private val serializer: JanksonValueSerializer = JanksonValueSerializer(false)

    fun init() {
        module = File(File(MinecraftClient.getInstance().runDirectory, "toastclient/"), "modules.json")
        if (configFile.createNewFile()) save()
        load()
        save()
    }

    fun getConfigTree(): ConfigBranch? {
        var configTree = ConfigTreeBuilder(null, "config")
        for (module in MODULE_MANAGER.modules) {
            configTree = configTree.withChild(ConfigTreeBuilder(null, module.label).applyFromPojo(module, annotationSetting).build())
        }
        return configTree.build()
    }

    fun save() {
        val fos = FileOutputStream(module!!)
        FiberSerialization.serialize(getConfigTree(), fos, serializer)
        fos.flush()
        fos.close()
    }

    fun load() {
        try {
            val fis = FileInputStream(module!!)
            FiberSerialization.deserialize(getConfigTree(), fis, serializer)
            for (module in MODULE_MANAGER.modules) {
                module.setEnabled(module.enabled)
            }
            fis.close()

        } catch (ignored: ValueDeserializationException){
            println("Config failed to load. \n\nStackTrace:\n${ignored.stackTrace}")
        }
    }

    fun ConfigBranch.getBranch(name: String): ConfigBranch? {
        return this.lookupBranch(name)
    }

    fun ConfigTree.getBranch(name: String): ConfigBranch? {
        return this.lookupBranch(name)
    }

    fun ConfigBranch.getNumber(type: NumberConfigType<*>, name: String): Any? {
        return this.lookupLeaf(name, type.serializedType)?.value
    }

    fun ConfigTree.getNumber(type: NumberConfigType<*>, name: String): Any? {
        return this.lookupLeaf(name, type.serializedType)?.value
    }

    fun ConfigBranch.setNumber(type: NumberConfigType<*>, name: String, newValue: BigDecimal): Boolean? {
        return this.lookupLeaf(name, type.serializedType)?.setValue(newValue)
    }

    fun ConfigTree.setNumber(type: NumberConfigType<*>, name: String, newValue: BigDecimal): Boolean? {
        return this.lookupLeaf(name, type.serializedType)?.setValue(newValue)
    }

    fun ConfigBranch.getBoolean(name: String): Boolean? {
        return this.lookupLeaf(name, ConfigTypes.BOOLEAN.serializedType)?.value
    }

    fun ConfigTree.getBoolean(name: String): Boolean? {
        return this.lookupLeaf(name, ConfigTypes.BOOLEAN.serializedType)?.value
    }

    fun ConfigBranch.setBoolean(name: String, newValue: Boolean): Boolean? {
        return this.lookupLeaf(name, ConfigTypes.BOOLEAN.serializedType)?.setValue(newValue)
    }

    fun ConfigTree.setBoolean(name: String, newValue: Boolean): Boolean? {
        return this.lookupLeaf(name, ConfigTypes.BOOLEAN.serializedType)?.setValue(newValue)
    }
}

class FileManager {
    private var modDirectory: File? = null
    private var initialized = false

    /**
     * Initializes the file manager
     */
    fun initFileManager() {
        modDirectory = MOD_DIRECTORY
        if ((modDirectory ?: return).mkdirs()) {
            fileManagerLogger("Created Mod Directory! " + (modDirectory ?: return).path)
        }
        initialized = true
    }

    private fun fileManagerLogger(m: String) {
        println("[" + ToastClient.MODNAME.replace(" ", "") + "] " + m)
    }

    /**
     * Creates a file if there is not already a file at the specified path
     * @param file path to create the file at
     */
    fun createFile(file: File): File {
        if (!initialized) return file
        val newFile = File(modDirectory, file.name)
        return try {
            if (newFile.createNewFile()) {
                fileManagerLogger("File " + newFile.absolutePath + " has been created.")
            }
            newFile
        } catch (e: IOException) {
            file
        }
    }

    fun fileExists(file: File): Boolean {
        return file.exists()
    }


    /**
     * Writes a String to a File
     * @param file File to write the String to
     * @param lines String to write to the file
     */
    fun writeFile(file: File, lines: String): File {
        return try {
            val writer = FileWriter(file)
            writer.write(lines)
            writer.close()
            file
        } catch (e: IOException) {
            fileManagerLogger("Failed to write to file " + file.absolutePath)
            e.printStackTrace()
            file
        }
    }

    /**
     * Reads a files lines to array of Strings
     * @param file File to read
     */
    fun readFile(file: File): MutableList<String> {
        return if (!file.exists() || !initialized) ArrayList() else try {
            Files.readAllLines(file.toPath())
        } catch (e: IOException) {
            fileManagerLogger("Failed to read file " + file.absolutePath)
            e.printStackTrace()
            ArrayList<String>()
        }
    }
}