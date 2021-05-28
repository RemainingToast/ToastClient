package dev.toastmc.toastclient.impl.module.player

import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.item.PickaxeItem

object NoEntityTrace : Module("NoEntityTrace", Category.PLAYER) {

    var pickaxe = bool("Pickaxe Only", false)

    fun work(): Boolean{
        return if(pickaxe.value) mc.player!!.mainHandStack.item is PickaxeItem || mc.player!!.offHandStack.item is PickaxeItem else true
    }

}