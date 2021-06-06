package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.render.DrawableUtil
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.roundToInt

object Watermark : HUDComponent("Watermark", SnapPoint.TOP_RIGHT) {

    init {
        width = mc.textRenderer.getWidth(lit(clientName))
        height = mc.textRenderer.fontHeight
    }

    override fun render(matrices: MatrixStack) {
        DrawableUtil.drawText(
            matrices,
            mc.textRenderer,
            lit(clientName),
            x.roundToInt(),
            y.roundToInt(),
            ClickGUI.FONT_COLOR,
            1f
        )
    }

}