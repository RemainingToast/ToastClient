package dev.toastmc.toastclient.api.util.render

import com.mojang.blaze3d.systems.RenderSystem
import dev.toastmc.toastclient.api.util.lit
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import org.lwjgl.opengl.GL11

interface DrawableExtensions {

    fun line(matrices: MatrixStack, x0: Float, x1: Float, y0: Float, y1: Float, color: Int) {
        val matrix = matrices.peek().model
        val a = (color shr 24 and 255).toFloat() / 255.0f
        val r = (color shr 16 and 255).toFloat() / 255.0f
        val g = (color shr 8 and 255).toFloat() / 255.0f
        val b = (color and 255).toFloat() / 255.0f
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        bufferBuilder.begin(1, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x0, y0, 0f).color(r, g, b, a).next()
        bufferBuilder.vertex(matrix, x1, y1, 0f).color(r, g, b, a).next()
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun drawCenteredText(
        matrices: MatrixStack,
        textRenderer: TextRenderer,
        text: Text,
        centerX: Int,
        y: Int,
        color: Int,
        scale: Float,
    ) {
        matrices.push()
        matrices.scale(scale, scale, 1f)
        matrices.translate((centerX / scale).toDouble(), ((y + textRenderer.fontHeight / 2f) / scale).toDouble(), 0.0)
        DrawableHelper.drawCenteredText(matrices, textRenderer, text, 0, -textRenderer.fontHeight / 2, color)
        matrices.pop()
    }

    fun drawYCenteredText(
        matrices: MatrixStack,
        textRenderer: TextRenderer,
        text: Text,
        x: Int,
        centerY: Int,
        color: Int,
        scale: Float,
    ) {
        matrices.push()
        matrices.scale(scale, scale, 1f)
        matrices.translate((x / scale).toDouble(), ((centerY + textRenderer.fontHeight / 2f) / scale).toDouble(), 0.0)
        textRenderer.drawWithShadow(matrices, text, 0f, -textRenderer.fontHeight / 2f, color)
        matrices.pop()
    }

    fun drawText(
        matrices: MatrixStack,
        textRenderer: TextRenderer,
        text: Text,
        x: Int,
        y: Int,
        color: Int,
        scale: Float
    ) {
        matrices.push()
        matrices.scale(scale, scale, 1f)
        matrices.translate((x / scale).toDouble(), ((y + textRenderer.fontHeight / 2f) / scale).toDouble(), 0.0)
        textRenderer.drawWithShadow(matrices, text, 0f, -textRenderer.fontHeight / 2f, color)
        matrices.pop()
    }

    fun drawText(
        matrices: MatrixStack,
        textRenderer: TextRenderer,
        text: String,
        x: Int,
        y: Int,
        color: Int,
        scale: Float,
    ) {
        drawText(matrices, textRenderer, lit(text), x, y, color, scale)
    }

    fun fill(matrices: MatrixStack, x1: Int, y1: Int, x2: Int, y2: Int, color: Int, opacity: Float) {
        var x1 = x1
        var y1 = y1
        var x2 = x2
        var y2 = y2
        val matrix = matrices.peek().model
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
        val r = (color shr 16 and 255).toFloat() / 255.0f
        val g = (color shr 8 and 255).toFloat() / 255.0f
        val b = (color and 255).toFloat() / 255.0f
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x1.toFloat(), y2.toFloat(), 0.0f).color(r, g, b, opacity).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y2.toFloat(), 0.0f).color(r, g, b, opacity).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y1.toFloat(), 0.0f).color(r, g, b, opacity).next()
        bufferBuilder.vertex(matrix, x1.toFloat(), y1.toFloat(), 0.0f).color(r, g, b, opacity).next()
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
        val f = (colorStart shr 24 and 255).toFloat() / 255.0f
        val g = (colorStart shr 16 and 255).toFloat() / 255.0f
        val h = (colorStart shr 8 and 255).toFloat() / 255.0f
        val i = (colorStart and 255).toFloat() / 255.0f
        val j = (colorEnd shr 24 and 255).toFloat() / 255.0f
        val k = (colorEnd shr 16 and 255).toFloat() / 255.0f
        val l = (colorEnd shr 8 and 255).toFloat() / 255.0f
        val m = (colorEnd and 255).toFloat() / 255.0f
        RenderSystem.disableTexture()
        RenderSystem.enableBlend()
        RenderSystem.disableAlphaTest()
        RenderSystem.defaultBlendFunc()
        RenderSystem.shadeModel(7425)
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        val matrix = matrices.peek().model
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, xEnd.toFloat(), yStart.toFloat(), 0f).color(g, h, i, f).next()
        bufferBuilder.vertex(matrix, xStart.toFloat(), yStart.toFloat(), 0f).color(g, h, i, f).next()
        bufferBuilder.vertex(matrix, xStart.toFloat(), yEnd.toFloat(), 0f).color(k, l, m, j).next()
        bufferBuilder.vertex(matrix, xEnd.toFloat(), yEnd.toFloat(), 0f).color(k, l, m, j).next()
        tessellator.draw()
        RenderSystem.shadeModel(7424)
        RenderSystem.disableBlend()
        RenderSystem.enableAlphaTest()
        RenderSystem.enableTexture()
    }

    fun darken(color: Int, amount: Double): Int {
        val a = ((color and -0x1000000) * amount).toInt()
        val r = ((color and 0x00FF0000) * amount).toInt()
        val g = ((color and 0x0000FF00) * amount).toInt()
        val b = ((color and 0x000000FF) * amount).toInt()
        return a or r or g or b
    }
}