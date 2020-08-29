package dev.toastmc.client.util

import dev.toastmc.client.ToastClient
import dev.toastmc.client.ToastClient.Companion.MODULE_MANAGER
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings
import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch
import java.io.*

class SettingSaveUtil {
    private val serializer: JanksonValueSerializer = JanksonValueSerializer(false)

    private val configTree: ConfigBranch

    init {
        var configTreeBuilder = ConfigTreeBuilder(null, "config")
        for (module in MODULE_MANAGER.modules) {
            configTreeBuilder = configTreeBuilder.fork(module.label).applyFromPojo(module, AnnotatedSettings.builder().collectOnlyAnnotatedMembers().build()).finishBranch()
        }
        configTree = configTreeBuilder.build()
    }

    fun save() {
        val fos = FileOutputStream(File(ToastClient.CONFIG_FILE))
        FiberSerialization.serialize(configTree, fos, serializer)
        fos.flush()
        fos.close()
    }

    fun load() {
        val fis = FileInputStream(File(ToastClient.CONFIG_FILE))
        FiberSerialization.deserialize(configTree, fis, serializer)
        fis.close()
    }
}