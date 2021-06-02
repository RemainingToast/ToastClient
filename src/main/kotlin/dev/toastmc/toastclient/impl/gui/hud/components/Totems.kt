package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.TwoDRenderUtil
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.totemCount
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.roundToInt

object Totems : HUDComponent("Totems", SnapPoint.BOTTOM_LEFT) {

    init {
        this.height = mc.textRenderer.fontHeight
    }

    override fun render(matrices: MatrixStack) {
        val text = lit(mc.player!!.totemCount().toString())

        this.width = mc.textRenderer.getWidth(text)

        TwoDRenderUtil.drawText(
            matrices,
            text,
            x.roundToInt(),
            y.roundToInt(),
            -1
        )
    }

}