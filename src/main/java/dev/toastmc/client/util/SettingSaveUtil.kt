package dev.toastmc.client.util

import dev.toastmc.client.ToastClient
import dev.toastmc.client.ToastClient.Companion.MODULE_MANAGER
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class SettingSaveUtil {
    private var directory: File? = null
    private var module: File? = null
    private var initialized = false

    private val serializer: JanksonValueSerializer = JanksonValueSerializer(false)
    private val configTree: ConfigBranch

    fun initSettingUtil() {
        directory = ToastClient.MOD_DIRECTORY
        module = File(directory, "modules.json")
        if(!ToastClient.FILE_MANAGER.fileExists(module!!)){ // Prevents crash when loading client for the first time.
            ToastClient.FILE_MANAGER.createFile(module!!)   //
            save()                                          //
        }
        load()
        save()
        initialized = true
    }

    init {
        var configTreeBuilder = ConfigTreeBuilder(null, "config")
        for (module in MODULE_MANAGER.modules) {
            configTreeBuilder = configTreeBuilder.fork(module.label).applyFromPojo(module, AnnotatedSettings.builder().collectOnlyAnnotatedMembers().build()).finishBranch()
        }
        configTree = configTreeBuilder.build()
    }

    fun save() {
        val fos = FileOutputStream(module!!)
        FiberSerialization.serialize(configTree, fos, serializer)
        fos.flush()
        fos.close()
    }

    fun load() {
        val fis = FileInputStream(module!!)
        FiberSerialization.deserialize(configTree, fis, serializer)
        fis.close()
    }
}