package me.remainingtoast.toastclient.client.module.render

import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.type.EnumSetting
import me.remainingtoast.toastclient.api.util.CapeUtil
import me.remainingtoast.toastclient.api.util.lit
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Formatting

object Capes : Module("Capes", Category.RENDER) {

    enum class CapeType {
        OLD_MOJANG,
        MINECON2013,
        MINECON2016
    }

    var capeType: EnumSetting<CapeType> = registerEnum(CapeType.OLD_MOJANG, "Type", "Type of Cape")

    override fun onEnable() {
        CapeUtil.getCapes()
        val player = MinecraftClient.getInstance().player!!
        if(!CapeUtil.hasCape(player.uuid)){
            disable()
            player.sendMessage(lit("You need to donate to gain access to Capes!").formatted(Formatting.RED), false)
        }
    }

}

