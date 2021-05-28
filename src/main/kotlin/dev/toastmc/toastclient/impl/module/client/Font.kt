package dev.toastmc.toastclient.impl.module.client

import dev.toastmc.toastclient.api.events.OverlayEvent
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.ToastColor
import dev.toastmc.toastclient.api.util.TwoDRenderUtil
import dev.toastmc.toastclient.api.util.font.FontRenderer
import org.quantumclient.energy.Subscribe

object Font : Module("Font", Category.CLIENT) {

    var fontName = mode("FontName", "Minecraft", listOf("Raleway")).onChanged { update() }
    var fontSize = number("FontSize", 24.0, 5.0, 50.0).onChanged { update() }
    var dropShadow = bool("DropShadow", true).onChanged { update() }
    var watermark = bool("Watermark", true).onChanged {
        watermarkX.isHidden = !it.value
        watermarkY.isHidden = !it.value
        update()
    }

    var watermarkX = number("WatermarkX", 15.0, 0.0, 2000.0)
    var watermarkY = number("WatermarkY", 15.0, 0.0, 2000.0)

    var renderer: FontRenderer? = null

    private fun update() {
        if(mc.world == null || mc.player == null) return
        renderer = FontRenderer.create(fontName.value, fontSize.intValue, false, false, false)
    }

    @Subscribe
    fun on(event: OverlayEvent) {
        if(watermark.value) {
            if (renderer == null) {
                update()
            }

            TwoDRenderUtil.drawCustomText(
                event.matrix,
                renderer!!,
                clientName,
                watermarkX.intValue,
                watermarkY.intValue,
                dropShadow.value,
                ToastColor.rainbow()
            )

            TwoDRenderUtil.drawCustomText(
                event.matrix,
                renderer!!,
                version,
                watermarkX.intValue + renderer!!.getWidth(clientName) + 1,
                watermarkY.intValue - (renderer!!.height / 2 - 3),
                dropShadow.value,
                ToastColor.rainbow()
            )
        } else return
    }
}