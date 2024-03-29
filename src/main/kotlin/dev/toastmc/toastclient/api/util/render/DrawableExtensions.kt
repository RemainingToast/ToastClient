package dev.toastmc.toastclient.api.util.render

import com.mojang.blaze3d.systems.RenderSystem
import dev.toastmc.toastclient.api.util.ToastColor
import dev.toastmc.toastclient.api.util.mc
import dev.toastmc.toastclient.impl.module.client.Font
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import org.lwjgl.opengl.GL11
import java.awt.Rectangle
import java.math.BigDecimal
import java.math.RoundingMode

interface DrawableExtensions {

    fun line(matrices: MatrixStack, x0: Float, x1: Float, y0: Float, y1: Float, color: ToastColor) {
        val matrix = matrices.peek().positionMatrix
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
        /*RenderSystem.disableTexture()*/
        RenderSystem.defaultBlendFunc()
        bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x0, y0, 0f).color(color.red, color.green, color.blue,color.alpha).next()
        bufferBuilder.vertex(matrix, x1, y1, 0f).color(color.red, color.green, color.blue,color.alpha).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
        /*RenderSystem.enableTexture()*/
        RenderSystem.disableBlend()
    }

    fun drawCenteredText(
        matrices: MatrixStack,
        text: Text,
        centerX: Int,
        y: Int,
        color: ToastColor,
        scale: Float,
    ) {
        matrices.push()
        matrices.scale(scale, scale, 1f)
        startSmooth()
        if (Font.isEnabled()) {
            /*FontAccessor.fontRenderer.drawString(
                text.asString(),
                (centerX - FontAccessor.fontRenderer.getWidth(text.asString()) / 2f),
                (y + -FontAccessor.fontRenderer.getHeight(text.asString()) / 2f),
                color.aBGRPackedInt,
                true
            )*/

            /*FontAccessor._fontRenderer.drawString(
                matrices,
                text.asString(),
                (centerX - FontAccessor._fontRenderer.getStringWidth(text.asString()) / 2f),
                (y + - FontAccessor._fontRenderer.getStringHeight(text.asString()) / 2f),
                color.red.toFloat(),
                color.green.toFloat(),
                color.blue.toFloat(),
                color.alpha.toFloat()
            )*/
        } else {
            matrices.translate((centerX / scale).toDouble(), ((y + mc.textRenderer.fontHeight / 2f) / scale).toDouble(), 0.0)
            DrawableHelper.drawCenteredTextWithShadow(matrices, mc.textRenderer, text, 0, -mc.textRenderer.fontHeight / 2, color.aBGRPackedInt)
        }
        matrices.pop()
        endSmooth()
    }

    fun drawText(
        matrices: MatrixStack,
        text: Text,
        x: Int,
        y: Int,
        color: ToastColor,
        scale: Float,
    ) {
        drawText(matrices, text, x, y, 0.0, color, scale)
    }

    fun drawText(
        matrices: MatrixStack,
        text: Text,
        x: Int,
        y: Int,
        z: Double,
        color: ToastColor,
        scale: Float,
    ) {
        matrices.push()
        matrices.scale(scale, scale, 1f)
        startSmooth()
        if (Font.isEnabled()) {
            /*FontAccessor.fontRenderer.drawString(
                text.asString(),
                x.toFloat(),
                (y + -FontAccessor.fontRenderer.getHeight(text.asString()) / 2f),
                color.aBGRPackedInt,
                true
            )*/

            /*FontAccessor._fontRenderer.drawString(
                matrices,
                text.asString(),
                x.toFloat(),
                (y + - FontAccessor._fontRenderer.getStringHeight(text.asString()) / 2f),
                color.red.toFloat(),
                color.green.toFloat(),
                color.blue.toFloat(),
                color.alpha.toFloat()
            )*/
        } else {
            matrices.translate((x / scale).toDouble(), ((y + mc.textRenderer.fontHeight / 2f) / scale).toDouble(), z)
            mc.textRenderer.drawWithShadow(matrices, text, 0f, -mc.textRenderer.fontHeight / 2f, color.aBGRPackedInt)
        }
        matrices.pop()
        endSmooth()
    }

    fun fill(matrices: MatrixStack, x1: Int, y1: Int, x2: Int, y2: Int, color: ToastColor) {
        var x1 = x1
        var y1 = y1
        var x2 = x2
        var y2 = y2
        val matrix = matrices.peek().positionMatrix
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
        val bufferBuilder = Tessellator.getInstance().buffer
        RenderSystem.enableBlend()
//        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, x1.toFloat(), y2.toFloat(), 0.0f).color(color.red, color.green, color.blue, color.alpha).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y2.toFloat(), 0.0f).color(color.red, color.green, color.blue, color.alpha).next()
        bufferBuilder.vertex(matrix, x2.toFloat(), y1.toFloat(), 0.0f).color(color.red, color.green, color.blue, color.alpha).next()
        bufferBuilder.vertex(matrix, x1.toFloat(), y1.toFloat(), 0.0f).color(color.red, color.green, color.blue, color.alpha).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
//        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    fun fillGradient(
        matrices: MatrixStack,
        xStart: Int,
        yStart: Int,
        xEnd: Int,
        yEnd: Int,
        colorStart: ToastColor,
        colorEnd: ToastColor,
    ) {
//        RenderSystem.disableTexture()
        RenderSystem.enableBlend()
//        RenderSystem.disableAlphaTest()
        RenderSystem.defaultBlendFunc()
        RenderSystem.getShaderTexture(7425)
        val tessellator = Tessellator.getInstance()
        val bufferBuilder = tessellator.buffer
        val matrix = matrices.peek().positionMatrix
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        bufferBuilder.vertex(matrix, xEnd.toFloat(), yStart.toFloat(), 0f).color(colorStart.red, colorStart.green, colorStart.blue, colorStart.alpha).next()
        bufferBuilder.vertex(matrix, xStart.toFloat(), yStart.toFloat(), 0f).color(colorStart.red, colorStart.green, colorStart.blue, colorStart.alpha).next()
        bufferBuilder.vertex(matrix, xStart.toFloat(), yEnd.toFloat(), 0f).color(colorEnd.red, colorEnd.green, colorEnd.blue, colorEnd.alpha).next()
        bufferBuilder.vertex(matrix, xEnd.toFloat(), yEnd.toFloat(), 0f).color(colorEnd.red, colorEnd.green, colorEnd.blue, colorEnd.alpha).next()
        tessellator.draw()
        RenderSystem.getShaderTexture(7424)
        RenderSystem.disableBlend()
//        RenderSystem.enableAlphaTest()
//        RenderSystem.enableTexture()
    }

    fun darken(color: ToastColor, amount: Double): ToastColor {
        return ToastColor(
            ((color.red * amount).toInt()).coerceAtLeast(0),
            ((color.green * amount).toInt()).coerceAtLeast(0),
            ((color.blue * amount).toInt()).coerceAtLeast(0),
            ((color.alpha * amount).toInt()).coerceAtLeast(0)
        )
    }

    fun round(value: Double, places: Int): Double {
        return BigDecimal(value).setScale(places, RoundingMode.HALF_UP).toDouble()
    }

    fun clamp(value: Double, min: Double, max: Double): Double {
        return min.coerceAtLeast(max.coerceAtMost(value))
    }

    fun hover(mouseX: Double, mouseY: Double, rect: Rectangle): Boolean {
        return mouseX >= rect.x && mouseX <= rect.width + rect.x && mouseY >= rect.y && mouseY <= rect.height + rect.y
    }

    fun iteration(rect: Rectangle, iteration: Int): Rectangle {
        return Rectangle(
            rect.x + 1,
            rect.y + iteration + rect.height * iteration,
            rect.width - 2,
            rect.height
        )
    }

    fun startSmooth() {
//        GL11.glEnable(GL11.GL_LINE_SMOOTH)
//        GL11.glEnable(GL11.GL_POLYGON_SMOOTH)
//        GL11.glEnable(GL11.GL_POINT_SMOOTH)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST)
    }

    fun endSmooth() {
//        GL11.glDisable(GL11.GL_LINE_SMOOTH)
//        GL11.glDisable(GL11.GL_POLYGON_SMOOTH)
//        GL11.glDisable(GL11.GL_POINT_SMOOTH)
    }

}