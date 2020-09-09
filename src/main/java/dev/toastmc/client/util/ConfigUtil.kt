package dev.toastmc.client.util

import dev.toastmc.client.ToastClient
import dev.toastmc.client.ToastClient.Companion.MODULE_MANAGER
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch
import net.minecraft.client.MinecraftClient
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.BigInteger

class ConfigUtil {
    private var module: File? = null
    private var initialized = false
    private val annotationSetting = AnnotatedSettings.builder().collectOnlyAnnotatedMembers().collectMembersRecursively().build()

    private val serializer: JanksonValueSerializer = JanksonValueSerializer(false)

    fun init() {
        module = File(File(MinecraftClient.getInstance().runDirectory, "toastclient/"), "modules.json")
        if (!ToastClient.FILE_MANAGER.fileExists(module!!)) { // Prevents crash when loading client for the first time.
            ToastClient.FILE_MANAGER.createFile(module!!)   //
            save()                                          //
        }
        load()
        save()
        initialized = true
    }

    private fun getConfigTree(): ConfigBranch? {
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

    fun ConfigTreeBuilder.getInt(settingName: String): Int? {
        return this.lookupLeaf(settingName, ConfigTypes.INTEGER.serializedType)?.value?.toInt()
    }

    fun ConfigTreeBuilder.setInt(settingName: String, value: Int) {
        this.lookupLeaf(settingName, ConfigTypes.INTEGER.serializedType)?.value = BigDecimal(value)
        save()
    }

    fun ConfigTreeBuilder.getLong(settingName: String): Long? {
        return this.lookupLeaf(settingName, ConfigTypes.LONG.serializedType)?.value?.longValueExact()
    }

    fun ConfigTreeBuilder.setLong(settingName: String, value: Long) {
        this.lookupLeaf(settingName, ConfigTypes.LONG.serializedType)?.value = BigDecimal(value)
        save()
    }

    fun ConfigTreeBuilder.getBigInteger(settingName: String): BigInteger? {
        return this.lookupLeaf(settingName, ConfigTypes.UNBOUNDED_INTEGER.serializedType)?.value?.toBigIntegerExact()
    }

    fun ConfigTreeBuilder.setBigInteger(settingName: String, value: BigInteger) {
        this.lookupLeaf(settingName, ConfigTypes.UNBOUNDED_INTEGER.serializedType)?.value = value.toBigDecimal()
        save()
    }

    fun ConfigTreeBuilder.getDouble(settingName: String): Double? {
        return this.lookupLeaf(settingName, ConfigTypes.DOUBLE.serializedType)?.value?.toDouble()
    }

    fun ConfigTreeBuilder.setDouble(settingName: String, value: Double) {
        this.lookupLeaf(settingName, ConfigTypes.DOUBLE.serializedType)?.value = BigDecimal(value)
        save()
    }

    fun ConfigTreeBuilder.getFloat(settingName: String): Float? {
        return this.lookupLeaf(settingName, ConfigTypes.FLOAT.serializedType)?.value?.toFloat()
    }

    fun ConfigTreeBuilder.setFloat(settingName: String, value: Float) {
        this.lookupLeaf(settingName, ConfigTypes.FLOAT.serializedType)?.value = BigDecimal(value.toDouble())
        save()
    }

    fun ConfigTreeBuilder.getBigDecimal(settingName: String): BigDecimal? {
        return this.lookupLeaf(settingName, ConfigTypes.UNBOUNDED_DECIMAL.serializedType)?.value
    }

    fun ConfigTreeBuilder.setBigDecimal(settingName: String, value: BigDecimal) {
        this.lookupLeaf(settingName, ConfigTypes.UNBOUNDED_DECIMAL.serializedType)?.value = value
        save()
    }

    fun ConfigTreeBuilder.getBoolean(settingName: String): Boolean? {
        return this.lookupLeaf(settingName, ConfigTypes.BOOLEAN.serializedType)?.value
    }

    fun ConfigTreeBuilder.setBoolean(settingName: String, value: Boolean) {
        this.lookupLeaf(settingName, ConfigTypes.BOOLEAN.serializedType)?.value = value
        save()
    }
}
