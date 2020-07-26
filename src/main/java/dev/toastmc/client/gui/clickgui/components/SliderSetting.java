package dev.toastmc.client.gui.clickgui.components;

import dev.toastmc.client.gui.clickgui.CategoryRenderer;
import dev.toastmc.client.gui.clickgui.ClickGuiSettings;
import dev.toastmc.client.modules.config.Setting;
import dev.toastmc.client.utils.TwoDRenderUtils;
import dev.toastmc.client.gui.clickgui.CategoryRenderer;
import dev.toastmc.client.gui.clickgui.ClickGuiSettings;
import toast.client.modules.Module;
import toast.client.modules.config.Setting;

import static dev.toastmc.client.gui.clickgui.CategoryRenderer.*;
import static toast.client.utils.TwoDRenderUtils.*;

public class SliderSetting {
    public static CategoryRenderer.Slider render(Module module, Setting setting, String settingName, ClickGuiSettings.Colors colors, double x, double mouseX, double mouseY, int curIt, boolean clickedL) {
        int settingBgColor = colors.settingOnBgColor;
        int settingKnobColor = colors.settingSliderKnobColor;
        int minValLength = CategoryRenderer.textRenderer.getWidth(module.getSettings().getSettingDef(settingName).getMinValue().toString());
        int maxValLength = CategoryRenderer.textRenderer.getWidth(module.getSettings().getSettingDef(settingName).getMaxValue().toString());
        int sliderX = (int) Math.round(x) + 5 + minValLength;
        int sliderY = (int) Math.round(CategoryRenderer.getYIteration(curIt + 1)) + CategoryRenderer.getBoxHeight() / 2 - 3;
        int sliderLength = CategoryRenderer.getBoxWidth() - 14 - minValLength - maxValLength;
        double sliderMax = module.getSettings().getSettingDef(settingName).getMaxValue();
        double sliderKnobX = Math.round(((setting.getValue() / sliderMax) * sliderLength) + sliderX);
        CategoryRenderer.Slider slider = new CategoryRenderer.Slider(sliderX, sliderY, sliderLength, sliderKnobX, module, setting, settingName);
        if (TwoDRenderUtils.isMouseOverRect(mouseX, mouseY, x, CategoryRenderer.getYIteration(curIt), CategoryRenderer.getBoxWidth(), CategoryRenderer.getBoxHeight() * 2)) {
            if (TwoDRenderUtils.isMouseOverRect(mouseX, mouseY, slider.sliderKnobX - 1, slider.sliderPosY - 2, 4, 6)) {
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
        TwoDRenderUtils.drawTextBox((int) Math.round(x), (int) Math.round(CategoryRenderer.getYIteration(curIt)), CategoryRenderer.getBoxWidth(), CategoryRenderer.getBoxHeight() * 2 + 1, colors.settingBoxColor, colors.settingOnTextColor, colors.settingPrefixColor, settingBgColor, colors.settingPrefix, settingName + ": " + curVal);
        curIt++;
        TwoDRenderUtils.drawRect(slider.sliderPosX, slider.sliderPosY, slider.sliderBarLength, 2, colors.settingSliderBarColor);
        TwoDRenderUtils.drawRect((int) Math.round(slider.sliderKnobX - 1), slider.sliderPosY - 2, 4, 6, settingKnobColor);
        TwoDRenderUtils.drawText(module.getSettings().getSettingDef(settingName).getMinValue().toString(), (int) Math.round(x) + 2, (int) Math.round(CategoryRenderer.getYIteration(curIt)), colors.settingSliderSideNumbersColor);
        TwoDRenderUtils.drawText(module.getSettings().getSettingDef(settingName).getMaxValue().toString(), (int) Math.round(x) + CategoryRenderer.getBoxWidth() - 6 - maxValLength, (int) Math.round(CategoryRenderer.getYIteration(curIt)), colors.settingSliderSideNumbersColor);
        return slider;
    }
}
