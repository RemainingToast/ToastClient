package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.managers.module.ModuleManager
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.render.DrawableUtil
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.roundToInt

object ModuleList : HUDComponent("ModuleList") {

    init {
        val point = getSnapPoint(SnapPoint.TOP_RIGHT, width, height)
        this.x = point.x.toDouble()
        this.y = point.y.toDouble()
    }

    override fun render(matrices: MatrixStack) {
        var yOffset = 2
        height = 0
        for(module in ModuleManager.modules.sortedWith(compareBy { it.getName().length }).asReversed()) {
            if(module.isEnabled()) {
                val text = lit(module.getName())
                if(width < mc.textRenderer.getWidth(text)) width = mc.textRenderer.getWidth(text)
                DrawableUtil.drawText(
                    matrices,
                    text,
                    getTextPositionWithOffset(text),
                    y.roundToInt() + yOffset,
                    ClickGUI.FONT_COLOR,
                    1f
                )
                yOffset += mc.textRenderer.fontHeight + 2
                height += mc.textRenderer.fontHeight + 2
            }
        }
    }

}