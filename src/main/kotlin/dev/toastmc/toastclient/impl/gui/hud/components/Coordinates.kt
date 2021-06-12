package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.getStringWidth
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.render.DrawableUtil
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import net.minecraft.client.util.math.MatrixStack
import java.text.DecimalFormat
import kotlin.math.roundToInt

object Coordinates : HUDComponent("Coordinates") {

    var formatter: DecimalFormat = DecimalFormat("0.#")

    init {
        val point = getSnapPoint(SnapPoint.BOTTOM_LEFT, width, height)
        this.x = point.x.toDouble()
        this.y = point.y.toDouble()
    }

    override fun render(matrices: MatrixStack) {
        val text = when {
            mc.world!!.registryKey.value.path.equals("the_nether") -> {
                lit(formatter.format(mc.player!!.x) + ", " + formatter.format(mc.player!!.y) + ", " + formatter.format(mc.player!!.z) + " [" + formatter.format(mc.player!!.x * 8) + ", " + formatter.format(mc.player!!.y) + ", " + formatter.format(mc.player!!.z * 8) + "]")
            }
            mc.world!!.registryKey.value.path.equals("overworld") -> {
                lit(formatter.format(mc.player!!.x) + ", " + formatter.format(mc.player!!.y) + ", " + formatter.format(mc.player!!.z) + " [" + formatter.format(mc.player!!.x / 8) + ", " + formatter.format(mc.player!!.y) + ", " + formatter.format(mc.player!!.z / 8) + "]")
            }
            else -> {
                lit(formatter.format(mc.player!!.x) + ", " + formatter.format(mc.player!!.y) + ", " + formatter.format(mc.player!!.z))
            }
        }

        width = getStringWidth(text.asString())
        height = mc.textRenderer.fontHeight

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