package dev.toastmc.toastclient.impl.module.render

import dev.toastmc.toastclient.api.events.WorldRenderEvent
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.ToastColor
import dev.toastmc.toastclient.api.util.WorldUtil
import dev.toastmc.toastclient.api.util.WorldUtil.isHole
import dev.toastmc.toastclient.api.util.render.RenderUtil
import org.quantumclient.energy.Subscribe

// TODO: Double/Quad Holes
// TODO: WorldUtil#getCube is laggy as fuck. 500 fps -> 70 fps
object HoleESP : Module("HoleESP", Category.RENDER) {

    var range = number("RenderRange", 20, 5, 64, 1)
    var hideOwn = bool("HideOwnHole", true)

    @Subscribe
    fun on(event: WorldRenderEvent) {
        WorldUtil.getCube(mc.player!!.pos, range.value.toInt()).filter {
            it.isHole(true)
        }.filter {
            !hideOwn.value || !mc.player!!.blockPos.equals(it)
        }.forEach {
            RenderUtil.drawHollowBox(it, ToastColor.rainbow(1))
        }
    }
}