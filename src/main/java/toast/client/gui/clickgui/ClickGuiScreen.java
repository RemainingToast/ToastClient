package toast.client.gui.clickgui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import toast.client.dontobfuscate.ClickGuiSettings;
import toast.client.dontobfuscate.Setting;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;
import toast.client.modules.render.ClickGui;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ClickGuiScreen extends Screen {
    private static TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    public static ClickGuiSettings settings = new ClickGuiSettings();
    public static ArrayList<CategoryRenderer> categoryRenderers = new ArrayList<>();

    public static int width = 50;

    public ClickGuiScreen() {
        super(new LiteralText("ClickGuiScreen"));
        settings.loadPositions();
        settings.loadColors();
        for (Module.Category category : Module.Category.values()) {
            int catWidth = textRenderer.getStringWidth(settings.colors.categoryPrefix + category.toString());
            if (catWidth > width) {
                width = catWidth + 4;
            }
        }
        for (Module module : ModuleManager.getModules()) {
            int moduleWidth = textRenderer.getStringWidth(settings.colors.modulePrefix + module.getName());
            if (moduleWidth > width) {
                width = moduleWidth + 4;
            }
            for (Map.Entry<String, Setting> settingEntry : module.getSettings().getSettings().entrySet()) {
                int settingWidth = textRenderer.getStringWidth(settings.colors.settingPrefix + settingEntry.getKey());
                if (settingWidth > width) {
                    width = settingWidth + 4;
                }
            }
        }
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

    private boolean mouseIsClickedL = false;
    private boolean mouseIsClickedR = false;
    private boolean clickedOnce = false;

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        int height = MinecraftClient.getInstance().textRenderer.getStringBoundedHeight("> A", 100)+3;
        categoryRenderers.clear();
        for (Module.Category category : Module.Category.values()) {
            categoryRenderers.add(new CategoryRenderer(mouseX, mouseY, width, height, category, mouseIsClickedL, mouseIsClickedR));
        }
        if (clickedOnce) {
            mouseIsClickedL = false;
            mouseIsClickedR = false;
        }
        for (CategoryRenderer categoryRenderer : categoryRenderers) {
            if (categoryRenderer.hasDesc) {
                drawTextBox(categoryRenderer.descPosX, categoryRenderer.descPosY, textRenderer.getStringWidth(settings.colors.descriptionPrefix + categoryRenderer.desc) + 4, height, settings.colors.descriptionBoxColor, settings.colors.descriptionTextColor, settings.colors.categoryPrefixColor, settings.colors.descriptionBgColor, settings.colors.descriptionPrefix, categoryRenderer.desc);
                break;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!clickedOnce) {
            if (button == 0) {
                mouseIsClickedL = true;
                mouseIsClickedR = false;
                clickedOnce = true;
            } else if (button == 1) {
                mouseIsClickedL = false;
                mouseIsClickedR = true;
                clickedOnce = true;
            }
        } else {
            mouseIsClickedL = false;
            mouseIsClickedR = false;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 || button == 1) {
            mouseIsClickedL = false;
            mouseIsClickedR = false;
            clickedOnce = false;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) {
            for (CategoryRenderer categoryRenderer : categoryRenderers) {
                categoryRenderer.updatePosition(deltaX, deltaY);
            }
        }
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (CategoryRenderer categoryRenderer : categoryRenderers) {
            categoryRenderer.updateMousePos(mouseX, mouseY);
        }
    }

    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        settings.savePositions();
        settings.saveColors();
        Objects.requireNonNull(ModuleManager.getModule(ClickGui.class)).disable();
    }

    public void reloadConfig() {
        settings.loadColors();
        settings.loadPositions();
    }

    public ClickGuiSettings getSettings() {
        return settings;
    }
}
