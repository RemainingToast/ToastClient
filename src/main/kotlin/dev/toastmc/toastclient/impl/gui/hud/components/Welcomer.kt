package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.asString
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.render.DrawableUtil
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.roundToInt

object Welcomer : HUDComponent("Welcomer") {

    init {
        width = mc.textRenderer.getWidth(lit("Welcome to toast client, " + mc.player?.entityName + " :^)"))
        height = mc.textRenderer.fontHeight
        val point = getSnapPoint(SnapPoint.TOP_RIGHT, width, height)
        this.x = point.x.toDouble()
        this.y = point.y.toDouble()
    }

    override fun render(matrices: MatrixStack) {
        val text = lit("Welcome to toast client, " + mc.player!!.displayName.asString() + " :^)")
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