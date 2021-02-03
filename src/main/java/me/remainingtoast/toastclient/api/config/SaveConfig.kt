package me.remainingtoast.toastclient.api.config

import kotlinx.serialization.encodeToString
import me.remainingtoast.toastclient.ToastClient
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.module.ModuleManager.modules
import me.remainingtoast.toastclient.api.setting.Setting.*
import net.minecraft.client.MinecraftClient
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


/**
 * Original @author Hoosiers
 * @since 10/15/2020
 * Rewritten into Fabric/Kotlin @author RemainingToast
 * Rewritten to use kotlinx.serialization instead of gson @author Vonr/Qther, only method logic from Hoosiers remains
 * @since 01/02/2021
 * @see https://github.com/IUDevman/gamesense-client/blob/master/src/main/java/com/gamesense/api/config/SaveConfig.java
 **/
object SaveConfig {

    val mainDirectory = "${MinecraftClient.getInstance().runDirectory.canonicalPath}/toastclient/"
    val moduleDirectory = "modules/"

    fun init() {
        try {
            if(!Files.exists(Paths.get(mainDirectory))) Files.createDirectories(Paths.get(mainDirectory))
            if(!Files.exists(Paths.get(mainDirectory + moduleDirectory))) Files.createDirectories(Paths.get(mainDirectory + moduleDirectory))
        } catch (e: IOException) {}
    }

    fun registerFiles(location: String, name: String) {
        val pathString = "$mainDirectory$location$name.json"
        if (!Files.exists(Paths.get(pathString))) {
            Files.createFile(Paths.get(pathString))
        } else {
            val file = File(pathString)
            file.delete()
            Files.createFile(Paths.get(pathString))
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
        println("Finished saving modules.")
    }

    fun registerModule(module: Module) {
        registerFiles(moduleDirectory, module.name)
        val path = "$mainDirectory$moduleDirectory${module.name}.json"
        println("Attempting to save ${module.name} to $path")
        try {
            val writer = FileWriter(File(path))
            writer.write(ToastClient.JSON.encodeToString<Module>(module))
            writer.close()
        } catch (e: IOException) {
            println("Could not save ${module.name} to $path")
            e.printStackTrace()
        }
    }


    fun saveEverything(){
        saveModules()
    }
}