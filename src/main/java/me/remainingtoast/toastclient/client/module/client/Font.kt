package me.remainingtoast.toastclient.client.module.client

import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.Setting.ListSetting
import net.minecraft.util.Identifier

object Font : Module("Font", Category.CLIENT, true, false) {

    private val fonts = arrayListOf (
        "MINECRAFT",
        "ROBOTO"
    )

    var fontType: ListSetting = registerList("Style", "Style of Font", fonts, 0)
}