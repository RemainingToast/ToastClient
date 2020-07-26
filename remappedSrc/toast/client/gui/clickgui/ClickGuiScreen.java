package toast.client.gui.clickgui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;
import toast.client.modules.Module;
import toast.client.modules.config.Setting;
import toast.client.modules.render.ClickGui;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static toast.client.ToastClient.MODULE_MANAGER;
import static toast.client.ToastClient.clickGui;
import static toast.client.utils.TwoDRenderUtils.drawTextBox;

public class ClickGuiScreen extends Screen {
    private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    public static ClickGuiSettings settings = new ClickGuiSettings();
    public static Map<Module.Category, CategoryRenderer> categoryRenderers = new HashMap<>();
    public static int width = 50;
    public static int height = 10;
    public static boolean descriptions = true;
    protected static CategoryRenderer keybindPressedCategory = null;
    private boolean mouseIsClickedL = false;
    private boolean mouseIsClickedR = false;
    private boolean clickedOnce = false;

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
        for (Module module : MODULE_MANAGER.getModules()) {
            int moduleWidth = textRenderer.getStringWidth(settings.colors.modulePrefix + module.getName());
            if (moduleWidth > width) {
                width = moduleWidth + 4;
            }
            for (Map.Entry<String, Setting> settingEntry : module.getSettings().getSettings().entrySet()) {
                int settingWidth = textRenderer.getStringWidth(settings.colors.settingPrefix + settingEntry.getKey());
                if (settingWidth > width) {
                    width = settingWidth + 4;
                }
                if (settingEntry.getValue().getType() == 0) {
                    for (String mode : Objects.requireNonNull(Objects.requireNonNull(module.getSettings().getModes(settingEntry.getKey())))) {
                        int modeWidth = textRenderer.getStringWidth(settings.colors.settingPrefix + settingEntry.getKey() + ": " + mode);
                        if (modeWidth > width) {
                            width = modeWidth + 4;
                        }
                    }
                }
            }
            String keyWidth = "Keybind: " + GLFW.glfwGetKeyName(module.getKey(), GLFW.glfwGetKeyScancode(module.getKey()));
            if (keyWidth.length() > width) width = keyWidth.length();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        height = MinecraftClient.getInstance().textRenderer.getStringBoundedHeight("> A", 100) + 3;
        categoryRenderers.clear();
        for (Module.Category category : Module.Category.values()) {
            categoryRenderers.put(category, new CategoryRenderer(mouseX, mouseY, category, mouseIsClickedL, mouseIsClickedR));
        }
        if (clickedOnce) {
            mouseIsClickedL = false;
            mouseIsClickedR = false;
        }
        if (descriptions) {
            for (Map.Entry<Module.Category, CategoryRenderer> categoryRendererEntry : categoryRenderers.entrySet()) {
                CategoryRenderer categoryRenderer = categoryRendererEntry.getValue();
                if (categoryRenderer.keybindPressed) {
                    keybindPressedCategory = categoryRenderer;
                }
                if (categoryRenderer.description != null) {
                    drawTextBox(categoryRenderer.description.descPosX, categoryRenderer.description.descPosY, textRenderer.getStringWidth(settings.colors.descriptionPrefix + categoryRenderer.description.desc) + 4, height, settings.colors.descriptionBoxColor, settings.colors.descriptionTextColor, settings.colors.categoryPrefixColor, settings.colors.descriptionBgColor, settings.colors.descriptionPrefix, categoryRenderer.description.desc);
                    break;
                }
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
            for (Map.Entry<Module.Category, CategoryRenderer> categoryRendererEntry : categoryRenderers.entrySet()) {
                CategoryRenderer categoryRenderer = categoryRendererEntry.getValue();
                if (categoryRenderer.updatePosition(deltaX, deltaY)) {
                    return false;
                }
            }
        }
        return false;
    }


    public void updateMousePos(double mouseX, double mouseY) {
        for (Map.Entry<Module.Category, CategoryRenderer> categoryRendererEntry : categoryRenderers.entrySet()) {
            CategoryRenderer categoryRenderer = categoryRendererEntry.getValue();
            categoryRenderer.updateMousePos(mouseX, mouseY);
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        updateMousePos(mouseX, mouseY);
    }

    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        settings.savePositions();
        settings.saveColors();
        Objects.requireNonNull(MODULE_MANAGER.getModule(ClickGui.class)).disable();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode != GLFW.GLFW_KEY_UNKNOWN) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) clickGui.onClose();
            if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) clickGui.onClose();
            else if (keybindPressedCategory != null) keybindPressedCategory.setKeyPressed(keyCode);
            if (keyCode == Objects.requireNonNull(MODULE_MANAGER.getModule(ClickGui.class)).getKey()) clickGui.onClose();
        }
        return false;
    }

    public void reloadConfig() {
        settings.loadColors();
        settings.loadPositions();
    }

    public ClickGuiSettings getSettings() {
        return settings;
    }
}
