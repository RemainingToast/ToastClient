package me.remainingtoast.toastclient.api.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.SettingManager
import me.remainingtoast.toastclient.api.setting.Type
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.io.FileOutputStream

import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import me.remainingtoast.toastclient.api.setting.Setting
import me.remainingtoast.toastclient.api.setting.type.*
import java.io.IOException
import java.util.*
import me.remainingtoast.toastclient.api.module.ModuleManager
import me.remainingtoast.toastclient.api.module.ModuleManager.modules


/**
 * Original @author Hoosiers
 * @since 10/15/2020
 * Rewritten into Fabric/Kotlin @author RemainingToast
 * @since 01/02/2021
 * @see https://github.com/IUDevman/gamesense-client/blob/master/src/main/java/com/gamesense/api/config/SaveConfig.java
 **/
object SaveConfig {

    val gson = GsonBuilder().setPrettyPrinting().create()

    val mainDirectory = "toastclient/"
    val moduleDirectory = "modules/"

    fun init() {
        try {
            if(!Files.exists(Paths.get(mainDirectory))) Files.createDirectories(Paths.get(mainDirectory))
            if(!Files.exists(Paths.get(mainDirectory + moduleDirectory))) Files.createDirectories(Paths.get(mainDirectory + moduleDirectory))
        } catch (e: IOException) {}
    }

    fun registerFiles(location: String, name: String) {
        if (!Files.exists(Paths.get("$mainDirectory$location$name.json"))) {
            Files.createFile(Paths.get("$mainDirectory$location$name.json"))
        } else {
            val file = File("$mainDirectory$location$name.json")
            file.delete()
            Files.createFile(Paths.get("$mainDirectory$location$name.json"))
        }
    }

    fun saveModules() {
        for (module in modules) {
            try {
                registerModule(module)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun registerModule(module: Module){
        registerFiles(moduleDirectory, module.name)
        val fileOutputStreamWriter = OutputStreamWriter(
            FileOutputStream("$mainDirectory$moduleDirectory${module.name}.json"),
            StandardCharsets.UTF_8
        )

        val moduleObject = JsonObject()
        val settingObject = JsonObject()

        moduleObject.add("Module", JsonPrimitive(module.name))

        for (s in SettingManager.getSettingsForModule(module)){
            when (s.getType()){
                Type.BOOLEAN -> settingObject.add(s.configName, JsonPrimitive((s as BooleanSetting).value))
                Type.INTEGER -> settingObject.add(s.configName, JsonPrimitive((s as IntegerSetting).value))
                Type.DOUBLE -> settingObject.add(s.configName, JsonPrimitive((s as DoubleSetting).value))
                Type.COLOR -> settingObject.add(s.configName, JsonPrimitive((s as ColorSetting).toInteger()))
                Type.ENUM -> settingObject.add(s.configName, JsonPrimitive((s as EnumSetting).value.toString()))
            }
        }

        moduleObject.add("Settings", settingObject)
        moduleObject.add("Enabled", JsonPrimitive(module.isEnabled()))
        moduleObject.add("Drawn", JsonPrimitive(module.isDrawn()))
        moduleObject.add("Bind", JsonPrimitive(module.key))
        val str = gson.toJson(JsonParser().parse(moduleObject.toString()))
        fileOutputStreamWriter.write(str)
        fileOutputStreamWriter.close()
    }


    fun saveEverything(){
        saveModules()
    }
}