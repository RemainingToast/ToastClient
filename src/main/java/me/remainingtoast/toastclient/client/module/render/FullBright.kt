package me.remainingtoast.toastclient.client.module.render

import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.util.mc
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import java.util.*

object FullBright : Module("FullBright", Category.RENDER) {

    override fun onUpdate() {
        if (mc.player == null) return
        increaseGamma()
    }

    override fun onDisable() {
        mc.options.gamma = previousGamma
        increasedGamma = false
        if (mc.player!!.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            if (Objects.requireNonNull(mc.player!!.getStatusEffect(StatusEffects.NIGHT_VISION))!!.amplifier == 42069) mc.player!!.removeStatusEffect(StatusEffects.NIGHT_VISION)
        }
    }

    override fun onEnable() {
        previousGamma = mc.options.gamma
        mc.player!!.addStatusEffect(StatusEffectInstance(StatusEffects.NIGHT_VISION, 42069, 42069))
    }

    private fun increaseGamma() {
        mc.options.gamma = 1000.0
        increasedGamma = true
    }

    private var increasedGamma = false
    private var previousGamma = 0.0
}