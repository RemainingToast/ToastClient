package toast.client.utils

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.item.ItemStack

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
    fun drawText(text: String?, x: Int, y: Int, color: Int) {
        textRenderer.drawWithShadow(text, x.toFloat(), y.toFloat(), color)
        RenderSystem.pushMatrix()
        RenderSystem.popMatrix()
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
    fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
        InGameHud.fill(x, y, x + width, y + height, color)
        RenderSystem.pushMatrix()
        RenderSystem.popMatrix()
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
    fun drawHollowRect(x: Int, y: Int, width: Int, height: Int, lW: Int, color: Int) {
        drawRect(x - lW, y - lW, width + lW * 2, lW, color) // top line
        drawRect(x - lW, y, lW, height, color) // left line
        drawRect(x - lW, y + height, width + lW * 2, lW, color) // bottom line
        drawRect(x + width, y, lW, height, color) // right line
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
        drawRect(x - 2, y - 2, width, height, bgColor)
        drawHollowRect(x - 2, y - 2, width, height, 1, color)
        drawText(prefix, x, y, prefixColor)
        drawText(text, x + textRenderer.getStringWidth(prefix), y, textColor)
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

    /**
     * Render a nine item wide grid from a list of item stacks at a coordinate
     *
     * @param itemStacks List of item stacks to render
     * @param xStart     X coordinate of the top left corner of the top left item
     * @param yStart     Y coordinate of the top left corner of the top left item
     */
    @JvmStatic
    fun renderNineWideInvItems(itemStacks: List<ItemStack>, xStart: Int, yStart: Int) {
        var x = xStart
        var y = yStart
        for (itemStack in itemStacks) {
            if (!itemStack.isEmpty) {
                MinecraftClient.getInstance().itemRenderer.renderGuiItem(itemStack, x + 1, y + 1)
                MinecraftClient.getInstance().itemRenderer
                        .renderGuiItemOverlay(textRenderer, itemStack, x + 1, y + 1)
            }
            if (x == 17 * 8 + x) {
                x = xStart
                y += 17
            } else x += 17
        }
    }
}