package dev.toastmc.client.gui.click.components

import dev.toastmc.client.gui.TwoDRenderUtils.drawTextBox
import dev.toastmc.client.gui.TwoDRenderUtils.isMouseOverRect
import dev.toastmc.client.gui.click.CategoryRenderer
import dev.toastmc.client.gui.click.ClickGuiSettings
import dev.toastmc.client.module.Module
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.roundToInt

class HiddenComponent {
    fun render(matrix: MatrixStack, module: Module, category: CategoryRenderer, settingName: String?, colors: ClickGuiSettings.Colors, x: Double, mouseX: Double, mouseY: Double, curIt: Int, clickedL: Boolean) {
        val settingTextColor: Int
        var settingBgColor: Int
        if (module.hidden) {
            settingTextColor = colors.settingOnTextColor
            settingBgColor = colors.settingOnBgColor
        } else {
            settingTextColor = colors.settingOffTextColor
            settingBgColor = colors.settingOffBgColor
        }
        if (isMouseOverRect(mouseX, mouseY, category.x, category.getYIteration(curIt), category.boxWidth, category.boxHeight)) {
            if (clickedL) {
                settingBgColor = colors.settingClickColor
                module.setHidden(!module.hidden)
            } else {
                settingBgColor = colors.settingHoverBgColor
            }
        }
        drawTextBox(matrix, x.roundToInt(), category.getYIteration(curIt).roundToInt(), category.boxWidth, category.boxHeight, colors.settingBoxColor, settingTextColor, colors.settingPrefixColor, settingBgColor, colors.settingPrefix, settingName)
    }
}