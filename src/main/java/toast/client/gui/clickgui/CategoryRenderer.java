package toast.client.gui.clickgui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import toast.client.dontobfuscate.ClickGuiSettings;
import toast.client.dontobfuscate.Setting;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

import java.util.ArrayList;
import java.util.Map;

import static toast.client.gui.clickgui.ClickGuiScreen.*;

public class CategoryRenderer {
    public TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    public ClickGuiSettings settings = ClickGuiScreen.settings;
    public ClickGuiSettings.Colors colors = settings.getColors();
    private final ArrayList<Slider> sliders = new ArrayList<>();
    public boolean hasDesc = false;
    public int descPosX = 0;
    public int descPosY = 0;
    public String desc = "";
    public boolean clickedL;
    public boolean clickedR;
    public double mouseX;
    public double mouseY;
    public String categoryString;
    public Module.Category category;
    public boolean isCategory;

    public CategoryRenderer(int mouseX, int mouseY, Module.Category category, boolean clickedL, boolean clickedR) {
        System.out.println("Rendering Category");
        this.isCategory = true;
        this.categoryString = category.toString();
        this.category = category;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.clickedL = clickedL;
        this.clickedR = clickedR;
        int catBgColor = colors.categoryBgColor;
        if (isMouseOverRect(getMouseX(), getMouseY(), getX(), getY(), getBoxWidth(), getBoxHeight())) {
            if (isClickedR()) {
                catBgColor = colors.categoryClickColor;
                settings.getPositions(this.categoryString).setExpanded(!settings.getPositions(this.categoryString).isExpanded());
                settings.savePositions();
            } else {
                catBgColor = colors.categoryHoverBgColor;
            }
        }
        drawTextBox(getXint(), getYint(), getBoxWidth(), getBoxHeight(), colors.categoryBoxColor, colors.categoryTextColor, colors.categoryPrefixColor, catBgColor, colors.categoryPrefix, category.toString());
        if (settings.getPositions(category.toString()).isExpanded()) {
            int u = 1;
            for (Module module : ModuleManager.getModulesInCategory(category)) {
                int moduleTextColor;
                int moduleBgColor;
                if (module.isEnabled()) {
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
                    } else if (isClickedR()) {
                        if (settings.getPositions(this.categoryString).getExpandedModules().contains(module.getName())) {
                            settings.getPositions(this.categoryString).getExpandedModules().remove(module.getName());
                        } else {
                            settings.getPositions(this.categoryString).getExpandedModules().add(module.getName());
                        }
                        settings.savePositions();
                    } else {
                        moduleBgColor = colors.moduleHoverBgColor;
                    }
                    desc = module.getDescription();
                    descPosX = (int) Math.round(getX() + getBoxWidth());
                    descPosY = (int) Math.round(getYIteration(u));
                    hasDesc = true;
                }
                drawTextBox(getXint(), (int) Math.round(getYIteration(u)), getBoxWidth(), getBoxHeight(), colors.moduleBoxColor, moduleTextColor, colors.modulePrefixColor, moduleBgColor, colors.modulePrefix, module.getName());
                u++;
                if (!module.getSettings().getSettings().isEmpty()) {
                    for (String modName : settings.getPositions(this.categoryString).getExpandedModules()) {
                        if (modName.equals(module.getName())) {
                            for (Map.Entry<String, Setting> settingEntry : module.getSettings().getSettings().entrySet()) {
                                Setting setting = settingEntry.getValue();
                                if (setting.getType() == 0) {
                                    int settingBgColor = colors.settingOnBgColor;
                                    if (isMouseOverRect(getMouseX(), getMouseY(), getX(), getYIteration(u), getBoxWidth(), getBoxHeight())) {
                                        if (isClickedL()) {
                                            settingBgColor = colors.settingClickColor;
                                            ArrayList<String> modes = module.getSettings().getSettingDef(settingEntry.getKey()).getModes();
                                            if (modes.size() > 0) {
                                                if (modes.indexOf(setting.getMode()) == modes.size() - 1) {
                                                    setting.setMode(modes.get(0));
                                                } else {
                                                    setting.setMode(modes.get(modes.indexOf(setting.getMode()) + 1));
                                                }
                                            }
                                        } else {
                                            settingBgColor = colors.settingHoverBgColor;
                                        }
                                    }
                                    drawTextBox(getXint(), (int) Math.round(getYIteration(u)), getBoxWidth(), getBoxHeight(), colors.settingBoxColor, colors.settingOnTextColor, colors.settingPrefixColor, settingBgColor, colors.settingPrefix, settingEntry.getKey() + ": " + setting.getMode());
                                    u++;
                                } else if (setting.getType() == 1) {
                                    int settingBgColor = colors.settingOnBgColor;
                                    int settingKnobColor = colors.settingSliderKnobColor;
                                    int minValLength = textRenderer.getStringWidth(module.getSettings().getSettingDef(settingEntry.getKey()).getMinValue().toString());
                                    int maxValLength = textRenderer.getStringWidth(module.getSettings().getSettingDef(settingEntry.getKey()).getMaxValue().toString());
                                    int sliderX = getXint() + 5 + minValLength;
                                    int sliderY = (int) Math.round(getYIteration(u + 1)) + getBoxHeight() / 2 - 3;
                                    int sliderLength = getBoxWidth() - 14 - minValLength - maxValLength;
                                    double sliderMax = module.getSettings().getSettingDef(settingEntry.getKey()).getMaxValue();
                                    double sliderKnobX = Math.round(((setting.getValue() / sliderMax) * sliderLength) + sliderX);
                                    sliders.add(new Slider(sliderX, sliderY, sliderLength, sliderKnobX, module, setting, settingEntry.getKey()));
                                    if (isMouseOverRect(getMouseX(), getMouseY(), getX(), getYIteration(u), getBoxWidth(), getBoxHeight() * 2)) {
                                        if (isMouseOverRect(getMouseX(), getMouseY(), sliders.get(sliders.size() - 1).sliderKnobX - 1, sliders.get(sliders.size() - 1).sliderPosY - 2, 4, 6)) {
                                            settingKnobColor = colors.settingSliderKnobHoverColor;
                                        } else if (isClickedL()) {
                                            settingBgColor = colors.settingClickColor;
                                        } else {
                                            settingBgColor = colors.settingHoverBgColor;
                                        }
                                    }
                                    String curVal;
                                    if (String.valueOf(setting.getValue()).length() > 5) {
                                        curVal = String.valueOf(setting.getValue()).substring(0, 5);
                                    } else {
                                        curVal = String.valueOf(setting.getValue());
                                    }
                                    drawTextBox(getXint(), (int) Math.round(getYIteration(u)), getBoxWidth(), getBoxHeight() * 2 + 1, colors.settingBoxColor, colors.settingOnTextColor, colors.settingPrefixColor, settingBgColor, colors.settingPrefix, settingEntry.getKey() + ": " + curVal);
                                    u++;
                                    drawRect(sliders.get(sliders.size() - 1).sliderPosX, sliders.get(sliders.size() - 1).sliderPosY, sliders.get(sliders.size() - 1).sliderBarLength, 2, colors.settingSliderBarColor);
                                    drawRect((int) Math.round(sliders.get(sliders.size() - 1).sliderKnobX - 1), sliders.get(sliders.size() - 1).sliderPosY - 2, 4, 6, settingKnobColor);
                                    drawText(module.getSettings().getSettingDef(settingEntry.getKey()).getMinValue().toString(), getXint() + 2, (int) Math.round(getYIteration(u)), colors.settingSliderSideNumbersColor);
                                    drawText(module.getSettings().getSettingDef(settingEntry.getKey()).getMaxValue().toString(), getXint() + getBoxWidth() - 6 - maxValLength, (int) Math.round(getYIteration(u)), colors.settingSliderSideNumbersColor);
                                    u++;
                                } else if (setting.getType() == 2) {
                                    int settingTextColor;
                                    int settingBgColor;
                                    if (setting.isEnabled()) {
                                        settingTextColor = colors.settingOnTextColor;
                                        settingBgColor = colors.settingOnBgColor;
                                    } else {
                                        settingTextColor = colors.settingOffTextColor;
                                        settingBgColor = colors.settingOffBgColor;
                                    }
                                    if (isMouseOverRect(getMouseX(), getMouseY(), getX(), getYIteration(u), getBoxWidth(), getBoxHeight())) {
                                        if (isClickedL()) {
                                            settingBgColor = colors.settingClickColor;
                                            setting.setEnabled(!setting.isEnabled());
                                        } else {
                                            settingBgColor = colors.settingHoverBgColor;
                                        }
                                        if (settingEntry.getKey().equals("Visible")) {
                                            desc = "Whether or not to show on the hud.";
                                            descPosX = (int) Math.round(getX() + getBoxWidth());
                                            descPosY = (int) Math.round(getYIteration(u));
                                            hasDesc = true;
                                        }
                                    }
                                    drawTextBox(getXint(), (int) Math.round(getYIteration(u)), getBoxWidth(), getBoxHeight(), colors.settingBoxColor, settingTextColor, colors.settingPrefixColor, settingBgColor, colors.settingPrefix, settingEntry.getKey());
                                    u++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static class Slider {
        int sliderPosX;
        int sliderPosY;
        int sliderBarLength;
        double sliderKnobX;
        Module module;
        Setting setting;
        String settingName;

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

    public double getX() {
        return settings.getPositions(this.categoryString).getPosX();
    }

    public void setX(double newX) {
        settings.getPositions(this.categoryString).setPosX(newX);
    }

    public int getBoxWidth() {
        return ClickGuiScreen.width;
    }

    public int getBoxHeight() {
        return ClickGuiScreen.height;
    }

    public double getY() {
        return settings.getPositions(this.categoryString).getPosY();
    }

    public void setY(double newY) {
        settings.getPositions(this.categoryString).setPosY(newY);
    }

    public double getYIteration(int iteration) {
        return getY() + iteration + getBoxHeight() * iteration;
    }

    public int getXint() {
        return (int) Math.round(settings.getPositions(this.categoryString).getPosX());
    }

    public int getYint() {
        return (int) Math.round(settings.getPositions(this.categoryString).getPosY());
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
        return isMouseOverRect(getMouseX(), getMouseY(), getX(), getY(), getBoxWidth(), getBoxHeight());
    }

    public void updateMousePos(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public void updatePosition(double dragX, double dragY) {
        if (this.isMouseOverCat() && isCategory) {
            this.setX(this.getX() + dragX);
            this.setY(this.getY() + dragY);
            settings.savePositions();
        } else {
            if (sliders.size() > 0) {
                for (Slider slider : sliders) {
                    if (isMouseOverRect(mouseX, mouseY, slider.sliderKnobX - 1, slider.sliderPosY - 2, 2, 6)) {
                        double newSliderKnobX = mouseX;
                        if (newSliderKnobX >= slider.sliderPosX && newSliderKnobX <= slider.sliderPosX + slider.sliderBarLength) {
                            slider.sliderKnobX = newSliderKnobX;
                            slider.setting.setValue((((mouseX - slider.sliderPosX) / slider.sliderBarLength) * slider.module.getSettings().getSettingDef(slider.settingName).getMaxValue()));
                        }
                    }
                }
            }
        }
    }
}
