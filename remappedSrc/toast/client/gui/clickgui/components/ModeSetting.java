package toast.client.gui.clickgui.components;

import toast.client.gui.clickgui.ClickGuiSettings;
import toast.client.modules.Module;

import java.util.ArrayList;

import static toast.client.gui.clickgui.CategoryRenderer.*;
import static toast.client.utils.TwoDRenderUtils.drawTextBox;
import static toast.client.utils.TwoDRenderUtils.isMouseOverRect;

public class ModeSetting {
    public static void render(Module module, String settingName, ClickGuiSettings.Colors colors, double x, double mouseX, double mouseY, int curIt, boolean clickedL) {
        int settingBgColor = colors.settingOnBgColor;
        if (isMouseOverRect(mouseX, mouseY, x, getYIteration(curIt), getBoxWidth(), getBoxHeight())) {
            if (clickedL) {
                settingBgColor = colors.settingClickColor;
                ArrayList<String> modes = module.getSettings().getModes(settingName);
                if (modes.size() > 0) {
                    if (modes.indexOf(module.getSettings().getMode(settingName)) == modes.size() - 1) {
                        module.getSettings().getSetting(settingName).setMode(modes.get(0));
                    } else {
                        module.getSettings().getSetting(settingName).setMode(modes.get(modes.indexOf(module.getSettings().getMode(settingName)) + 1));
                    }
                }
            } else {
                settingBgColor = colors.settingHoverBgColor;
            }
        }
        drawTextBox((int) Math.round(x), (int) Math.round(getYIteration(curIt)), getBoxWidth(), getBoxHeight(), colors.settingBoxColor, colors.settingOnTextColor, colors.settingPrefixColor, settingBgColor, colors.settingPrefix, settingName + ": " + module.getSettings().getMode(settingName));
    }
}
