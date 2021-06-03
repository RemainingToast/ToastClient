package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.entity.totemCount
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.render.DrawableUtil
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

        DrawableUtil.drawText(
            matrices,
            mc.textRenderer,
            text,
            x.roundToInt(),
            y.roundToInt(),
            -1,
            1f
        )
    }

}