package dev.toastmc.toastclient.api.config

object ConfigManager {

    fun init() {
        SaveConfig.init()
        LoadConfig.init()
    }

    fun loadEverything(){
        LoadConfig.loadModules()
    }

    fun saveEverything() {
        SaveConfig.saveModules()
    }

}