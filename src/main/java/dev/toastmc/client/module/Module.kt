package dev.toastmc.client.module

import dev.toastmc.client.ToastClient
import dev.toastmc.client.command.CommandManifest
import net.minecraft.client.MinecraftClient

open class Module (){
    protected var mc: MinecraftClient = MinecraftClient.getInstance()

    var label: String? = null
    var description: String? = null
    var usage: String? = null
    var alias: Array<String>? = null
    var hidden: Boolean? = null
    var enabled: Boolean? = null
    var persistent: Boolean? = null
    var key: Int = -1
    var category: Category? = null

    init {
        if (javaClass.isAnnotationPresent(ModuleManifest::class.java)) {
            val moduleManifest = javaClass.getAnnotation(ModuleManifest::class.java)
            label = moduleManifest.label
            alias = moduleManifest.aliases
            description = moduleManifest.description
            usage = moduleManifest.usage
            hidden = moduleManifest.hidden
            enabled = moduleManifest.enabled
            persistent = moduleManifest.persistent
            key = moduleManifest.key
            category = moduleManifest.category
        }
    }

    private fun setEnabled(newEnabled: Boolean): Boolean {
        enabled = newEnabled
        try {
            ToastClient.EVENT_BUS.post(this@Module)
        } catch (ignored: IllegalArgumentException) {

        }
        return newEnabled
    }

    fun toggle(){
        setEnabled(!enabled!!)
    }


}