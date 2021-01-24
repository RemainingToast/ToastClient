package me.remainingtoast.toastclient.client.module.client

import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.type.EnumSetting
import net.minecraft.util.Identifier

object Font : Module("Font", Category.CLIENT, true) {

    enum class FontType {
        MINECRAFT,
        ROBOTO
    }

    var fontType: EnumSetting<FontType> = registerEnum(FontType.MINECRAFT, "Style", "Style of Font")

    fun getFontFromType(type: FontType): Identifier{
        return when (type){
            FontType.MINECRAFT -> {
                Identifier("minecraft", "default")
            }
            FontType.ROBOTO -> {
                Identifier("toastclient", "roboto")
            }
        }
    }
}