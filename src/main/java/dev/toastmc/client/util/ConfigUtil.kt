package dev.toastmc.client.util

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
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigDecimal

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