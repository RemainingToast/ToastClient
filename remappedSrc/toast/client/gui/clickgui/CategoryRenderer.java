package toast.client.gui.clickgui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import org.lwjgl.glfw.GLFW;
import toast.client.ToastClient;
import toast.client.gui.clickgui.components.Description;
import toast.client.gui.clickgui.components.ModeSetting;
import toast.client.gui.clickgui.components.SliderSetting;
import toast.client.gui.clickgui.components.ToggleSetting;
import toast.client.modules.Module;
import toast.client.modules.config.Setting;

import java.util.ArrayList;
import java.util.Map;

import static toast.client.ToastClient.CONFIG_MANAGER;
import static toast.client.ToastClient.MODULE_MANAGER;
import static toast.client.gui.clickgui.ClickGuiScreen.keybindPressedCategory;
import static toast.client.utils.TwoDRenderUtils.drawTextBox;
import static toast.client.utils.TwoDRenderUtils.isMouseOverRect;

public class CategoryRenderer {
    private static final ArrayList<Slider> sliders = new ArrayList<>();
    public static TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    public static ClickGuiSettings settings = ClickGuiScreen.settings;
    public static ClickGuiSettings.Colors colors = settings.getColors();
    public static String categoryString;
    public static Module.Category category;
    public boolean keybindPressed = false;
    public Module keybindModule = null;
    public boolean isKeyPressed = false;
    public Description description = null;
    public boolean clickedL;
    public boolean clickedR;
    public double mouseX;
    public double mouseY;
    public boolean isCategory;

    public CategoryRenderer(int mouseX, int mouseY, Module.Category category, boolean clickedL, boolean clickedR) {
        this.isCategory = true;
        categoryString = category.toString();
        CategoryRenderer.category = category;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.clickedL = clickedL;
        this.clickedR = clickedR;
        int catBgColor = colors.categoryBgColor;
        if (isMouseOverRect(getMouseX(), getMouseY(), getX(), getY(), getBoxWidth(), getBoxHeight())) {
            if (isClickedR()) {
                catBgColor = colors.categoryClickColor;
                settings.getPositions(categoryString).setExpanded(!settings.getPositions(categoryString).isExpanded());
                settings.savePositions();
            } else {
                catBgColor = colors.categoryHoverBgColor;
            }
        }
        drawTextBox(getXint(), getYint(), getBoxWidth(), getBoxHeight(), colors.categoryBoxColor, colors.categoryTextColor, colors.categoryPrefixColor, catBgColor, colors.categoryPrefix, category.toString());
        if (settings.getPositions(category.toString()).isExpanded()) {
            int u = 1;
            for (Module module : MODULE_MANAGER.getModulesInCategory(category)) {
                int moduleTextColor;
                int moduleBgColor;
                if (module.getEnabled()) {
                    moduleTextColor = colors.moduleOnTextColor;
                    moduleBgColor = colors.moduleOnBgColor;
                } else {
                    moduleTextColor = colors.moduleOffTextColor;
                    moduleBgColor = colors.moduleOffBgColor;
                }
                if (isMouseOverRect(getMouseX(), getMouseY(), getX(), getYIteration(u), getBoxWidth(), getBoxHeight())) {
                    if (isClickedL()) {
                        moduleBgColor = colors.moduleClickColor;
                        module.toggle();
                        ToastClient.CONFIG_MANAGER.writeModules();
                    } else if (isClickedR()) {
                        if (settings.getPositions(categoryString).getExpandedModules().contains(module.getName())) {
                            settings.getPositions(categoryString).getExpandedModules().remove(module.getName());
                        } else {
                            settings.getPositions(categoryString).getExpandedModules().add(module.getName());
                        }
                        settings.savePositions();
                    } else {
                        moduleBgColor = colors.moduleHoverBgColor;
                    }
                    description = new Description();
                    description.desc = module.getDescription();
                    description.descPosX = (int) Math.round(getX() + getBoxWidth());
                    description.descPosY = (int) Math.round(getYIteration(u));
                    description.hasDesc = true;
                }
                drawTextBox(getXint(), (int) Math.round(getYIteration(u)), getBoxWidth(), getBoxHeight(), colors.moduleBoxColor, moduleTextColor, colors.modulePrefixColor, moduleBgColor, colors.modulePrefix, module.getName());
                u++;
                if (!module.getSettings().getSettings().isEmpty()) {
                    for (String modName : settings.getPositions(categoryString).getExpandedModules()) {
                        if (modName.equals(module.getName())) {
                            for (Map.Entry<String, Setting> settingEntry : module.getSettings().getSettings().entrySet()) {
                                Setting setting = settingEntry.getValue();
                                if (!settingEntry.getKey().equals("Visible")) {
                                    if (setting.getType() == 0) {
                                        ModeSetting.render(module, settingEntry.getKey(), colors, getX(), getMouseX(), getMouseY(), u, isClickedL());
                                        u++;
                                    } else if (setting.getType() == 1) {
                                        sliders.add(SliderSetting.render(module, setting, settingEntry.getKey(), colors, getX(), getMouseX(), getMouseY(), u, isClickedL()));
                                        u++;
                                        u++;
                                    } else if (setting.getType() == 2) {
                                        ToggleSetting.render(setting, settingEntry.getKey(), colors, getX(), getMouseX(), getMouseY(), u, isClickedL());
                                        u++;
                                    }
                                }
                            }
                            ToggleSetting.render(module.getSettings().getSetting("Visible"), "Visible", colors, getX(), getMouseX(), getMouseY(), u, isClickedL());
                            description = new Description();
                            description.desc = "Whether or not to show on the hud.";
                            description.descPosX = (int) Math.round(getX() + getBoxWidth());
                            description.descPosY = (int) Math.round(getYIteration(u));
                            description.hasDesc = true;
                            u++;
                            int keybindBgColor = colors.settingOnBgColor;
                            String keybindText;
                            if (isMouseOverRect(getMouseX(), getMouseY(), getX(), getYIteration(u), getBoxWidth(), getBoxHeight())) {
                                keybindBgColor = colors.settingHoverBgColor;
                                if (isClickedL()) {
                                    this.keybindPressed = true;
                                    this.keybindModule = module;
                                }
                            }
                            if (keybindPressedCategory != null && keybindPressedCategory.keybindModule == module) {
                                keybindBgColor = colors.settingHoverBgColor;
                                keybindText = "Keybind: ...";
                                if (!isKeyPressed) {
                                    keybindPressed = true;
                                    keybindModule = module;
                                }
                            } else {
                                int key = module.getKey();
                                if (key == GLFW.GLFW_KEY_UNKNOWN) keybindText = "Keybind: NONE";
                                else keybindText = "Keybind: " + GLFW.glfwGetKeyName(key, GLFW.glfwGetKeyScancode(key));
                            }
                            drawTextBox(getXint(), (int) Math.round(getYIteration(u)), getBoxWidth(), getBoxHeight(), colors.settingBoxColor, colors.settingOnTextColor, colors.settingPrefixColor, keybindBgColor, colors.settingPrefix, keybindText);
                            u++;
                        }
                    }
                }
            }
        }
    }

    public static double getX() {
        return settings.getPositions(categoryString).getPosX();
    }

    public void setX(double newX) {
        settings.getPositions(categoryString).setPosX(newX);
    }

    public static int getBoxWidth() {
        return ClickGuiScreen.width;
    }

    public static int getBoxHeight() {
        return ClickGuiScreen.height;
    }

    public static double getY() {
        return settings.getPositions(categoryString).getPosY();
    }

    public void setY(double newY) {
        settings.getPositions(categoryString).setPosY(newY);
    }

    public static double getYIteration(int iteration) {
        return getY() + iteration + getBoxHeight() * iteration;
    }

    public int getXint() {
        return (int) Math.round(settings.getPositions(categoryString).getPosX());
    }

    public int getYint() {
        return (int) Math.round(settings.getPositions(categoryString).getPosY());
    }

    public double getMouseX() {
        return this.mouseX;
    }

    public double getMouseY() {
        return this.mouseY;
    }

    public boolean isClickedL() {
        return clickedL;
    }

    public boolean isClickedR() {
        return clickedR;
    }

    public boolean isMouseOverCat() {
        return isMouseOverRect(getMouseX(), getMouseY(), getX() - 4, getY() - 4, getBoxWidth() + 4, getBoxHeight() + 2);
    }

    public void setKeyPressed(int keyPressed) {
        if (this.keybindPressed) {
            if (keyPressed == -1) return;
            else if (keyPressed == GLFW.GLFW_KEY_BACKSPACE || keyPressed == GLFW.GLFW_KEY_DELETE)
                this.keybindModule.setKey(-1);
            else this.keybindModule.setKey(keyPressed);
            this.isKeyPressed = true;
            keybindPressedCategory = null;
            CONFIG_MANAGER.writeKeyBinds();
        } else this.keybindPressed = true;
    }

    public void updateMousePos(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public boolean updatePosition(double dragX, double dragY) {
        if (this.isMouseOverCat() && isCategory) {
            this.setX(getX() + dragX);
            this.setY(getY() + dragY);
            settings.savePositions();
            return true;
        } else {
            if (sliders.size() > 0) {
                for (Slider slider : sliders) {
                    if (isMouseOverRect(mouseX, mouseY, slider.sliderKnobX - 4, slider.sliderPosY - 4, 10, 10)) {
                        double newSliderKnobX = mouseX;
                        if (newSliderKnobX >= slider.sliderPosX && newSliderKnobX <= slider.sliderPosX + slider.sliderBarLength) {
                            slider.sliderKnobX = newSliderKnobX;
                            slider.setting.setValue((((mouseX - slider.sliderPosX) / slider.sliderBarLength) * slider.module.getSettings().getSettingDef(slider.settingName).getMaxValue()));
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static class Slider {
        public int sliderPosX;
        public int sliderPosY;
        public int sliderBarLength;
        public double sliderKnobX;
        public Module module;
        public Setting setting;
        public String settingName;

        public Slider(int x, int y, int length, double knob, Module module, Setting setting, String settingName) {
            sliderPosX = x;
            sliderPosY = y;
            sliderBarLength = length;
            sliderKnobX = knob;
            this.module = module;
            this.setting = setting;
            this.settingName = settingName;
        }
    }
}
