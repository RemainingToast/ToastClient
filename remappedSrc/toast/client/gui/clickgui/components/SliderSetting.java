package toast.client.gui.clickgui.components;

import toast.client.gui.clickgui.CategoryRenderer;
import toast.client.gui.clickgui.ClickGuiSettings;
import toast.client.modules.Module;
import toast.client.modules.config.Setting;

import static toast.client.gui.clickgui.CategoryRenderer.*;
import static toast.client.utils.TwoDRenderUtils.*;

public class SliderSetting {
    public static CategoryRenderer.Slider render(Module module, Setting setting, String settingName, ClickGuiSettings.Colors colors, double x, double mouseX, double mouseY, int curIt, boolean clickedL) {
        int settingBgColor = colors.settingOnBgColor;
        int settingKnobColor = colors.settingSliderKnobColor;
        int minValLength = CategoryRenderer.textRenderer.getStringWidth(module.getSettings().getSettingDef(settingName).getMinValue().toString());
        int maxValLength = CategoryRenderer.textRenderer.getStringWidth(module.getSettings().getSettingDef(settingName).getMaxValue().toString());
        int sliderX = (int) Math.round(x) + 5 + minValLength;
        int sliderY = (int) Math.round(getYIteration(curIt + 1)) + getBoxHeight() / 2 - 3;
        int sliderLength = getBoxWidth() - 14 - minValLength - maxValLength;
        double sliderMax = module.getSettings().getSettingDef(settingName).getMaxValue();
        double sliderKnobX = Math.round(((setting.getValue() / sliderMax) * sliderLength) + sliderX);
        CategoryRenderer.Slider slider = new CategoryRenderer.Slider(sliderX, sliderY, sliderLength, sliderKnobX, module, setting, settingName);
        if (isMouseOverRect(mouseX, mouseY, x, getYIteration(curIt), getBoxWidth(), getBoxHeight() * 2)) {
            if (isMouseOverRect(mouseX, mouseY, slider.sliderKnobX - 1, slider.sliderPosY - 2, 4, 6)) {
                settingKnobColor = colors.settingSliderKnobHoverColor;
            } else if (clickedL) {
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
        drawTextBox((int) Math.round(x), (int) Math.round(getYIteration(curIt)), getBoxWidth(), getBoxHeight() * 2 + 1, colors.settingBoxColor, colors.settingOnTextColor, colors.settingPrefixColor, settingBgColor, colors.settingPrefix, settingName + ": " + curVal);
        curIt++;
        drawRect(slider.sliderPosX, slider.sliderPosY, slider.sliderBarLength, 2, colors.settingSliderBarColor);
        drawRect((int) Math.round(slider.sliderKnobX - 1), slider.sliderPosY - 2, 4, 6, settingKnobColor);
        drawText(module.getSettings().getSettingDef(settingName).getMinValue().toString(), (int) Math.round(x) + 2, (int) Math.round(getYIteration(curIt)), colors.settingSliderSideNumbersColor);
        drawText(module.getSettings().getSettingDef(settingName).getMaxValue().toString(), (int) Math.round(x) + getBoxWidth() - 6 - maxValLength, (int) Math.round(getYIteration(curIt)), colors.settingSliderSideNumbersColor);
        return slider;
    }
}
