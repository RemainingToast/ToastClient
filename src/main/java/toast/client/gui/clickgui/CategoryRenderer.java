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
    public boolean hasDesc = false;
    public String category;
    public int descPosX = 0;
    public int descPosY = 0;
    public String desc = "";
    public boolean clickedL;
    public boolean clickedR;
    public boolean mouseLHeld;
    public double mouseX;
    public double mouseY;
    public int boxWidth;
    public int boxHeight;
    public boolean isOver;
    public static ClickGuiSettings.Colors colors = settings.getColors();
    public CategoryRenderer(double mouseX, double mouseY, int boxWidth, int boxHeight, Module.Category category, boolean clickedL, boolean clickedR) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        this.category = category.toString();
        this.clickedL = clickedL;
        this.clickedR = clickedR;
        this.boxHeight = boxHeight;
        this.boxWidth = boxWidth;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        int catBgColor = colors.categoryBgColor;
        if (isMouseOverRect(getMouseX(), getMouseY(), getX(), getY(), boxWidth, boxHeight)) {
            this.isOver = true;
            if (isClickedR()) {
                catBgColor = colors.categoryClickColor;
                settings.getPositions(this.category).setExpanded(!settings.getPositions(this.category).isExpanded());
                settings.savePositions();
            } else {
                catBgColor = colors.categoryHoverBgColor;
            }
        } else {
            this.isOver = false;
        }
        drawTextBox(getXint(), getYint(), boxWidth, boxHeight, colors.categoryBoxColor, colors.categoryTextColor, colors.categoryPrefixColor, catBgColor, colors.categoryPrefix, category.toString());
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
                if (isMouseOverRect(getMouseX(), getMouseY(), getX(), getYIteration(u), boxWidth, boxHeight)) {
                    if (isClickedL()) {
                        moduleBgColor = colors.moduleClickColor;
                        module.toggle();
                    } else if (isClickedR()) {
                        if (settings.getPositions(this.category).getExpandedModules().contains(module.getName())) {
                            settings.getPositions(this.category).getExpandedModules().remove(module.getName());
                        } else {
                            settings.getPositions(this.category).getExpandedModules().add(module.getName());
                        }
                        settings.savePositions();
                    } else {
                        moduleBgColor = colors.moduleHoverBgColor;
                    }
                    desc = module.getDescription();
                    descPosX = (int) Math.round(getX() + boxWidth);
                    descPosY = (int) Math.round(getYIteration(u));
                    hasDesc = true;
                }
                drawTextBox(getXint(), (int) Math.round(getYIteration(u)), boxWidth, boxHeight, colors.moduleBoxColor, moduleTextColor, colors.modulePrefixColor, moduleBgColor, colors.modulePrefix, module.getName());
                u++;
                if (!module.getSettings().getSettings().isEmpty()) {
                    for (String modName : settings.getPositions(this.category).getExpandedModules()) {
                        if (modName.equals(module.getName())) {
                            for (Map.Entry<String, Setting> settingEntry : module.getSettings().getSettings().entrySet()) {
                                Setting setting = settingEntry.getValue();
                                if (setting.getType() == 0) {
                                    int settingBgColor = colors.settingOnBgColor;
                                    if (isMouseOverRect(getMouseX(), getMouseY(), getX(), getYIteration(u), boxWidth, boxHeight)) {
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
                                    drawTextBox(getXint(), (int) Math.round(getYIteration(u)), boxWidth, boxHeight, colors.settingBoxColor, colors.settingOnTextColor, colors.settingPrefixColor, settingBgColor, colors.settingPrefix, settingEntry.getKey() + ": " + setting.getMode());
                                    u++;
                                }
                                if (setting.getType() == 1) {
                                    int settingBgColor = colors.settingOnBgColor;
                                    int settingKnobColor = colors.settingSliderKnobColor;
                                    int minValLenght = textRenderer.getStringWidth(module.getSettings().getSettingDef(settingEntry.getKey()).getMinValue().toString());
                                    int maxValLenght = textRenderer.getStringWidth(module.getSettings().getSettingDef(settingEntry.getKey()).getMaxValue().toString());
                                    int barLength = boxWidth - 14 - minValLenght - maxValLenght;
                                    int barX = getXint() + 5 + minValLenght;
                                    int barY = (int) Math.round(getYIteration(u + 1)) + boxHeight / 2 - 3;
                                    int knobCenterX = (int) Math.round(((setting.getValue() / module.getSettings().getSettingDef(settingEntry.getKey()).getMaxValue()) * barLength) + barX);
                                    if (isMouseOverRect(getMouseX(), getMouseY(), getX(), getYIteration(u), boxWidth, boxHeight * 2)) {
                                        if (isMouseOverRect(getMouseX(), getMouseY(), knobCenterX - 1, barY - 2, 4, 6)) {
                                            if (isMouseLHeld()) {
                                                if (mouseX >= barX && mouseX <= barX + barLength) {
                                                    knobCenterX = (int) Math.round(mouseX);
                                                    setting.setValue(((mouseX / barLength) * module.getSettings().getSettingDef(settingEntry.getKey()).getMaxValue()));
                                                    settingKnobColor = colors.settingSliderKnobDragColor;
                                                }
                                            } else {
                                                settingKnobColor = colors.settingSliderKnobHoverColor;
                                            }
                                        } else if (isClickedL()) {
                                            settingBgColor = colors.settingClickColor;
                                        } else {
                                            settingBgColor = colors.settingHoverBgColor;
                                        }
                                    }
                                    drawTextBox(getXint(), (int) Math.round(getYIteration(u)), boxWidth, boxHeight * 2, colors.settingBoxColor, colors.settingOnTextColor, colors.settingPrefixColor, settingBgColor, colors.settingPrefix, settingEntry.getKey() + ": " + setting.getValue());
                                    u++;
                                    drawRect(barX, barY, barLength, 2, colors.settingSliderBarColor);
                                    drawRect(knobCenterX - 1, barY - 2, 4, 6, settingKnobColor);
                                    drawText(module.getSettings().getSettingDef(settingEntry.getKey()).getMinValue().toString(), getXint() + 2, (int) Math.round(getYIteration(u)), colors.settingSliderSideNumbersColor);
                                    drawText(module.getSettings().getSettingDef(settingEntry.getKey()).getMaxValue().toString(), getXint() + boxWidth - 6 - maxValLenght, (int) Math.round(getYIteration(u)), colors.settingSliderSideNumbersColor);
                                    u++;
                                }
                                if (setting.getType() == 2) {
                                    int settingTextColor;
                                    int settingBgColor;
                                    if (setting.isEnabled()) {
                                        settingTextColor = colors.settingOnTextColor;
                                        settingBgColor = colors.settingOnBgColor;
                                    } else {
                                        settingTextColor = colors.settingOffTextColor;
                                        settingBgColor = colors.settingOffBgColor;
                                    }
                                    if (isMouseOverRect(getMouseX(), getMouseY(), getX(), getYIteration(u), boxWidth, boxHeight)) {
                                        if (isClickedL()) {
                                            settingBgColor = colors.settingClickColor;
                                            setting.setEnabled(!setting.isEnabled());
                                        } else {
                                            settingBgColor = colors.settingHoverBgColor;
                                        }
                                        if (settingEntry.getKey().equals("Visible")) {
                                            desc = "Whether or not to show on the hud.";
                                            descPosX = (int) Math.round(getX() + boxWidth);
                                            descPosY = (int) Math.round(getYIteration(u));
                                            hasDesc = true;
                                        }
                                    }
                                    drawTextBox(getXint(), (int) Math.round(getYIteration(u)), boxWidth, boxHeight, colors.settingBoxColor, settingTextColor, colors.settingPrefixColor, settingBgColor, colors.settingPrefix, settingEntry.getKey());
                                    u++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public double getX() {
        return settings.getPositions(this.category).getPosX();
    }

    public double getY() {
        return settings.getPositions(this.category).getPosY();
    }

    public double getYIteration(int iteration) {
        return getY() + iteration + boxHeight * iteration;
    }

    public int getXint() {
        return (int) Math.round(settings.getPositions(this.category).getPosX());
    }

    public int getYint() {
        return (int) Math.round(settings.getPositions(this.category).getPosY());
    }

    public void setX(double newX) {
        settings.getPositions(this.category).setPosX(newX);
    }

    public void setY(double newY) {
        settings.getPositions(this.category).setPosY(newY);
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
        return isMouseOverRect(getMouseX(), getMouseY(), getX(), getY(), boxWidth, boxHeight);
    }

    public void updateMousePos(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public boolean isMouseLHeld() {
        return mouseLHeld;
    }

    public void mouseHeldL(boolean isheld) {
        this.mouseLHeld = isheld;
    }

    public void updatePosition(double dragX, double dragY) {
        if (this.isMouseOverCat()) {
            this.setX(this.getX() + dragX);
            this.setY(this.getY() + dragY);
            settings.savePositions();
        }
    }
}
