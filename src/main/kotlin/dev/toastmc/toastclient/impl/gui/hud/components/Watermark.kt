package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.render.DrawableUtil
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.roundToInt

object Watermark : HUDComponent("Watermark") {

    init {
        width = mc.textRenderer.getWidth(lit(nameVersion))
        height = mc.textRenderer.fontHeight
        val point = getSnapPoint(SnapPoint.TOP_RIGHT, width, height)
        this.x = point.x.toDouble()
        this.y = point.y.toDouble()
    }

    override fun render(matrices: MatrixStack) {
        val text = lit(nameVersion)
        DrawableUtil.drawText(
            matrices,
            text,
            getTextPositionWithOffset(text),
            y.roundToInt(),
            ClickGUI.FONT_COLOR,
            1f
        )
    }

}