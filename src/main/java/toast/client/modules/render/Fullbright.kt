package toast.client.modules.render

import com.google.common.eventbus.Subscribe
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import toast.client.events.player.EventRender
import toast.client.modules.Module
import java.util.*

class Fullbright : Module("Fullbright", "Night Vision, allows you to see in the dark.", Category.RENDER, -1) {
    @Subscribe
    fun onTick(event: EventRender?) {
        if (mc.player == null) return  // avoid excessive logs and client crashing
        when (mode) {
            "Gamma" -> {
                if (increasedGamma) return
                if (lastMode != null) {
                    if (lastMode == "Potion") {
                        assert(mc.player != null)
                        if (Objects.requireNonNull(mc.player!!.getStatusEffect(StatusEffects.NIGHT_VISION))!!.amplifier == 69) mc.player!!.removeStatusEffect(StatusEffects.NIGHT_VISION)
                    }
                } else {
                    lastMode = this.mode
                }
                if (mc.options.gamma < 16) increaseGamma()
                lastMode = "Gamma"
            }
            "Potion" -> {
                assert(mc.player != null)
                mc.player!!.addStatusEffect(StatusEffectInstance(StatusEffects.NIGHT_VISION, 82820, 42069))
                lastMode = "Potion"
            }
        }
    }

    override fun onDisable() {
        if (mc.player == null) return
        mc.options.gamma = previousGamma
        increasedGamma = false
        if (mc.player!!.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
            if (Objects.requireNonNull(mc.player!!.getStatusEffect(StatusEffects.NIGHT_VISION))!!.amplifier == 42069) mc.player!!.removeStatusEffect(StatusEffects.NIGHT_VISION)
        }
    }

    override fun onEnable() {
        if (mc.player == null) return
        previousGamma = mc.options.gamma
    }

    private fun increaseGamma() {
        mc.options.gamma = 1000.0
        increasedGamma = true
    }

    companion object {
        private var increasedGamma = false
        private var previousGamma = 0.0
        private var lastMode: String? = null
    }

    init {
        settings.addMode("Mode", "Gamma", "Potion", "Gamma")
    }
}