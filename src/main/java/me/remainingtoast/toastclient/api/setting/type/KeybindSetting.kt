package me.remainingtoast.toastclient.api.setting.type

import com.lukflug.panelstudio.settings.KeybindSetting
import me.remainingtoast.toastclient.api.setting.Setting
import me.remainingtoast.toastclient.api.setting.Type
import me.remainingtoast.toastclient.api.module.Module
import net.minecraft.client.util.InputUtil

import net.minecraft.text.TranslatableText


class KeybindSetting(private var key: Int, name: String, description: String, module: Module) : Setting<Int>(key, name, description, module, Type.KEYBIND), KeybindSetting {

    val defaultKey = key

    override fun getKey(): Int {
        return key
    }

    override fun setKey(key: Int) {
        this.key = key
    }

    override fun getKeyName(): String {
        return getKeyName(key)
    }

    companion object {
        fun getKeyName(key: Int): String {
            val translationKey = InputUtil.Type.KEYSYM.createFromCode(key).translationKey
            val translation = TranslatableText(translationKey).string
            return if (translation != translationKey) translation else InputUtil.Type.KEYSYM.createFromCode(key).localizedText.string
        }
    }
}