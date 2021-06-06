package dev.toastmc.toastclient.api.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import dev.toastmc.toastclient.api.config.ConfigUtil.mainDirectory
import dev.toastmc.toastclient.api.config.ConfigUtil.moduleDirectory
import dev.toastmc.toastclient.api.managers.SettingManager
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.managers.module.ModuleManager
import dev.toastmc.toastclient.api.setting.Setting
import dev.toastmc.toastclient.api.setting.Setting.*
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * @author Hoosiers
**/
object ConfigSaver {

    fun saveModules() {
        for (module in ModuleManager.modules) {
            try {
                registerModule(module)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun registerModule(module: Module){
        ConfigUtil.registerFile(moduleDirectory + module.getName())

        val fileOutputStreamWriter = OutputStreamWriter(
            FileOutputStream("$mainDirectory$moduleDirectory${module.getName()}.json"),
            StandardCharsets.UTF_8
        )

        val moduleObject = JsonObject()
        val settingObject = JsonObject()

        moduleObject.add("Module", JsonPrimitive(module.getName()))

        for (s in SettingManager.getSettingsForMod(module)){
            when (s.type){
                Type.BOOLEAN -> settingObject.add(s.name, JsonPrimitive((s as Setting.Boolean).value))
                Type.NUMBER -> settingObject.add(s.name, JsonPrimitive((s as Setting.Number).value))
                Type.COLOR -> settingObject.add(s.name, JsonPrimitive((s as ColorSetting).toInteger()))
                Type.MODE -> settingObject.add(s.name, JsonPrimitive((s as Mode).value.toString()))
            }
        }

        moduleObject.add("Settings", settingObject)
        moduleObject.add("Enabled", JsonPrimitive(module.isEnabled()))
        moduleObject.add("Drawn", JsonPrimitive(module.isDrawn()))
//        moduleObject.add("Bind", JsonPrimitive(module.key))
        val str = ConfigUtil.gson.toJson(JsonParser().parse(moduleObject.toString()))
        fileOutputStreamWriter.write(str)
        fileOutputStreamWriter.close()
    }

}