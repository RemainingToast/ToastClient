package dev.toastmc.toastclient.impl.clickgui

import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.api.managers.SettingManager
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.managers.module.ModuleManager
import dev.toastmc.toastclient.api.setting.Setting
import dev.toastmc.toastclient.api.util.KeyUtil
import dev.toastmc.toastclient.api.util.TwoDRenderUtil
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.glfw.GLFW
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

    var keybinding = false
    var keyPressed = false
    var keybindingModule: Module? = null

    private var level = 1

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

    fun keyPressed(key: Int) {
        if(keybinding) {
            if(key == -1) return
            else if (key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE) keybindingModule!!.setKey(key, -1)
            this.keyPressed = true
        } else keybinding = true
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

    private fun drawCategory(matrices: MatrixStack, category: Module.Category) {
        TwoDRenderUtil.drawCenteredTextBox(
            matrices,
            category.name,
            Rectangle(x.roundToInt(), y.roundToInt(), width, height),
            if (hovering) -0x66ff0100 else -0x7fff0100,
            -0x1
        )

        /**
         * level is 1 when it reaches this point
         * and if I run it after the for loop drawing modules the level is correct but the rect is drawn over the modules..
         */
        TwoDRenderUtil.drawRect(
            matrices,
            Rectangle(x.roundToInt() - 2, y.roundToInt() + height - 1, width,
                if(categoryExpanded) level + height * level else height / 2
            ),
            -0x80000000
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

        this.level = 1
    }

    private fun drawModule(matrices: MatrixStack, mod: Module) {
        val rect = iteration(Rectangle(x.roundToInt(), y.roundToInt(), width, height), level)
        val hovering = hover(mouseX, mouseY, rect)

        modsExpanded.putIfAbsent(mod, false)

        if (hovering) {
            if (leftClicked) {
                mod.toggle()
            }
            if (rightClicked) {
                modsExpanded[mod] = !modsExpanded[mod]!!
            }
        }

        val bgColor = if (mod.isEnabled())
            if (hovering) -0x66ff0100 else -0x7fff0100
        else
            if (hovering) -0x80000000 else 0x50000000

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
                if(module == mod) {
                    drawKeybinding(matrices, mod)
                    level++
                }
            }
        }
    }

    private fun drawSetting(matrices: MatrixStack, setting: Setting<*>) {
        when (setting.type) {
            Setting.Type.BOOLEAN -> return drawBoolean(matrices, setting as Setting.Boolean)
            Setting.Type.NUMBER -> return drawSlider(matrices, setting as Setting.Number)
            Setting.Type.MODE -> return drawMode(matrices, setting as Setting.Mode)
            Setting.Type.COLOR -> return drawColorPicker(matrices, setting as Setting.ColorSetting)
            else -> return
        }
    }

    private fun drawKeybinding(matrices: MatrixStack, mod: Module) {
        val rect = iteration(Rectangle(x.roundToInt(), y.roundToInt(), width, height), level)
        val hovering = hover(mouseX, mouseY, rect)
        val str = if(keybindingModule == mod) "Bind: ..." else "Bind: " + KeyUtil.getKeyName(mod.getKey().code)

        TwoDRenderUtil.drawTextBox(matrices, str, rect, -1, -1)

        if(hovering && leftClicked) {
            this.keybinding = true
            this.keybindingModule = mod
        }

        if(keybindingModule == mod) {
            if(!keyPressed) {
                keybinding = true
                keybindingModule = mod
            }
        }
    }

    private fun drawGroup(matrices: MatrixStack, group: Setting.Group) {
        val rect = iteration(Rectangle(x.roundToInt(), y.roundToInt(), width, height), level)
        val hovering = hover(mouseX, mouseY, rect)
        val str = if (group.isExpanded) "_" else "..."

        TwoDRenderUtil.drawRect(
            matrices,
            rect.x, rect.y - 2, rect.width - 2, rect.height,
            if (hovering) ClickGUI.getMainColor().brighter().rgb else ClickGUI.getMainColor().rgb
        )

        TwoDRenderUtil.drawRect(
            matrices,
            rect.x - 2, rect.y - 3, 2, rect.height + 1,
            ClickGUI.getMainColor().rgb
        )
        TwoDRenderUtil.drawStringWithShadow(matrices, group.name, rect.x + 2, rect.y, -0x1)
        TwoDRenderUtil.drawStringWithShadow(matrices, str, rect.x + (rect.width - mc.textRenderer.getWidth(str)) - 5, rect.y, -0x1)

        if(hovering && rightClicked) {
            group.isExpanded = !group.isExpanded
        }
    }

    private fun drawBoolean(matrices: MatrixStack, bool: Setting.Boolean) {
        val rect = iteration(Rectangle(x.roundToInt(), y.roundToInt(), width, height), level)
        val hovering = hover(mouseX, mouseY, rect)
        val color = if (bool.value) if (hovering) ClickGUI.getMainColor().brighter().rgb else ClickGUI.getMainColor().rgb else if (hovering) ClickGUI.getModuleColor().brighter().rgb else ClickGUI.getModuleColor().rgb

        TwoDRenderUtil.drawRect(matrices, rect.x, rect.y - 2, rect.width - 2, rect.height, color)
        TwoDRenderUtil.drawRect(matrices, rect.x - 2, rect.y - 3, 2, rect.height + 1, ClickGUI.getMainColor().rgb)
        TwoDRenderUtil.drawStringWithShadow(matrices, bool.name, rect.x + 2, rect.y, -0x1)

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
        TwoDRenderUtil.drawStringWithShadow(matrices, setting.name, rect.x + 2, rect.y, -0x1)
        TwoDRenderUtil.drawStringWithShadow(matrices, setting.stringValue, rect.x + (rect.width - mc.textRenderer.getWidth(setting.stringValue)) - 5, rect.y, -0x1)

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
        TwoDRenderUtil.drawStringWithShadow(matrices, mode.name, rect.x + 2, rect.y, -0x1)
        TwoDRenderUtil.drawStringWithShadow(matrices, mode.stringValue, rect.x + (rect.width - mc.textRenderer.getWidth(mode.stringValue)) - 5, rect.y, -0x1)

        if (hovering && leftClicked) {
            mode.increment()
        }
    }

    private fun drawColorPicker(matrices: MatrixStack, colorSetting: Setting.ColorSetting) {
//        val rect = iteration(Rectangle(x.roundToInt(), y.roundToInt(), width, height), level)
//
//        val hue = colorSetting.value.hue
//        val saturation = colorSetting.value.saturation
//        val brightness = colorSetting.value.brightness
//
//        val colorA = ToastColor.fromHSB(hue, 0f, 1f)
//        val colorB = ToastColor.fromHSB(hue, 1f, 1f)
//
//        TwoDRenderUtil.fillRect(rect, colorA, colorB, colorB, colorA)
//
//        val colorC = ToastColor(0, 0, 0, 0)
//        val colorD = ToastColor(0, 0, 0)
//
//        TwoDRenderUtil.fillRect(rect, colorC, colorC, colorD, colorD)
//
//        val p = Point((x + hue * width).roundToInt(), (y + height - saturation * height).roundToInt())
//
//        val fontColor = ToastColor(-0x1)
//
//        TwoDRenderUtil.fillRect(Rectangle(p.x, p.y - 2, 1, 2 * 2 + 1), fontColor, fontColor, fontColor, fontColor)
//        TwoDRenderUtil.fillRect(Rectangle(p.x - 2, p.y, 2 * 2 + 1, 1), fontColor, fontColor, fontColor, fontColor)

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