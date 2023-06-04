package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.asString
import dev.toastmc.toastclient.api.util.getStringWidth
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.render.DrawableUtil
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import dev.toastmc.toastclient.mixin.client.IMinecraftClient
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.roundToInt

object FPS : HUDComponent("FPS") {

    init {
        val point = getSnapPoint(SnapPoint.BOTTOM_RIGHT, width, height)
        this.x = point.x.toDouble()
        this.y = point.y.toDouble()
    }

    override fun render(matrices: MatrixStack) {
        val fps = lit("FPS: ${(mc as IMinecraftClient).currentFps}")

        width = getStringWidth(fps.asString())
        height = mc.textRenderer.fontHeight

        DrawableUtil.drawText(
            matrices,
            fps,
            getTextPositionWithOffset(fps),
            y.roundToInt(),
            ClickGUI.FONT_COLOR,
            1f
        )
    }
}