package dev.toastmc.toastclient.impl.module.render

import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import java.util.*

object FullBright : Module("FullBright", Category.RENDER) {

    override fun onUpdate() {
        /*increaseGamma()*/
        if (!mc.player!!.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            mc.player!!.addStatusEffect(StatusEffectInstance(StatusEffects.NIGHT_VISION, Int.MAX_VALUE, Int.MAX_VALUE))
        }
    }

    override fun onDisable() {
        /*mc.options.gamma.value = previousGamma
        increasedGamma = false*/
        if (mc.player!!.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            if (Objects.requireNonNull(mc.player!!.getStatusEffect(StatusEffects.NIGHT_VISION))!!.amplifier == Int.MAX_VALUE) {
                mc.player!!.removeStatusEffect(StatusEffects.NIGHT_VISION)
            }
        }
    }

    override fun onEnable() {
        if (mc.player == null) return
        /*previousGamma = mc.options.gamma.value*/
        mc.player!!.addStatusEffect(StatusEffectInstance(StatusEffects.NIGHT_VISION, Int.MAX_VALUE, Int.MAX_VALUE))
    }

    private fun increaseGamma() {
        /*mc.options.gamma.value = 1000.0
        increasedGamma = true*/
    }

    private var increasedGamma = false
    private var previousGamma = 0.0
}