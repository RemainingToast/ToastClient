package me.remainingtoast.toastclient.client.module.render

import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.type.EnumSetting

object Capes : Module("Capes", Category.RENDER) {

    enum class CapeType {
        OLD_MOJANG,
        MINECON2013,
        MINECON2016
    }

    var capeType: EnumSetting<CapeType> = registerEnum(CapeType.OLD_MOJANG, "Type", "Type of Cape")

}

