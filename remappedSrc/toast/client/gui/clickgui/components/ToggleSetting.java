package toast.client.gui.clickgui.components;

import toast.client.gui.clickgui.ClickGuiSettings;
import toast.client.modules.config.Setting;

import static toast.client.gui.clickgui.CategoryRenderer.*;
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
        if (isMouseOverRect(mouseX, mouseY, getX(), getYIteration(curIt), getBoxWidth(), getBoxHeight())) {
            if (clickedL) {
                settingBgColor = colors.settingClickColor;
                setting.setEnabled(!setting.isEnabled());
            } else {
                settingBgColor = colors.settingHoverBgColor;
            }
        }
        drawTextBox((int) Math.round(x), (int) Math.round(getYIteration(curIt)), getBoxWidth(), getBoxHeight(), colors.settingBoxColor, settingTextColor, colors.settingPrefixColor, settingBgColor, colors.settingPrefix, settingName);
    }
}
