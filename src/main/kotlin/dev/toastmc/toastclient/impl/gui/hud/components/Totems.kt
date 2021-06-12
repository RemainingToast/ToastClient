package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.InventoryUtil.count
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.render.DrawableUtil
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.Items
import kotlin.math.roundToInt

object Totems : HUDComponent("Totems") {

    init {
        this.width = 16
        this.height = 16
        val point = getSnapPoint(SnapPoint.BOTTOM_LEFT, width, height)
        this.x = point.x.toDouble()
        this.y = point.y.toDouble()
    }

    override fun render(matrices: MatrixStack) {
        mc.itemRenderer.renderGuiItemIcon(
            Items.TOTEM_OF_UNDYING.defaultStack,
            x.roundToInt(),
            y.roundToInt()
        )

        val text = lit(mc.player!!.count(Items.TOTEM_OF_UNDYING).toString())

        DrawableUtil.drawText(
            matrices,
            text,
            getTextPositionWithOffset(text) + width - 8,
            y.roundToInt() + height - 8,
            1000.0,
            ClickGUI.FONT_COLOR,
            1f
        )
    }

}