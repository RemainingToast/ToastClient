package dev.toastmc.client.gui.click

import dev.toastmc.client.ToastClient.Companion.MODULE_MANAGER
import dev.toastmc.client.gui.TwoDRenderUtils.drawTextBox
import dev.toastmc.client.gui.TwoDRenderUtils.isMouseOverRect
import dev.toastmc.client.gui.click.components.Description
import dev.toastmc.client.gui.click.components.HiddenComponent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.util.ConfigUtil
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.glfw.GLFW
import java.awt.Color
import kotlin.math.roundToInt

class CategoryRenderer(
        private val clickGuiScreen: ClickGuiScreen,
        matrixStack: MatrixStack,
        mouseX: Int,
        mouseY: Int,
        val category: Category,
        clickedL: Boolean,
        clickedR: Boolean
) {
    var keybindingPressed: Boolean = false
    private var keybindingModule: Module? = null
    private var isKeyPressed: Boolean = false
    var description: Description? = null
    var hidden: HiddenComponent? = null
    private var isClickedL: Boolean
    private var isClickedR: Boolean
    var mouseX: Double
    var mouseY: Double
    private var isCategory: Boolean = true
    private fun setX(newX: Double) {
        settings.getPositions(categoryString).x = newX
    }

    private fun setY(newY: Double) {
        settings.getPositions(categoryString).y = newY
    }

    private val xInt: Int get() = settings.getPositions(categoryString).x.roundToInt()
    private val yInt: Int get() = settings.getPositions(categoryString).y.roundToInt()
    private val isMouseOverCat: Boolean
        get() {
            return isMouseOverRect(mouseX, mouseY, x - 4, y - 4, boxWidth + 4, boxHeight + 2)
        }

    fun setKeyPressed(keyPressed: Int) {
        if (keybindingPressed) {
            if (keyPressed == -1) return else if (keyPressed == GLFW.GLFW_KEY_BACKSPACE || keyPressed == GLFW.GLFW_KEY_DELETE) keybindingModule?.key = -1
            else keybindingModule?.key = keyPressed
            isKeyPressed = true
            clickGuiScreen.keybindingPressedCategory = null
            ConfigUtil.save()
        } else keybindingPressed = true
    }

    fun updateMousePos(mouseX: Double, mouseY: Double) {
        this.mouseX = mouseX
        this.mouseY = mouseY
    }

    fun updatePosition(dragX: Double, dragY: Double): Boolean {
        if (isMouseOverCat && isCategory) {
            setX(x + dragX)
            setY(y + dragY)
            settings.savePositions()
            return true
        }
        return false
    }

    val boxWidth: Int
        get() {
            return clickGuiScreen.w
        }
    val boxHeight: Int
        get() {
            return clickGuiScreen.h
        }
    val x: Double
        get() {
            return settings.getPositions(categoryString).x
        }
    val y: Double
        get() {
            return settings.getPositions(categoryString).y
        }

    fun getYIteration(iteration: Int): Double {
        return y + iteration + (boxHeight * iteration)
    }

    private val categoryString: String
        get() {
            return category.toString()
        }
    private var settings = clickGuiScreen.settings

    var colors = settings.colors

    init {
        this.mouseX = mouseX.toDouble()
        this.mouseY = mouseY.toDouble()
        isClickedL = clickedL
        isClickedR = clickedR
        var moduleTextColor: Int
        var moduleBgColor: Int
        val catExpanded = settings.getPositions(category.toString()).expanded
        var catBgColor: Int = Color(55, 175, 0, 200).rgb
        if (isMouseOverRect(mouseX.toDouble(), mouseY.toDouble(), x, y, boxWidth, boxHeight)) {
            if (isClickedR) {
                catBgColor = Color(55, 175, 0, 100).rgb
                settings.getPositions(categoryString).expanded = !settings.getPositions(categoryString).expanded
                settings.savePositions()
            } else {
                catBgColor = Color(55, 175, 0, 100).rgb
            }
        }
        drawTextBox(matrixStack, xInt, yInt, boxWidth, boxHeight, colors.categoryBoxColor, colors.categoryTextColor, colors.categoryPrefixColor, catBgColor, if (catExpanded) "- " else "+ ", category.toString())
        if (catExpanded) {
            var u = 1
            for (module: Module in MODULE_MANAGER.getModulesInCategory(category)) {
                if (module.enabled) {
                    moduleTextColor = colors.moduleOnTextColor
                    moduleBgColor = colors.moduleOffBgColor
                } else {
                    moduleTextColor = colors.moduleOffTextColor
                    moduleBgColor = colors.moduleOffBgColor
                }
                if (isMouseOverRect(mouseX.toDouble(), mouseY.toDouble(), x, getYIteration(u), boxWidth, boxHeight)) {
                    when {
                        isClickedL -> {
                            moduleBgColor = colors.moduleClickColor
                            module.toggle()
                            ConfigUtil.save()
                        }
                        isClickedR -> {
                            if (settings.getPositions(categoryString).expandedModules.contains(module.label)) {
                                settings.getPositions(categoryString).expandedModules.remove(module.label)
                                moduleBgColor = colors.moduleOnBgColor
                            } else {
                                settings.getPositions(categoryString).expandedModules.add(module.label)
                                moduleBgColor = colors.moduleOnBgColor
                            }
                            settings.savePositions()
                        }
                        else -> {
                            moduleBgColor = colors.moduleHoverBgColor
                        }
                    }
                    if(module.description.isNotEmpty()) description = Description(module.description, (x + boxWidth).roundToInt(), getYIteration(u).roundToInt(), true)
                }
                if(settings.moduleExpanded(categoryString, module)) moduleBgColor = colors.moduleExpandadBgColor
                drawTextBox(matrixStack, xInt, getYIteration(u).roundToInt(), boxWidth, boxHeight,
                        colors.moduleBoxColor, moduleTextColor,
                        colors.modulePrefixColor, moduleBgColor,
                        "", module.label)
                u++
                for (mod in settings.getPositions(categoryString).expandedModules){
                    if(mod == module.label){
                        HiddenComponent().render(matrixStack, module, this, "Hidden", colors, x, mouseX.toDouble(), mouseY.toDouble(), u, isClickedL)
                        u++
                        var keybindBgColor = colors.settingOnBgColor
                        var keybindText: String
                        if (isMouseOverRect(mouseX.toDouble(), mouseY.toDouble(), x, getYIteration(u), boxWidth, boxHeight)) {
                            keybindBgColor = colors.settingHoverBgColor
                            if (isClickedL) {
                                this.keybindingPressed = true
                                this.keybindingModule = module
                            }
                        }
                        if (keybindingModule === module) {
                            keybindBgColor = colors.settingHoverBgColor
                            keybindText = "Keybind: ..."
                            if (!isKeyPressed) {
                                keybindingPressed = true
                                keybindingModule = module
                            }
                        } else {
                            val key: Int = module.key
                            keybindText = if (key == GLFW.GLFW_KEY_UNKNOWN) "Keybind: NONE" else "Keybind: " + GLFW.glfwGetKeyName(key, GLFW.glfwGetKeyScancode(key))
                        }
                        drawTextBox(matrixStack, xInt, getYIteration(u).roundToInt(), boxWidth, boxHeight, colors.settingBoxColor, colors.settingOnTextColor, colors.settingPrefixColor, keybindBgColor, colors.settingPrefix, keybindText)
                        u++
                    }
                }
            }
        }
    }
}