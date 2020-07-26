package dev.toastmc.client.gui.clickgui.components;

import dev.toastmc.client.gui.clickgui.CategoryRenderer;
import dev.toastmc.client.gui.clickgui.ClickGuiSettings;
import dev.toastmc.client.modules.config.Setting;
import dev.toastmc.client.utils.TwoDRenderUtils;
import dev.toastmc.client.gui.clickgui.ClickGuiSettings;
import toast.client.modules.config.Setting;

import static dev.toastmc.client.gui.clickgui.CategoryRenderer.*;
import static toast.client.utils.TwoDRenderUtils.drawTextBox;
import static toast.client.utils.TwoDRenderUtils.isMouseOverRect;

public class ToggleSetting {
    public static void render(Setting setting, String settingName, ClickGuiSettings.Colors colors, double x, double mouseX, double mouseY, int curIt, boolean clickedL) {
        int settingTextColor;
        int settingBgColor;
        if (setting.isEnabled()) {
            settingTextColor = colors.settingOnTextColor;
            settingBgColor = colors.settingOnBgColor;
        } else {
            settingTextColor = colors.settingOffTextColor;
            settingBgColor = colors.settingOffBgColor;
        }
        if (TwoDRenderUtils.isMouseOverRect(mouseX, mouseY, CategoryRenderer.getX(), CategoryRenderer.getYIteration(curIt), CategoryRenderer.getBoxWidth(), CategoryRenderer.getBoxHeight())) {
            if (clickedL) {
                settingBgColor = colors.settingClickColor;
                setting.setEnabled(!setting.isEnabled());
            } else {
                settingBgColor = colors.settingHoverBgColor;
            }
        }
        TwoDRenderUtils.drawTextBox((int) Math.round(x), (int) Math.round(CategoryRenderer.getYIteration(curIt)), CategoryRenderer.getBoxWidth(), CategoryRenderer.getBoxHeight(), colors.settingBoxColor, settingTextColor, colors.settingPrefixColor, settingBgColor, colors.settingPrefix, settingName);
    }
}
