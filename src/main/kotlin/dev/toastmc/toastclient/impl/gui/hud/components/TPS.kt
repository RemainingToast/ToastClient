package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.render.DrawableUtil
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import kotlin.math.roundToInt

object TPS : HUDComponent("TPS") {

    private val ticks = FloatArray(20)

    init {
        width = mc.textRenderer.getWidth(lit(clientName))
        height = mc.textRenderer.fontHeight
        val point = getSnapPoint(SnapPoint.TOP_RIGHT, width, height)
        this.x = point.x.toDouble()
        this.y = point.y.toDouble()
    }

    override fun render(matrices: MatrixStack) {
        DrawableUtil.drawText(
            matrices,
            mc.textRenderer,
            lit("TPS: " + getTickRate()),
            x.roundToInt(),
            y.roundToInt(),
            ClickGUI.FONT_COLOR,
            1f
        )
    }

    fun getTickRate(): Float {
        var tickCount = 0
        var tickRate = 0.0f
        for (i in 0 until this.ticks.size) {
            val tick: Float = this.ticks.get(i)
            if (tick > 0.0f) {
                tickRate += tick
                tickCount++
            }
        }
        return MathHelper.clamp(tickRate / tickCount, 0.0f, 20.0f)
    }

}