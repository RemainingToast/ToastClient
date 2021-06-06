package dev.toastmc.toastclient.impl.gui.hud.components

import dev.toastmc.toastclient.api.util.InventoryUtil
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.render.DrawableUtil
import dev.toastmc.toastclient.impl.gui.hud.HUDComponent
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.Items
import kotlin.math.roundToInt

object Totems : HUDComponent("Totems", SnapPoint.BOTTOM_LEFT,16,16) {

    override fun render(matrices: MatrixStack) {
        val text = lit(InventoryUtil.itemCount(Items.TOTEM_OF_UNDYING).toString())

        mc.itemRenderer.renderGuiItemIcon(
            Items.TOTEM_OF_UNDYING.defaultStack,
            x.roundToInt(),
            y.roundToInt()
        )

        DrawableUtil.drawText(
            matrices,
            mc.textRenderer,
            text,
            x.roundToInt() + 5,
            y.roundToInt() + 5,
            -1,
            1f
        )

    }

}