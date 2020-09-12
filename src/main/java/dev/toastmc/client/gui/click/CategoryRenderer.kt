package dev.toastmc.client.gui.click

import dev.toastmc.client.ToastClient.Companion.MODULE_MANAGER
import dev.toastmc.client.gui.TwoDRenderUtils.drawTextBox
import dev.toastmc.client.gui.TwoDRenderUtils.isMouseOverRect
import dev.toastmc.client.gui.click.components.Description
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.util.ConfigUtil
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.glfw.GLFW
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
            if (keyPressed == -1) return else if (keyPressed == GLFW.GLFW_KEY_BACKSPACE || keyPressed == GLFW.GLFW_KEY_DELETE) keybindingModule?.key =
                -1
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

    private val boxWidth: Int
        get() {
            return clickGuiScreen.w
        }
    private val boxHeight: Int
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

    private fun getYIteration(iteration: Int): Double {
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
        var catBgColor: Int = colors.categoryBgColor
        if (isMouseOverRect(mouseX.toDouble(), mouseY.toDouble(), x, y, boxWidth, boxHeight)) {
            if (isClickedR) {
                catBgColor = colors.categoryClickColor
                settings.getPositions(categoryString).expanded = !settings.getPositions(categoryString).expanded
                settings.savePositions()
            } else {
                catBgColor = colors.categoryHoverBgColor
            }
        }
        drawTextBox(
            matrixStack,
            xInt,
            yInt,
            boxWidth,
            boxHeight,
            colors.categoryBoxColor,
            colors.categoryTextColor,
            colors.categoryPrefixColor,
            catBgColor,
            colors.categoryPrefix,
            category.toString()
        )
        if (settings.getPositions(category.toString()).expanded) {
            var u = 1
            for (module: Module in MODULE_MANAGER.getModulesInCategory(category)) {
                var moduleTextColor: Int
                var moduleBgColor: Int
                if (module.enabled) {
                    moduleTextColor = colors.moduleOnTextColor
                    moduleBgColor = colors.moduleOnBgColor
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
                            } else {
                                settings.getPositions(categoryString).expandedModules.add(module.label)
                            }
                            settings.savePositions()
                        }
                        else -> {
                            moduleBgColor = colors.moduleHoverBgColor
                        }
                    }
                    description = Description(
                        module.description,
                        (x + boxWidth).roundToInt(),
                        getYIteration(u).roundToInt(),
                        true
                    )
                }
                drawTextBox(
                    matrixStack,
                    xInt,
                    getYIteration(u).roundToInt(),
                    boxWidth,
                    boxHeight,
                    colors.moduleBoxColor,
                    moduleTextColor,
                    colors.modulePrefixColor,
                    moduleBgColor,
                    colors.modulePrefix,
                    module.label
                )
                u++
            }
        }
    }
}