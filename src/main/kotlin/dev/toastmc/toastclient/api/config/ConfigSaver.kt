package dev.toastmc.toastclient.api.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
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
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * @author Hoosiers
**/
object ConfigSaver : IToastClient {

    fun saveModules() {
        for (module in ModuleManager.modules) {
            try {
                registerModule(module)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun saveComponents() {
        for (component in HUDEditor.SCREEN.getComponents()) {
            try {
                registerHudComponent(component)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun registerModule(module: Module) {
        ConfigUtil.registerFile(moduleDirectory + module.getName())

        val fileOutputStreamWriter = OutputStreamWriter(
            FileOutputStream("$mainDirectory$moduleDirectory${module.getName()}.json"),
            StandardCharsets.UTF_8
        )

        val moduleObject = JsonObject()
        val settingObject = JsonObject()

        moduleObject.add("Module", JsonPrimitive(module.getName()))

        for (setting in SettingManager.getSettingsForMod(module)){
            when (setting.type){
                Type.BOOLEAN -> settingObject.add(setting.name, JsonPrimitive((setting as Setting.Boolean).value))
                Type.NUMBER -> settingObject.add(setting.name, JsonPrimitive((setting as Setting.Number).value))
                Type.COLOR -> settingObject.add(setting.name, JsonPrimitive((setting as ColorSetting).toInteger()))
                Type.MODE -> settingObject.add(setting.name, JsonPrimitive((setting as Mode).value.toString()))
                Type.GROUP -> settingObject.add(setting.name, JsonPrimitive((setting as Group).isExpanded.toString()))
            }
        }

        moduleObject.add("Settings", settingObject)
        moduleObject.add("Enabled", JsonPrimitive(module.isEnabled()))
        moduleObject.add("Drawn", JsonPrimitive(module.isDrawn()))
        moduleObject.add("Bind", JsonPrimitive(module.getKey().code))

        fileOutputStreamWriter.write(gson.toJson(JsonParser().parse(moduleObject.toString())))
        fileOutputStreamWriter.close()
    }

    private fun registerHudComponent(hudComponent: HUDComponent) {
        ConfigUtil.registerFile(hudDirectory + hudComponent.name)

        val fileOutputStreamWriter = OutputStreamWriter(
            FileOutputStream("$mainDirectory$hudDirectory${hudComponent.name}.json"),
            StandardCharsets.UTF_8
        )

        val hudComponentObject = JsonObject()

        hudComponentObject.add("X", JsonPrimitive(hudComponent.x))
        hudComponentObject.add("Y", JsonPrimitive(hudComponent.y))
        hudComponentObject.add("Enabled", JsonPrimitive(hudComponent.enabled))

        fileOutputStreamWriter.write(gson.toJson(JsonParser().parse(hudComponentObject.toString())))
        fileOutputStreamWriter.close()
    }

}