package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.TwoDRenderUtil
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.roundToInt

object Watermark : HUDComponent("Watermark", 5.0, 5.0) {

    init {
        width = mc.textRenderer.getWidth(lit(clientName))
        height = mc.textRenderer.fontHeight
    }

    override fun render(matrices: MatrixStack) {
        TwoDRenderUtil.drawText(matrices, lit(clientName), x.roundToInt(), y.roundToInt(), -1)
    }

}