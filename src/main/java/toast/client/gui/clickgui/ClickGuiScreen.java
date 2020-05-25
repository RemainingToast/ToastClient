package toast.client.gui.clickgui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import toast.client.dontobfuscate.ClickGuiSettings;
import toast.client.modules.Module;

import java.util.ArrayList;

;

public class ClickGuiScreen extends Screen {

    private static TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    public static ClickGuiSettings settings = new ClickGuiSettings();
    public static ArrayList<Category> categories = new ArrayList<>();

    public ClickGuiScreen() {
        super(new LiteralText("ClickGuiScreen"));
        settings.loadPositions();
        settings.loadColors();
    }

    /**
     * Draws text at the given coordinates
     *
     * @param text Text to draw
     * @param x X coordinate of the top left corner of the text
     * @param y Y coordinate of the top left corner of the text
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
     * @param x X coordinate of the top left corner of the rectangle
     * @param y Y coordinate of the top left corner of the rectangle
     * @param width Width of the box
     * @param height Height of the box
     * @param color Color of the box
     */
    public static void drawRect(int x, int y, int width, int height, int color) {
        InGameHud.fill(x, y, x + width, y + height, color);
        RenderSystem.pushMatrix();
        RenderSystem.popMatrix();
    }

    /**
     * Draws a box at the given coordinates
     *
     * @param x X coordinate of the top left corner of the inside of the box
     * @param y Y coordinate of the top left corner of the inside of the box
     * @param width Width of the inside of the box
     * @param height Height of the inside of the box
     * @param lW Width of the lines of the box
     * @param color Color of the box
     */
    public static void drawHollowRect(int x, int y, int width, int height, int lW, int color) {
        drawRect(x-lW, y-lW, width + lW*2, lW, color); // top line
        drawRect(x-lW, y, lW, height, color); // left line
        drawRect(x-lW, y + height, width + lW*2, lW, color); // bottom line
        drawRect(x + width, y, lW, height, color); // right line
    }

    /**
     * Draw a text box at the given coordinates
     *
     * @param x X coordinate of the top left corner of the inside of the text box
     * @param y Y coordinate of the top left corner of the inside of the text box
     * @param width Width of the inside of the box
     * @param height Height of the inside of the box
     * @param color Color of the box outlines
     * @param textColor Color of the text
     * @param prefixColor Color of the text prefix
     * @param bgColor Color of the box's background
     * @param prefix Text to prepend to the main text
     * @param text Main text to put in the box
     */
    public static void drawTextBox(int x, int y, int width, int height, int color, int textColor, int prefixColor, int bgColor, String prefix, String text) {
        drawRect(x-2, y-2, width, height, bgColor);
        drawHollowRect(x-2, y-2, width, height, 1, color);
        drawText(prefix, x, y, prefixColor);
        drawText(text, x + textRenderer.getStringWidth(prefix), y, textColor);
    }

    public static boolean isMouseOverRect(int mouseX, int mouseY, int x, int y, int width, int height) {
        boolean xOver = false;
        boolean yOver = false;
        if (mouseX >= x && mouseX <= width + x) {
            xOver = true;
        }
        if (mouseY >= y && mouseY <= height + y) {
            yOver = true;
        }
        if (xOver && yOver) return true;
        else return false;
    }

    public static ClickGuiSettings getSettings() {
        return settings;
    }

    private boolean mouseIsClicked = false;
    private boolean clickedOnce = false;

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        int width = 100;
        int height = MinecraftClient.getInstance().textRenderer.getStringBoundedHeight("> A", 100)+3;
        categories.clear();
        for (Module.Category category : Module.Category.values()) {
            categories.add(new Category(mouseX, mouseY, width, height, category, mouseIsClicked));
        }
        if (clickedOnce) {
            mouseIsClicked = false;
        }
        for (Category category : categories) {
            if (category.hasDesc) {
                drawTextBox(category.descPosX, category.descPosY, textRenderer.getStringWidth(settings.colors.descriptionPrefix + category.desc) + 4, height, settings.colors.descriptionBoxColor, settings.colors.descriptionTextColor, settings.colors.categoryPrefixColor, settings.colors.descriptionBgColor, settings.colors.descriptionPrefix, category.desc);
                break;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && !clickedOnce) {
            mouseIsClicked = true;
            clickedOnce = true;
        } else {
            mouseIsClicked = false;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            mouseIsClicked = false;
            clickedOnce = false;
        }
        return false;
    }

    public boolean isPauseScreen() {
        return false;
    }
}
