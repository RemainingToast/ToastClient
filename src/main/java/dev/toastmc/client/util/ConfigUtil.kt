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
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigDecimal

object ConfigUtil {
    private var configFile = File("toastclient/modules.json")
    private val annotationSetting = AnnotatedSettings.builder().collectOnlyAnnotatedMembers().collectMembersRecursively().build()

    private val serializer: JanksonValueSerializer = JanksonValueSerializer(false)

    fun init() {
        configFile.also {
            file -> file.parentFile.mkdirs()
            save()
        }
        load()
        save()
    }

    fun getConfigTree(): ConfigTree? {
        return getConfigBranch()
    }

    fun getConfigBranch(): ConfigBranch? {
        var configTree = ConfigTreeBuilder(null, "config")
        for (module in MODULE_MANAGER.modules) {
            configTree = configTree.withChild(ConfigTreeBuilder(null, module.label).applyFromPojo(module, annotationSetting).build())
        }
        return configTree.build()
    }

    fun save() {
        val fos = FileOutputStream(configFile)
        FiberSerialization.serialize(getConfigTree(), fos, serializer)
        fos.flush()
        fos.close()
    }

    fun load() {
        try {
            val fis = FileInputStream(configFile)
            FiberSerialization.deserialize(getConfigTree(), fis, serializer)
            for (module in MODULE_MANAGER.modules) {
                if(module.enabled) module.setEnabled(true)
            }
            fis.close()
        } catch (ignored: ValueDeserializationException){
            println("Config failed to load. StackTrace: ${ignored.stackTrace}")
        }
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