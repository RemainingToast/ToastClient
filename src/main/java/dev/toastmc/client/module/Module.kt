package dev.toastmc.client.module

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.util.ConfigUtil
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
open class Module {

    var label: String = ""
    var description: String = ""
    var usage: String = ""
    var alias: Array<String> = arrayOf("")

    @Setting(name = "Persistent")
    var persistent: Boolean = false

    @Setting(name = "Category")
    var category: Category = Category.NONE

    @Setting(name = "Enabled")
    var enabled: Boolean = false

    @Setting(name = "Hidden")
    var hidden: Boolean = false

    @Setting(name = "KeyBind")
    var key: Int = -1

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

    fun setEnabled(newEnabled: Boolean): Boolean {
        enabled = newEnabled
        if (enabled) {
            try {
                EVENT_BUS.post(this@Module)
            } catch (ignored: IllegalArgumentException) {
            }
            try {
                onEnable()
            } catch (ignored: NullPointerException) {
            }
        } else {
            try {
//                ToastClient.EVENT_BUS.post(this@Module)
            } catch (ignored: IllegalArgumentException) {
            }
            try {
                onDisable()
            } catch (ignored: NullPointerException) {
            }
        }
        return enabled
    }

    fun setHidden(newHidden: Boolean): Boolean {
        hidden = newHidden
        ConfigUtil.save()
        return hidden
    }

    fun disable(): Boolean {
        val enabled = setEnabled(false)
        ConfigUtil.save()
        return enabled
    }

    fun enable(): Boolean {
        val enabled = setEnabled(true)
        ConfigUtil.save()
        return enabled
    }

    fun toggle(): Boolean {
        val enabled = setEnabled(!this.enabled)
        ConfigUtil.save()
        return enabled
    }

    open fun onEnable() {

    }

    open fun onDisable() {

    }
}