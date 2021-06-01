package dev.toastmc.toastclient.api.util

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.Matrix4f
import org.lwjgl.opengl.GL11
import java.awt.Rectangle


object TwoDRenderUtil {

    @JvmStatic
    var zOffset = 1

    fun drawHorizontalLine(matrices: MatrixStack, x1: Int, x2: Int, y: Int, color: Int) {
        var x1 = x1
        var x2 = x2
        if (x2 < x1) {
            val i = x1
            x1 = x2
            x2 = i
        }
        fill(matrices, x1, y, x2 + 1, y + 1, color)
    }

    fun drawVerticalLine(matrices: MatrixStack, x: Int, y1: Int, y2: Int, color: Int) {
        var y1 = y1
        var y2 = y2
        if (y2 < y1) {
            val i = y1
            y1 = y2
            y2 = i
        }
        fill(matrices, x, y1 + 1, x + 1, y2, color)
    }

    fun fill(matrices: MatrixStack, x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
        fill(matrices.peek().model, x1, y1, x2, y2, color)
    }

    private fun fill(matrix: Matrix4f, x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
        var x1 = x1
        var y1 = y1
        var x2 = x2
        var y2 = y2
        var j: Int
        if (x1 < x2) {
            j = x1
            x1 = x2
            x2 = j
        }
        if (y1 < y2) {
            j = y1
            y1 = y2
            y2 = j
        }
        val f = (color shr 24 and 255).toFloat() / 255.0f
        val g = (color shr 16 and 255).toFloat() / 255.0f
        val h = (color shr 8 and 255).toFloat() / 255.0f
        val k = (color and 255).toFloat() / 255.0f
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x1.toFloat(), y2.toFloat(), zOffset.toFloat()).color(g, h, k, f).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y2.toFloat(), zOffset.toFloat()).color(g, h, k, f).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y1.toFloat(), zOffset.toFloat()).color(g, h, k, f).next()
        bufferBuilder.vertex(matrix, x1.toFloat(), y1.toFloat(), zOffset.toFloat()).color(g, h, k, f).next()
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun fillGradient(
        matrices: MatrixStack,
        xStart: Int,
        yStart: Int,
        xEnd: Int,
        yEnd: Int,
        colorStart: Int,
        colorEnd: Int,
    ) {
        RenderSystem.disableTexture()
        RenderSystem.enableBlend()
        RenderSystem.disableAlphaTest()
        RenderSystem.defaultBlendFunc()
        RenderSystem.shadeModel(7425)
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR)
        fillGradient(matrices.peek().model, bufferBuilder, xStart, yStart, xEnd, yEnd, zOffset, colorStart, colorEnd)
        tessellator.draw()
        RenderSystem.shadeModel(7424)
        RenderSystem.disableBlend()
        RenderSystem.enableAlphaTest()
        RenderSystem.enableTexture()
    }

    private fun fillGradient(
        matrix: Matrix4f,
        bufferBuilder: BufferBuilder,
        xStart: Int,
        yStart: Int,
        xEnd: Int,
        yEnd: Int,
        z: Int,
        colorStart: Int,
        colorEnd: Int,
    ) {
        val f = (colorStart shr 24 and 255).toFloat() / 255.0f
        val g = (colorStart shr 16 and 255).toFloat() / 255.0f
        val h = (colorStart shr 8 and 255).toFloat() / 255.0f
        val i = (colorStart and 255).toFloat() / 255.0f
        val j = (colorEnd shr 24 and 255).toFloat() / 255.0f
        val k = (colorEnd shr 16 and 255).toFloat() / 255.0f
        val l = (colorEnd shr 8 and 255).toFloat() / 255.0f
        val m = (colorEnd and 255).toFloat() / 255.0f
        bufferBuilder.vertex(matrix, xEnd.toFloat(), yStart.toFloat(), z.toFloat()).color(g, h, i, f).next()
        bufferBuilder.vertex(matrix, xStart.toFloat(), yStart.toFloat(), z.toFloat()).color(g, h, i, f).next()
        bufferBuilder.vertex(matrix, xStart.toFloat(), yEnd.toFloat(), z.toFloat()).color(k, l, m, j).next()
        bufferBuilder.vertex(matrix, xEnd.toFloat(), yEnd.toFloat(), z.toFloat()).color(k, l, m, j).next()
    }

    fun drawRect(matrices: MatrixStack, rect: Rectangle, color: Int) {
        fill(matrices, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, color)
    }

    fun drawRect(matrices: MatrixStack, x: Int, y: Int, width: Int, height: Int, color: Int) {
        drawRect(matrices, Rectangle(x, y, width, height), color)
    }

    fun drawHollowRect(matrices: MatrixStack, x: Int, y: Int, width: Int, height: Int, lineWidth: Int, color: Int) {
        drawRect(matrices, x - lineWidth, y - lineWidth, width + lineWidth * 2, lineWidth, color) // top line
        drawRect(matrices, x - lineWidth, y, lineWidth, height, color) // left line
        drawRect(matrices, x - lineWidth, y + height, width + lineWidth * 2, lineWidth, color) // bottom line
        drawRect(matrices, x + width, y, lineWidth, height, color) // right line
    }

    fun drawText(matrices: MatrixStack, text: Text, x: Int, y: Int, color: Int) {
        mc.textRenderer.drawWithShadow(
            matrices,
            text,
            x.toFloat(),
            y.toFloat(),
            color
        )
        matrices.push()
        matrices.pop()
    }

    fun drawCenteredText(matrices: MatrixStack, text: Text, centerX: Int, y: Int, color: Int) {
        mc.textRenderer.drawWithShadow(
            matrices,
            text,
            centerX - mc.textRenderer.getWidth(text) / 2.toFloat(),
            y.toFloat(),
            color
        )
        matrices.push()
        matrices.pop()
    }

    fun drawCenteredYText(matrices: MatrixStack, text: Text, x: Int, centerY: Int, color: Int) {
        mc.textRenderer.drawWithShadow(
            matrices,
            text,
            x.toFloat(),
            centerY - mc.textRenderer.fontHeight / 2f,
            color
        )
        matrices.push()
        matrices.pop()
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
        drawText(matrices, lit(text), rect.x, rect.y, textColor)
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