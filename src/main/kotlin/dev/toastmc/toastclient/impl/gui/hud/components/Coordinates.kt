package dev.toastmc.toastclient.impl.gui.hud.components

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
        // TODO make the background size portable
        width = mc.textRenderer.getWidth(lit("100, 100, 100"))
        height = mc.textRenderer.fontHeight
        val point = getSnapPoint(SnapPoint.TOP_RIGHT, width, height)
        this.x = point.x.toDouble()
        this.y = point.y.toDouble()
    }

    override fun render(matrices: MatrixStack) {
        if (mc.world!!.registryKey.value.path.equals("the_nether")) {
            DrawableUtil.drawText(
                matrices,
                mc.textRenderer,
                lit(formatter.format(mc.player!!.x) + ", " + formatter.format(mc.player!!.y) + ", " + formatter.format(mc.player!!.z) + " [" + formatter.format(mc.player!!.x / 8) + ", " + formatter.format(mc.player!!.y) + ", " + formatter.format(mc.player!!.z / 8) + "]"),
                x.roundToInt(),
                y.roundToInt(),
                ClickGUI.FONT_COLOR,
                1f
            )
        } else if (mc.world!!.registryKey.value.path.equals("overworld")) {
            DrawableUtil.drawText(
                matrices,
                mc.textRenderer,
                lit(formatter.format(mc.player!!.x) + ", " + formatter.format(mc.player!!.y) + ", " + formatter.format(mc.player!!.z) + " [" + formatter.format(mc.player!!.x * 8) + ", " + formatter.format(mc.player!!.y) + ", " + formatter.format(mc.player!!.z * 8) + "]"),
                x.roundToInt(),
                y.roundToInt(),
                ClickGUI.FONT_COLOR,
                1f
            )
        } else {
            DrawableUtil.drawText(
                matrices,
                mc.textRenderer,
                lit(formatter.format(mc.player!!.x) + ", " + formatter.format(mc.player!!.y) + ", " + formatter.format(mc.player!!.z)),
                x.roundToInt(),
                y.roundToInt(),
                ClickGUI.FONT_COLOR,
                1f
            )
        }
    }

}