package me.remainingtoast.toastclient.client.module.render

import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.Setting.ListSetting

object Capes : Module("Capes", Category.RENDER) {

    private val capeTypes = arrayListOf(
        "OLD_MOJANG",
        "MINECON2013",
        "MINECON2016"
    )

    var capeType: ListSetting = registerList("Type", "Type of Cape", capeTypes, 0)

}
