package dev.toastmc.toastclient.impl.gui.hud

import dev.toastmc.toastclient.api.util.TwoDRenderUtil
import dev.toastmc.toastclient.api.util.lit
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

class HUDEditorScreen : Screen(lit("HUDEditor")) {

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        TwoDRenderUtil.drawRect(matrices, 1, 1, 1, 1, -1)
    }

}