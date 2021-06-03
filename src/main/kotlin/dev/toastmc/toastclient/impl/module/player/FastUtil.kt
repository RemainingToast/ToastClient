package dev.toastmc.toastclient.impl.module.player

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.mixin.client.IMinecraftClient
import net.minecraft.item.Items

object FastUtil : Module("FastUtil", Category.PLAYER) {

    var fastExp = bool("EXP Bottles", true)
    var fastCrystal = bool("Crystals", true)
    var fastBreak =  bool("Break", true)
    var fastPlace = bool("Place", true)

    override fun onUpdate() {
        if ((mc.player!!.mainHandStack.item.equals(Items.EXPERIENCE_BOTTLE)
            || mc.player!!.offHandStack.item.equals(Items.EXPERIENCE_BOTTLE))
            && fastExp.value
        ) {
            (mc as IMinecraftClient).setItemUseCooldown(0)
        }

        if ((mc.player!!.mainHandStack.item.equals(Items.END_CRYSTAL)
            || mc.player!!.offHandStack.item.equals(Items.END_CRYSTAL))
            && fastCrystal.value
        ) {
                (mc as IMinecraftClient).setItemUseCooldown(0)
        }

        if (fastBreak.value || fastPlace.value) (mc as IMinecraftClient).setItemUseCooldown(0)
    }

}