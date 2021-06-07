package dev.toastmc.toastclient.api.util.render

import dev.toastmc.toastclient.api.util.ToastColor
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.mc
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.opengl.GL11
import java.awt.Rectangle

object DrawableUtil : DrawableExtensions {

    fun drawRect(matrices: MatrixStack, rect: Rectangle, color: ToastColor) {
        startSmooth()
        fill(matrices, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, color)
        endSmooth()
    }

    fun drawRect(matrices: MatrixStack, x: Int, y: Int, width: Int, height: Int, color: ToastColor) {
        drawRect(matrices, Rectangle(x, y, width, height), color)
    }

    fun drawCenteredTextBox(matrices: MatrixStack, text: String, rect: Rectangle, bgColor: ToastColor, textColor: ToastColor, textScale: Float) {
        drawRect(matrices, rect.x - 2, rect.y - 2, rect.width, rect.height, bgColor)
        drawCenteredText(matrices, mc.textRenderer, lit(text), rect.centerX.toInt(), rect.y, textColor, textScale)
    }

    fun drawTextBox(matrices: MatrixStack, text: String, rect: Rectangle, bgColor: ToastColor, textColor: ToastColor, textScale: Float) {
        drawRect(matrices, rect.x - 2, rect.y - 2, rect.width, rect.height, bgColor)
        drawText(matrices, mc.textRenderer, lit(text), rect.x, rect.y, textColor, textScale)
    }

    fun drawHollowRect(matrices: MatrixStack, x: Int, y: Int, width: Int, height: Int, lineWidth: Int, color: ToastColor) {
        drawRect(matrices, x - lineWidth, y - lineWidth, width + lineWidth * 2, lineWidth, color) // top line
        drawRect(matrices, x - lineWidth, y, lineWidth, height, color) // left line
        drawRect(matrices, x - lineWidth, y + height, width + lineWidth * 2, lineWidth, color) // bottom line
        drawRect(matrices, x + width, y, lineWidth, height, color) // right line
    }

}