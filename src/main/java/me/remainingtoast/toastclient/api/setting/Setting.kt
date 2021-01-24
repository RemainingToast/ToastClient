package me.remainingtoast.toastclient.api.setting

import me.remainingtoast.toastclient.api.module.Module

abstract class Setting<T>(var value: T, name: String, description: String, module: Module, private val type: Type) {

    val defaultValue: T = value
    val name: String = name
    val configName: String = name.replace(" ", "")
    val description: String = description
    val module: Module = module

    fun getType(): Type {
        return type
    }

}