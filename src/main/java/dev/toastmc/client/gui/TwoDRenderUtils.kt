package dev.toastmc.client.gui

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.util.math.MatrixStack

/**
 * A set of utilities for drawing things on screen on a 2D plane
 */
object TwoDRenderUtils {
    private val textRenderer = MinecraftClient.getInstance().textRenderer

    /**
     * Draws text at the given coordinates
     *
     * @param text  Text to draw
     * @param x     X coordinate of the top left corner of the text
     * @param y     Y coordinate of the top left corner of the text
     * @param color Color of the box
     */
    @JvmStatic
    fun drawText(matrixStack: MatrixStack, text: String?, x: Int, y: Int, color: Int) {
        textRenderer.drawWithShadow(matrixStack, text, x.toFloat(), y.toFloat(), color)
        matrixStack.push()
        matrixStack.pop()
    }

    /**
     * Draws a rectangle at the given coordinates
     *
     * @param x      X coordinate of the top left corner of the rectangle
     * @param y      Y coordinate of the top left corner of the rectangle
     * @param width  Width of the box
     * @param height Height of the box
     * @param color  Color of the box
     */
    @JvmStatic
    fun drawRect(matrixStack: MatrixStack, x: Int, y: Int, width: Int, height: Int, color: Int) {
        InGameHud.fill(matrixStack, x, y, x + width, y + height, color)
        matrixStack.push()
        matrixStack.pop()
    }

    /**
     * Draws a box at the given coordinates
     *
     * @param x      X coordinate of the top left corner of the inside of the box
     * @param y      Y coordinate of the top left corner of the inside of the box
     * @param width  Width of the inside of the box
     * @param height Height of the inside of the box
     * @param lW     Width of the lines of the box
     * @param color  Color of the box
     */
    @JvmStatic
    fun drawHollowRect(matrixStack: MatrixStack, x: Int, y: Int, width: Int, height: Int, lW: Int, color: Int) {
        drawRect(matrixStack, x - lW, y - lW, width + lW * 2, lW, color) // top line
        drawRect(matrixStack, x - lW, y, lW, height, color) // left line
        drawRect(matrixStack, x - lW, y + height, width + lW * 2, lW, color) // bottom line
        drawRect(matrixStack, x + width, y, lW, height, color) // right line
    }

    /**
     * Draw a text box at the given coordinates
     *
     * @param x           X coordinate of the top left corner of the inside of the text box
     * @param y           Y coordinate of the top left corner of the inside of the text box
     * @param width       Width of the inside of the box
     * @param height      Height of the inside of the box
     * @param color       Color of the box outlines
     * @param textColor   Color of the text
     * @param prefixColor Color of the text prefix
     * @param bgColor     Color of the box's background
     * @param prefix      Text to prepend to the main text
     * @param text        Main text to put in the box
     */
    @JvmStatic
    fun drawTextBox(
        matrixStack: MatrixStack,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        color: Int,
        textColor: Int,
        prefixColor: Int,
        bgColor: Int,
        prefix: String?,
        text: String?
    ) {
        drawRect(matrixStack, x - 2, y - 2, width, height, bgColor)
        drawHollowRect(matrixStack, x - 2, y - 2, width, height, 1, color)
        drawText(matrixStack, prefix, x, y, prefixColor)
        drawText(matrixStack, text, x + textRenderer.getWidth(prefix), y, textColor)
    }

    /**
     * Check if the mouse if over a box on screen
     *
     * @param mouseX Current X coordinate of the mouse
     * @param mouseY Current Y coordinate of the mouse
     * @param x      X coordinate of the top left corner of the inside of the text box
     * @param y      Y coordinate of the top left corner of the inside of the text box
     * @param width  Width of the inside of the box
     * @param height Height of the inside of the box
     */
    @JvmStatic
    fun isMouseOverRect(
        mouseX: Double,
        mouseY: Double,
        x: Double,
        y: Double,
        width: Int,
        height: Int
    ): Boolean {
        var xOver = false
        var yOver = false
        if (mouseX >= x && mouseX <= width + x) {
            xOver = true
        }
        if (mouseY >= y && mouseY <= height + y) {
            yOver = true
        }
        return xOver && yOver
    }
}