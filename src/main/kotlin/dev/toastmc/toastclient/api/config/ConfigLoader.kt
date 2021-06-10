package dev.toastmc.toastclient.api.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.api.config.ConfigUtil.hudDirectory
import dev.toastmc.toastclient.api.config.ConfigUtil.mainDirectory
import dev.toastmc.toastclient.api.config.ConfigUtil.moduleDirectory
import dev.toastmc.toastclient.api.managers.SettingManager
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.managers.module.ModuleManager
import dev.toastmc.toastclient.api.setting.Setting
import dev.toastmc.toastclient.api.setting.Setting.*
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import dev.toastmc.toastclient.impl.module.client.HUDEditor
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Original @author Hoosiers
 * Modified for my client base
 **/
object ConfigLoader : IToastClient {

    fun loadModules() {
        try {
            for (module in ModuleManager.modules) {
                loadModuleDirect(module)
            }
        } catch (ignore: IOException) { }
    }

    fun loadComponents() {
        for (component in HUDEditor.SCREEN.getComponents()) {
            try {
                loadHudComponentDirect(component)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun loadModuleDirect(module: Module) {
        val path = Paths.get("$mainDirectory$moduleDirectory${module.getName()}.json")

        if (!Files.exists(path)) {
            return
        }

        val inputStream: InputStream = Files.newInputStream(path)
        val moduleObject: JsonObject = JsonParser().parse(InputStreamReader(inputStream)).asJsonObject

        if (moduleObject["Module"] == null) {
            return
        }

        val settingObject = moduleObject["Settings"].asJsonObject

        for (setting in SettingManager.getSettingsForMod(module)) {
            val dataObject = settingObject[setting.id]
            if (dataObject != null && dataObject.isJsonPrimitive) {
                when (setting.type!!) {
                    Type.GROUP -> (setting as Group).isExpanded = settingObject["${setting.id}.Expanded"].asBoolean
                    Type.BOOLEAN -> (setting as Setting.Boolean).value = dataObject.asBoolean
                    Type.NUMBER -> (setting as Setting.Number).value = dataObject.asDouble
                    Type.COLOR -> (setting as ColorSetting).fromInteger(dataObject.asInt)
                    Type.MODE -> (setting as Mode).value = dataObject.asString
                }
            }
        }

        module.setEnabled(moduleObject["Enabled"].asBoolean)
        module.setDrawn(moduleObject["Drawn"].asBoolean)
        module.setKey(moduleObject["Bind"].asInt, -1)

        inputStream.close()
    }

    private fun loadHudComponentDirect(hudComponent: HUDComponent) {
        val path = Paths.get("$mainDirectory$hudDirectory${hudComponent.name}.json")

        if (!Files.exists(path)) {
            return
        }

        val inputStream: InputStream = Files.newInputStream(path)
        val jsonObject: JsonObject = JsonParser().parse(InputStreamReader(inputStream)).asJsonObject

        hudComponent.x = jsonObject["X"].asDouble
        hudComponent.y = jsonObject["Y"].asDouble
        hudComponent.enabled = jsonObject["Enabled"].asBoolean

        inputStream.close()
    }
}