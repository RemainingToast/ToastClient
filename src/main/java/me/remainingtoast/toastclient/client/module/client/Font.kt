package me.remainingtoast.toastclient.client.module.client

import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.Setting.ListSetting
import net.minecraft.util.Identifier

object Font : Module("Font", Category.CLIENT, true) {

    private val fonts = arrayListOf (
        "MINECRAFT",
        "ROBOTO"
    )

    var fontType: ListSetting = registerList("Style", "Style of Font", fonts, 0)

    fun getFontFromType(index: Int): Identifier{
        return when (index) {
            1 -> {
                Identifier("toastclient", "roboto")
            }
            else -> {
                Identifier("minecraft", "default")
            }
        }
    }
}