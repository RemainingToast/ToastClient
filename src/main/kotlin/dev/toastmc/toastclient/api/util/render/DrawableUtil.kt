package dev.toastmc.toastclient.api.util.render

import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.mc
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.opengl.GL11
import java.awt.Rectangle

object DrawableUtil : DrawableExtensions {

    fun drawRect(matrices: MatrixStack, rect: Rectangle, color: Int) {
        startSmooth()
        fill(matrices, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, color, (color shr 24 and 255).toFloat() / 255.0f)
        endSmooth()
    }

    fun drawRect(matrices: MatrixStack, x: Int, y: Int, width: Int, height: Int, color: Int) {
        drawRect(matrices, Rectangle(x, y, width, height), color)
    }

    fun drawCenteredTextBox(matrices: MatrixStack, text: String, rect: Rectangle, bgColor: Int, textColor: Int, textScale: Float) {
        drawRect(matrices, rect.x - 2, rect.y - 2, rect.width, rect.height, bgColor)
        drawCenteredText(matrices, mc.textRenderer, lit(text), rect.centerX.toInt(), rect.y, textColor, textScale)
    }

    fun drawTextBox(matrices: MatrixStack, text: String, rect: Rectangle, bgColor: Int, textColor: Int, textScale: Float) {
        drawRect(matrices, rect.x - 2, rect.y - 2, rect.width, rect.height, bgColor)
        drawText(matrices, mc.textRenderer, lit(text), rect.x, rect.y, textColor, textScale)
    }

    fun drawHollowRect(matrices: MatrixStack, x: Int, y: Int, width: Int, height: Int, lineWidth: Int, color: Int) {
        drawRect(matrices, x - lineWidth, y - lineWidth, width + lineWidth * 2, lineWidth, color) // top line
        drawRect(matrices, x - lineWidth, y, lineWidth, height, color) // left line
        drawRect(matrices, x - lineWidth, y + height, width + lineWidth * 2, lineWidth, color) // bottom line
        drawRect(matrices, x + width, y, lineWidth, height, color) // right line
    }

    fun startSmooth() {
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH)
        GL11.glEnable(GL11.GL_POINT_SMOOTH)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST)
    }

    fun endSmooth() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH)
        GL11.glEnable(GL11.GL_POINT_SMOOTH)
    }

}