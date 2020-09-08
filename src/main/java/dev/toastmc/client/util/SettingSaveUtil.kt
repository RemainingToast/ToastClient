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
    private val annotationSetting = AnnotatedSettings.builder().collectOnlyAnnotatedMembers().collectMembersRecursively().build()
    private var configTree = ConfigTreeBuilder(null, "config")

    private val serializer: JanksonValueSerializer = JanksonValueSerializer(false)

    fun initSettingUtil() {
        directory = ToastClient.MOD_DIRECTORY
        module = File(directory, "modules.json")
        if(!ToastClient.FILE_MANAGER.fileExists(module!!)){ // Prevents crash when loading client for the first time.
            ToastClient.FILE_MANAGER.createFile(module!!)   //
            save()                                          //
        }
        load()
        save()
        for (module in MODULE_MANAGER.modules) {
            if (module.config == null) {
                module.config = ConfigTreeBuilder(configTree, module.label).applyFromPojo(module)
            }
            configTree = configTree.fork(module.label).applyFromPojo(module, annotationSetting).finishBranch()
        }
        initialized = true
    }

    fun save() {
        val fos = FileOutputStream(module!!)
        FiberSerialization.serialize(configTree.build(), fos, serializer)
        fos.flush()
        fos.close()
    }

    fun load() {
        val fis = FileInputStream(module!!)
        FiberSerialization.deserialize(configTree.build(), fis, serializer)
        for (module in MODULE_MANAGER.modules) {
            if (module.enabled) {
                module.enable()
            } else {
                module.disable()
            }
        }
        fis.close()
    }
}
