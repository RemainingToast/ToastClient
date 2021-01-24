package me.remainingtoast.toastclient.client.module.player

import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.setting.type.BooleanSetting
import me.remainingtoast.toastclient.api.util.mc
import net.minecraft.item.PickaxeItem

object NoEntityTrace : Module("NoEntityTrace", Category.PLAYER) {

    var pickaxe: BooleanSetting = registerBoolean("Pickaxe Only", false)

    fun work(): Boolean{
        return if(pickaxe.value) mc.player!!.mainHandStack.item is PickaxeItem || mc.player!!.offHandStack.item is PickaxeItem else true
    }

}