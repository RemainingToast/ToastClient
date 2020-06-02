package toast.client.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;

public class TwoDRenderUtils {
    private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    /**
     * Draws text at the given coordinates
     *
     * @param text  Text to draw
     * @param x     X coordinate of the top left corner of the text
     * @param y     Y coordinate of the top left corner of the text
     * @param color Color of the box
     */
    public static void drawText(String text, int x, int y, int color) {
        textRenderer.drawWithShadow(text, x, y, color);
        RenderSystem.pushMatrix();
        RenderSystem.popMatrix();
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
    public static void drawRect(int x, int y, int width, int height, int color) {
        InGameHud.fill(x, y, x + width, y + height, color);
        RenderSystem.pushMatrix();
        RenderSystem.popMatrix();
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
    public static void drawHollowRect(int x, int y, int width, int height, int lW, int color) {
        drawRect(x - lW, y - lW, width + lW * 2, lW, color); // top line
        drawRect(x - lW, y, lW, height, color); // left line
        drawRect(x - lW, y + height, width + lW * 2, lW, color); // bottom line
        drawRect(x + width, y, lW, height, color); // right line
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
    public static void drawTextBox(int x, int y, int width, int height, int color, int textColor, int prefixColor, int bgColor, String prefix, String text) {
        drawRect(x - 2, y - 2, width, height, bgColor);
        drawHollowRect(x - 2, y - 2, width, height, 1, color);
        drawText(prefix, x, y, prefixColor);
        drawText(text, x + textRenderer.getStringWidth(prefix), y, textColor);
    }
}
