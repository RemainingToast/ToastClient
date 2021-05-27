package dev.toastmc.toastclient.impl.module.render

import dev.toastmc.toastclient.api.module.Module

object Capes : Module("Capes", Category.RENDER) {

    private val capeTypes = arrayListOf(
        "OLD_MOJANG",
        "MINECON2013",
        "MINECON2016"
    )

    var capeType = mode("Type", "OLD_MOJANG", capeTypes)

}
