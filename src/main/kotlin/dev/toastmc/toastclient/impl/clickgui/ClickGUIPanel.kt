package dev.toastmc.toastclient.impl.clickgui

import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.api.managers.SettingManager
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.managers.module.ModuleManager
import dev.toastmc.toastclient.api.setting.Setting
import dev.toastmc.toastclient.api.util.TwoDRenderUtil
import net.minecraft.client.util.math.MatrixStack
import java.awt.Rectangle
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt







class ClickGUIPanel(category: Module.Category, var x: Double, var y: Double) : IToastClient {

    var width = 90
    var height = 12

    var category: Module.Category = Module.Category.NONE
    var categoryExpanded = false
    var modsExpanded: HashMap<Module, Boolean> = java.util.HashMap<Module, Boolean>()

    private var level = 0

    private var mouseX = 0.0
    private var mouseY = 0.0

    private var dragging = false
    private var clickedOnce = false
    private var rightClicked = false
    private var leftClicked = false

    private val hovering: Boolean
        get() {
            return hover(mouseX, mouseY, Rectangle(x.roundToInt(), y.roundToInt(), width, height))
        }

    init {
        this.category = category
    }

    fun render(matrices: MatrixStack, mouseX: Double, mouseY: Double) {
        this.mouseX = mouseX
        this.mouseY = mouseY

        drawCategory(matrices, category)

        if(this.clickedOnce) {
            this.leftClicked = false
            this.rightClicked = false
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int) {
        if(!clickedOnce) {
            if (button == 0) {
                leftClicked = true
                rightClicked = false
                clickedOnce = true
            } else if (button == 1) {
                leftClicked = false
                rightClicked  = true
                clickedOnce = true
            }
        } else {
            leftClicked = false
            rightClicked  = false
        }
    }

    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if(button == 0 || button == 1) {
            leftClicked = false
            rightClicked  = false
            clickedOnce = false
            dragging = false
        }
    }

    fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double) {
        if (button == 0) {
            this.dragging = true

            if(hovering) {
                this.x += dragX
                this.y += dragY
            }
        }
    }

    private fun drawCategory(matrices: MatrixStack, category: Module.Category) {
        level = 1

        TwoDRenderUtil.drawCenteredTextBox(
            matrices,
            category.name,
            Rectangle(x.roundToInt(), y.roundToInt(), width, height),
            if (hovering) -0x66ff0100 else -0x7fff0100,
            -0x1
        )

        if (hovering) {
            if (rightClicked) {
                categoryExpanded = !categoryExpanded
            }
        }

        if (categoryExpanded) {
            for (mod in ModuleManager.getModulesByCategory(category)!!) {
                drawModule(matrices, mod)
            }
        }

        // Outline
        TwoDRenderUtil.drawHollowRect(matrices, (x - 2).roundToInt(), (y - 2).roundToInt(), width, level + height * level, 1, -0x7fff0100)
    }

    private fun drawModule(matrices: MatrixStack, mod: Module) {
        val hoveringMod = hover(mouseX, mouseY, iteration(Rectangle(x.roundToInt(), y.roundToInt(), width, height), level))

        modsExpanded.putIfAbsent(mod, false)

        if (hoveringMod) {
            if (leftClicked) {
                mod.toggle()
            }
            if (rightClicked) {
                modsExpanded[mod] = !modsExpanded[mod]!!
            }
        }

        val bgColor = if (mod.isEnabled()) if (hoveringMod) -0x66ff0100 else -0x7fff0100 else if (hoveringMod) -0x80000000 else 0x50000000
        val rect = iteration(Rectangle(x.roundToInt(), y.roundToInt(), width, height), level)

        TwoDRenderUtil.drawTextBox(matrices, mod.getName(), rect, bgColor, -0x1)
        level++

        for ((module, expanded) in modsExpanded) {
            if(expanded) {
                for (setting in SettingManager.getSettingsForMod(module)) {
                    if(setting.parent == mod && !setting.isHidden) {
                        if(setting.type == Setting.Type.GROUP) {
                            val groupSetting = setting as Setting.Group

                            drawGroup(matrices, groupSetting)
                            level++

                            if(groupSetting.isExpanded) {
                                for(subSetting in groupSetting.settings) {
                                    drawSetting(matrices, subSetting)
                                    level++
                                }
                            }

                        } else if(!setting.isGrouped) {
                            drawSetting(matrices, setting)
                            level++
                        }
                    }
                }
            }
        }
    }

    private fun drawSetting(matrices: MatrixStack, setting: Setting<*>) {
        when (setting.type) {
            Setting.Type.BOOLEAN -> return drawBoolean(matrices, setting as Setting.Boolean)
            Setting.Type.NUMBER -> return drawSlider(matrices, setting as Setting.Number)
            Setting.Type.MODE -> return drawMode(matrices, setting as Setting.Mode)
            else -> return
        }
    }

    private fun drawGroup(matrices: MatrixStack, group: Setting.Group) {
        val rect = iteration(Rectangle(x.roundToInt(), y.roundToInt(), width, height), level)
        val str = if (group.isExpanded) "_" else "..."

        TwoDRenderUtil.drawRect(matrices, rect.x, rect.y - 2, rect.width - 2, rect.height, if (hover(mouseX, mouseY, rect)) -0x66ff0100 else -0x7fff0100)
        TwoDRenderUtil.drawRect(matrices, rect.x - 2, rect.y - 3, 2, rect.height + 1, -0x7fff0100)
        TwoDRenderUtil.drawText(matrices, group.name, rect.x + 2, rect.y, -0x1)
        TwoDRenderUtil.drawText(matrices, str, rect.x + (rect.width - mc.textRenderer.getWidth(str)) - 5, rect.y, -0x1)
    }

    private fun drawBoolean(matrices: MatrixStack, bool: Setting.Boolean) {
        val rect = iteration(Rectangle(x.roundToInt(), y.roundToInt(), width, height), level)
        val hovering = hover(mouseX, mouseY, rect)
        val color = if (bool.value) if (hovering) -0x66ff0100 else -0x7fff0100 else if (hovering) -0x80000000 else 0x50000000

        TwoDRenderUtil.drawRect(matrices, rect.x, rect.y - 2, rect.width - 2, rect.height, color)
        TwoDRenderUtil.drawRect(matrices, rect.x - 2, rect.y - 3, 2, rect.height + 1, -0x7fff0100)
        TwoDRenderUtil.drawText(matrices, bool.name, rect.x + 2, rect.y, -0x1)

        if (hovering && leftClicked) {
            bool.value = !bool.value
        }
    }

    private fun drawSlider(matrices: MatrixStack, setting: Setting.Number) {
        val rect = iteration(Rectangle(x.roundToInt(), y.roundToInt(), width, height), level)
        val hovering = hover(mouseX, mouseY, rect)

        val percentage = (setting.value - setting.min) / (setting.max - setting.min)
        val progress = (percentage * rect.width).toInt()

        if (setting.value != setting.min) {
            TwoDRenderUtil.drawRect(matrices, rect.x, rect.y - 2, progress - 2, rect.height, -0x7fff0100)
        }

        TwoDRenderUtil.drawRect(matrices, rect.x - 2 + progress, rect.y - 2, rect.width - progress, rect.height, if (hovering) -0x80000000 else 0x50000000)
        TwoDRenderUtil.drawRect(matrices, rect.x - 2, rect.y - 3, 2, rect.height + 1, if (hovering) -0x66ff0100 else -0x7fff0100)
        TwoDRenderUtil.drawText(matrices, setting.name, rect.x + 2, rect.y, -0x1)
        TwoDRenderUtil.drawText(matrices, setting.stringValue, rect.x + (rect.width - mc.textRenderer.getWidth(setting.stringValue)) - 5, rect.y, -0x1)

        if(hovering && (dragging || leftClicked)) {
            val percent: Int = ((mouseX - x) * 100 / (width - 2)).roundToInt()
            setting.value = clamp(round(percent * ((setting.max - setting.min) / 100) + setting.min, setting.precision), setting.min, setting.max)
        }
    }

    private fun drawMode(matrices: MatrixStack, mode: Setting.Mode) {
        val rect = iteration(Rectangle(x.roundToInt(), y.roundToInt(), width, height), level)
        val hovering = hover(mouseX, mouseY, rect)

        TwoDRenderUtil.drawRect(matrices, rect.x, rect.y - 2, rect.width - 2, rect.height, if (hovering) -0x80000000 else 0x50000000)
        TwoDRenderUtil.drawRect(matrices, rect.x - 2, rect.y - 3, 2, rect.height + 1, if (hovering) -0x66ff0100 else -0x7fff0100)
        TwoDRenderUtil.drawText(matrices, mode.name, rect.x + 2, rect.y, -0x1)
        TwoDRenderUtil.drawText(matrices, mode.stringValue, rect.x + (rect.width - mc.textRenderer.getWidth(mode.stringValue)) - 5, rect.y, -0x1)

        if (hovering && leftClicked) {
            mode.increment()
        }
    }

    private fun round(value: Double, places: Int): Double {
        var bd = BigDecimal(value)
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

    fun clamp(value: Double, min: Double, max: Double): Double {
        return min.coerceAtLeast(max.coerceAtMost(value))
    }

    private fun hover(mouseX: Double, mouseY: Double, rect: Rectangle): Boolean {
        return mouseX >= rect.x && mouseX <= rect.width + rect.x && mouseY >= rect.y && mouseY <= rect.height + rect.y
    }

    private fun iteration(rect: Rectangle, iteration: Int): Rectangle {
        return Rectangle(
            rect.x + 1,
            rect.y + iteration + rect.height * iteration,
            rect.width - 2,
            rect.height
        )
    }
}