package dev.toastmc.toastclient.api.util

import dev.toastmc.toastclient.api.util.font.FontRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import org.lwjgl.opengl.GL11
import java.awt.Rectangle

object TwoDRenderUtil {

    fun drawCustomText(
        matrices: MatrixStack,
        renderer: FontRenderer,
        text: String,
        x: Int,
        y: Int,
        dropShadow: Boolean,
        color: ToastColor,
    ) {
        startSmooth()
        matrices.push()
        renderer.drawString(
            text,
            x.toFloat(),
            y.toFloat(),
            color.rgb,
            dropShadow
        )
        matrices.pop()
        endSmooth()
    }

    fun drawCustomCenteredText(
        matrices: MatrixStack,
        renderer: FontRenderer,
        text: String,
        centerX: Int,
        y: Int,
        dropShadow: Boolean,
        color: ToastColor,
    ) {
        startSmooth()
        matrices.push()
        renderer.drawString(
            text,
            centerX - (renderer.getWidth(text) shr 1).toFloat(),
            y.toFloat(),
            color.rgb,
            dropShadow
        )
        matrices.pop()
        endSmooth()
    }

    fun drawText(matrices: MatrixStack, text: String, x: Int, y: Int, color: Int) {
        mc.textRenderer.drawWithShadow(
            matrices,
            text,
            x.toFloat(),
            y.toFloat(),
            color)
        matrices.push()
        matrices.pop()
    }

    fun drawCenteredText(matrices: MatrixStack, text: Text, centerX: Int, y: Int, color: Int) {
        mc.textRenderer.drawWithShadow(
            matrices,
            text,
            centerX - mc.textRenderer.getWidth(text) / 2.toFloat(),
            y.toFloat(),
            color)
    }

    /**
     * @param color
     * format 0xAARRGGBB
     */
    fun drawRect(matrices: MatrixStack, rect: Rectangle, color: Int) {
        DrawableHelper.fill(matrices, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, color)
    }

    fun drawRect(matrices: MatrixStack, x: Int, y: Int, width: Int, height: Int, color: Int) {
        DrawableHelper.fill(matrices, x, y, x + width, y + height, color)
    }

    fun drawHollowRect(matrices: MatrixStack, x: Int, y: Int, width: Int, height: Int, lineWidth: Int, color: Int) {
        drawRect(matrices, x - lineWidth, y - lineWidth, width + lineWidth * 2, lineWidth, color) // top line
        drawRect(matrices, x - lineWidth, y, lineWidth, height, color) // left line
        drawRect(matrices, x - lineWidth, y + height, width + lineWidth * 2, lineWidth, color) // bottom line
        drawRect(matrices, x + width, y, lineWidth, height, color) // right line
    }

    fun drawCenteredTextBox(matrices: MatrixStack, text: String, rect: Rectangle, bgColor: Int, textColor: Int) {
        drawRect(matrices, rect.x - 2, rect.y - 2, rect.width, rect.height, bgColor)
        drawCenteredText(
            matrices,
            lit(text),
            rect.centerX.toInt(),
            rect.y,
            textColor
        )
    }

    fun drawTextBox(matrices: MatrixStack, text: String, rect: Rectangle, bgColor: Int, textColor: Int) {
        drawRect(matrices, rect.x - 2, rect.y - 2, rect.width, rect.height, bgColor)
        drawText(matrices, text, rect.x, rect.y, textColor)
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