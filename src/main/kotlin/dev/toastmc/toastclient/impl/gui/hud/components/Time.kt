package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.render.DrawableUtil
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import net.minecraft.client.util.math.MatrixStack
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object Time : HUDComponent("Time") {
    val formatter = SimpleDateFormat("HH:mm")
    val date = Date()

    init {
        width = mc.textRenderer.getWidth(lit((formatter.format(date))))
        height = mc.textRenderer.fontHeight
        val point = getSnapPoint(SnapPoint.TOP_RIGHT, width, height)
        this.x = point.x.toDouble()
        this.y = point.y.toDouble()
    }

    override fun render(matrices: MatrixStack) {
        val text = lit(formatter.format(date))
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