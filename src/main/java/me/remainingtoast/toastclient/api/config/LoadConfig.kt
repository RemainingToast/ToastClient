package me.remainingtoast.toastclient.api.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.module.ModuleManager.modules
import me.remainingtoast.toastclient.api.setting.SettingManager
import me.remainingtoast.toastclient.api.setting.Type
import me.remainingtoast.toastclient.api.setting.type.*
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths


/**
 * Original @author Hoosiers
 * @since 10/15/2020
 * Rewritten into Fabric/Kotlin @author RemainingToast
 * @since 01/02/2021
 * @see https://github.com/IUDevman/gamesense-client/blob/master/src/main/java/com/gamesense/api/config/LoadConfig.java
 **/
object LoadConfig {

    val mainDirectory = "toastclient/"
    val moduleDirectory = "modules/"

    fun init(){
        try {
            loadConfig()
        } catch (e: IOException){

        }
    }

    fun loadConfig(){
        loadModules()

    }

    fun loadModules() {
        val moduleLocation: String = mainDirectory + moduleDirectory
        for (module in modules) {
            try {
                loadModuleDirect(moduleLocation, module)
            } catch (e: IOException) {
                println(module.name)
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    fun loadModuleDirect(moduleLocation: String, module: Module) {
        if (!Files.exists(Paths.get(moduleLocation + module.name + ".json"))) {
            return
        }
        val inputStream: InputStream = Files.newInputStream(Paths.get(moduleLocation + module.name + ".json"))
        val moduleObject: JsonObject = JsonParser().parse(InputStreamReader(inputStream)).asJsonObject
        if (moduleObject["Module"] == null) {
            return
        }
        val settingObject = moduleObject["Settings"].asJsonObject
        for (setting in SettingManager.getSettingsForModule(module)) {
            val dataObject = settingObject[setting.configName]
            if (dataObject != null && dataObject.isJsonPrimitive) {
                when (setting.getType()) {
                    Type.BOOLEAN -> (setting as BooleanSetting).value = dataObject.asBoolean
                    Type.INTEGER -> (setting as IntegerSetting).value = dataObject.asInt
                    Type.DOUBLE -> (setting as DoubleSetting).value = dataObject.asDouble
                    Type.COLOR -> (setting as ColorSetting).fromInteger(dataObject.asInt)
                    Type.ENUM -> (setting as EnumSetting).run {
                        if(value.toString() != dataObject.asString) this.increment()
                    }
                }
            }
        }
        if(moduleObject["Enabled"].asBoolean) module.enable()
        module.setDrawn(moduleObject["Drawn"].asBoolean)
        module.setBind(moduleObject["Bind"].asInt)
        inputStream.close()
    }
}