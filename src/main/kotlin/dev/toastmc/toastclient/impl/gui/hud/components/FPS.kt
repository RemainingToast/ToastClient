package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.render.DrawableUtil
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import net.fabricmc.fabric.mixin.networking.accessor.MinecraftClientAccessor
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import kotlin.math.roundToInt


object FPS : HUDComponent("FPS") {

    private val ticks = FloatArray(20)

    init {
        width = mc.textRenderer.getWidth(lit(clientName))
        height = mc.textRenderer.fontHeight
        val point = getSnapPoint(SnapPoint.TOP_RIGHT, width, height)
        this.x = point.x.toDouble()
        this.y = point.y.toDouble()
    }

    override fun render(matrices: MatrixStack) {
//        DrawableUtil.drawText(
//            matrices,
//            mc.textRenderer,
//            lit("FPS: " + (mc as MinecraftClientAccessor)..toString()),
//            x.roundToInt(),
//            y.roundToInt(),
//            ClickGUI.FONT_COLOR,
//            1f
//        )
    }

}