package dev.toastmc.toastclient.api.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.api.module.Module
import dev.toastmc.toastclient.api.module.ModuleManager
import dev.toastmc.toastclient.api.setting.Setting
import dev.toastmc.toastclient.api.setting.Setting.*
import dev.toastmc.toastclient.api.setting.SettingManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Hoosiers
**/
object SaveConfig : IToastClient {

    private val mainDirectory = "${mc.runDirectory.canonicalPath}/toastclient/"
    private const val moduleDirectory = "modules/"

    fun init() {
        try {
            if(!Files.exists(Paths.get(mainDirectory)))
                Files.createDirectories(Paths.get(mainDirectory))
            if(!Files.exists(Paths.get(mainDirectory + moduleDirectory)))
                Files.createDirectories(Paths.get(mainDirectory + moduleDirectory))
        } catch (e: IOException) {}
    }

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
        registerFiles(moduleDirectory, module.getName())
        val fileOutputStreamWriter = OutputStreamWriter(
            FileOutputStream("$mainDirectory$moduleDirectory${module.getName()}.json"),
            StandardCharsets.UTF_8
        )

        val moduleObject = JsonObject()
        val settingObject = JsonObject()

        moduleObject.add("Module", JsonPrimitive(module.getName()))

        for (s in SettingManager.getSettingsForMod(module)){
            when (s.type){
                Type.BOOLEAN -> settingObject.add(s.configName, JsonPrimitive((s as Setting.Boolean).value))
                Type.DOUBLE -> settingObject.add(s.configName, JsonPrimitive((s as Setting.Double).value))
                Type.COLOR -> settingObject.add(s.configName, JsonPrimitive((s as ColorSetting).toInteger()))
                Type.MODE -> settingObject.add(s.configName, JsonPrimitive((s as Mode).value.toString()))
            }
        }

        moduleObject.add("Settings", settingObject)
        moduleObject.add("Enabled", JsonPrimitive(module.isEnabled()))
        moduleObject.add("Drawn", JsonPrimitive(module.isDrawn()))
//        moduleObject.add("Bind", JsonPrimitive(module.key))
        val str = gson.toJson(JsonParser().parse(moduleObject.toString()))
        fileOutputStreamWriter.write(str)
        fileOutputStreamWriter.close()
    }


    private fun registerFiles(location: String, name: String) {
        if (!Files.exists(Paths.get("$mainDirectory$location$name.json"))) {
            Files.createFile(Paths.get("$mainDirectory$location$name.json"))
        } else {
            val file = File("$mainDirectory$location$name.json")
            file.delete()
            Files.createFile(Paths.get("$mainDirectory$location$name.json"))
        }
    }
}