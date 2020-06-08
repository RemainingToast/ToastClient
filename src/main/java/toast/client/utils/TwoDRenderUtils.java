package toast.client.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;

import java.util.List;

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
    public static boolean isMouseOverRect(double mouseX, double mouseY, double x, double y, int width, int height) {
        boolean xOver = false;
        boolean yOver = false;
        if (mouseX >= x && mouseX <= width + x) {
            xOver = true;
        }
        if (mouseY >= y && mouseY <= height + y) {
            yOver = true;
        }
        return xOver && yOver;
    }

    /**
     * Render a nine item wide grid from a list of item stacks at a coordinate
     *
     * @param itemStacks List of item stacks to render
     * @param x          X coordinate of the top left corner of the top left item
     * @param y          Y coordinate of the top left corner of the top left item
     */
    public static void renderNineWideInvItems(List<ItemStack> itemStacks, int x, int y) {
        int startX = x;
        int u = 0;
        for (ItemStack itemStack : itemStacks) {
            if (u > 8) {
                if (!itemStack.isEmpty()) {
                    MinecraftClient.getInstance().getItemRenderer().renderGuiItem(itemStack, x + 1, y + 1);
                    MinecraftClient.getInstance().getItemRenderer().renderGuiItemOverlay(textRenderer, itemStack, x + 1, y + 1);
                }
                if (x == 17 * 8 + startX) {
                    x = startX;
                    y += 17;
                } else x += 17;
            }
            u++;
        }
    }
}
