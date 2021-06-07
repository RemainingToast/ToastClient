package dev.toastmc.toastclient.impl.gui.click

import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.api.managers.SettingManager
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.managers.module.ModuleManager
import dev.toastmc.toastclient.api.setting.Setting
import dev.toastmc.toastclient.api.util.KeyUtil
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.render.DrawableExtensions
import dev.toastmc.toastclient.api.util.render.DrawableUtil
import dev.toastmc.toastclient.impl.module.client.ClickGUI
import dev.toastmc.toastclient.impl.module.client.ClickGUI.BACKGROUND_COLOR
import dev.toastmc.toastclient.impl.module.client.ClickGUI.COLOR
import dev.toastmc.toastclient.impl.module.client.ClickGUI.COLOR_HOVER
import dev.toastmc.toastclient.impl.module.client.ClickGUI.FONT_COLOR
import dev.toastmc.toastclient.impl.module.client.ClickGUI.HOVER_COLOR
import dev.toastmc.toastclient.impl.module.client.ClickGUI.INACTIVE_COLOR
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.glfw.GLFW
import java.awt.Rectangle
import kotlin.math.roundToInt

// TODO Themes
class ClickGUIPanel(var category: Module.Category, var x: Double, var y: Double) : IToastClient, DrawableExtensions {

    var width = 100
    var height = 15

    var categoryExpanded = true
    var modsExpanded: HashMap<Module, Boolean> = java.util.HashMap<Module, Boolean>()

    var keybinding = false

    private var level = 1

    private var mouseX = 0.0
    private var mouseY = 0.0

    private var dragging = false
    private var clickedOnce = false
    private var rightClicked = false
    private var leftClicked = false

    private var textScaleCategory = 1f
    private var textScaleModule = 0.90f
    private var textScaleSetting = 0.915f

    private val hoveringCategory: Boolean
        get() {
            return hover(mouseX, mouseY, Rectangle(x.roundToInt(), y.roundToInt() - 3, width, height - 3))
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

            if(hoveringCategory) {
                this.x += dragX
                this.y += dragY
            }
        }
    }

    fun keyReleased(key: Int, scancode: Int) {
        if(keybinding) {
            if (key == GLFW.GLFW_KEY_BACKSPACE ||
                key == GLFW.GLFW_KEY_DELETE ||
                ModuleManager.modules.stream().anyMatch { it.getKey().code == key }
            ) {
                ClickGUI.SCREEN.keybindingModule?.setKey(-1, -1)
            } else {
                ClickGUI.SCREEN.keybindingModule?.setKey(key, -1)
            }

            this.keybinding = false
            ClickGUI.SCREEN.keybindingModule = null
        } else {
            keybinding = true
        }
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
        this.level = 1

        val catHeight = height - 3

        // Category Name
        DrawableUtil.drawCenteredTextBox(
            matrices,
            category.name,
            Rectangle(x.roundToInt(), y.roundToInt() - 3, width, catHeight),
            if (hoveringCategory) COLOR_HOVER else COLOR,
            FONT_COLOR,
            textScaleCategory
        )

        // Background
        val bgHeight = if(categoryExpanded) level() + height * level() - (height - 2) else height / 2 - 2

        DrawableUtil.drawRect(
            matrices,
            x.roundToInt() - 2,
            y.roundToInt() - 4 + catHeight,
            width,
            bgHeight,
            BACKGROUND_COLOR
        )

        if (hoveringCategory) {
            if (rightClicked) {
                categoryExpanded = !categoryExpanded
            }
        }

        if (categoryExpanded) {
            for (mod in ModuleManager.getModulesByCategory(category)!!) {
                drawModule(matrices, mod)
            }
        }
    }

    private fun drawModule(matrices: MatrixStack, mod: Module) {
        val rect = iteration(Rectangle(x.roundToInt(), y.roundToInt() - 6, width - 4, height), level)
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
            if (hovering) COLOR_HOVER else COLOR
        else
            if (hovering) HOVER_COLOR else INACTIVE_COLOR

        DrawableUtil.drawRect(matrices, rect.x, rect.y, rect.width, rect.height, bgColor)
        DrawableUtil.drawText(matrices, mc.textRenderer, lit(mod.getName()), rect.x + 3, rect.y + 3, FONT_COLOR, textScaleModule)
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
        val rect = Rectangle((x + 3).roundToInt(), (y + level + height * level).roundToInt() - 6, width - 10, height)
        val hovering = hover(mouseX, mouseY, rect)

        if(hovering)
            DrawableUtil.drawRect(matrices, rect.x, rect.y, rect.width, rect.height, HOVER_COLOR)

        if(ClickGUI.SCREEN.keybindingModule == mod) {
            DrawableUtil.drawRect(matrices, rect.x, rect.y, rect.width, rect.height, if(hovering) COLOR_HOVER else COLOR)
            DrawableUtil.drawText(matrices, mc.textRenderer, lit("Press a key..."), rect.x + 4, rect.y + 3, FONT_COLOR, textScaleSetting)
        } else {
            DrawableUtil.drawText(matrices, mc.textRenderer, lit("Keybind " + KeyUtil.getKeyName(mod.getKey().code)), rect.x + 4, rect.y + 3, FONT_COLOR, textScaleSetting)
        }

        if(hovering && leftClicked) {
            keybinding = true
            ClickGUI.SCREEN.keybindingModule = mod
        }
    }

    private fun drawGroup(matrices: MatrixStack, group: Setting.Group) {
        val rect = Rectangle((x + 3).roundToInt(), (y + level + height * level).roundToInt() - 6, width - 10, height)
        val hovering = hover(mouseX, mouseY, rect)
        val str = if (group.isExpanded) "_" else "..."
        val i = if (group.isExpanded) 1 else 0

        DrawableUtil.drawRect(matrices, rect.x, rect.y, rect.width, rect.height - i, if(hovering) COLOR_HOVER else COLOR)
        DrawableUtil.drawText(matrices, mc.textRenderer, lit(group.name), rect.x + 4, rect.y + 3, FONT_COLOR,textScaleSetting)
        DrawableUtil.drawText(matrices, mc.textRenderer, lit(str), rect.x + (rect.width - mc.textRenderer.getWidth(str)) - 5, rect.y + 3, FONT_COLOR,textScaleSetting)

        if(hovering && rightClicked) {
            group.isExpanded = !group.isExpanded
        }
    }

    private fun drawBoolean(matrices: MatrixStack, bool: Setting.Boolean) {
        val rect = Rectangle((x + 3).roundToInt(), (y + level + height * level).roundToInt() - 6, width - 10, height + 1)
        val hovering = hover(mouseX, mouseY, rect)
        val i = if (bool.isGrouped) 1 else 0
        val t = if (bool.isGrouped) 2 else 0

        if(hovering)
            DrawableUtil.drawRect(matrices, rect.x + t, rect.y - i, rect.width - (t * 2), rect.height, if(bool.enabled()) COLOR_HOVER else HOVER_COLOR)
        if(bool.enabled())
            DrawableUtil.drawRect(matrices, rect.x + t, rect.y - i, rect.width - (t * 2), rect.height, if(hovering) COLOR_HOVER else COLOR)
        DrawableUtil.drawText(matrices, mc.textRenderer, lit(bool.name), rect.x + 4 + t, rect.y + 3 - i, FONT_COLOR, textScaleSetting)

        if (hovering && leftClicked) {
            bool.value = !bool.value
        }
    }

    private fun drawSlider(matrices: MatrixStack, setting: Setting.Number) {
        val rect = Rectangle((x + 3).roundToInt(), (y + level + height * level).roundToInt() - 6, width - 10, height + 1)
        val hovering = hover(mouseX, mouseY, rect)
        val i = if (setting.isGrouped) 1 else 0
        val t = if (setting.isGrouped) 2 else 0
        val progress = ((setting.value - setting.min) / (setting.max - setting.min) * (rect.width - (t * 2))).toInt()

        if (setting.value != setting.min)
            DrawableUtil.drawRect(matrices, rect.x + t, rect.y - i, progress, rect.height, if(hovering) COLOR_HOVER else COLOR)
        if (hovering)
            DrawableUtil.drawRect(matrices, rect.x + progress + t, rect.y - i, rect.width - progress - (t * 2), rect.height, HOVER_COLOR)
        DrawableUtil.drawText(matrices, mc.textRenderer, lit(setting.name), rect.x + 4 + t, rect.y + 3, FONT_COLOR, textScaleSetting)
        DrawableUtil.drawText(matrices, mc.textRenderer, lit(setting.stringValue), rect.x + (rect.width - mc.textRenderer.getWidth(setting.stringValue)) - 5 + t,rect.y + 3, FONT_COLOR, textScaleSetting)

        if(hovering && (dragging || leftClicked)) {
            setting.value = clamp(
                round(((mouseX - x) * 100 / (width - 10)).roundToInt() * ((setting.max - setting.min) / 100) + setting.min, setting.precision),
                setting.min,
                setting.max
            )
        }
    }

    private fun drawMode(matrices: MatrixStack, mode: Setting.Mode) {
        val rect = Rectangle((x + 3).roundToInt(), (y + level + height * level).roundToInt() - 6, width - 10, height + 1)
        val hovering = hover(mouseX, mouseY, rect)

        DrawableUtil.drawText(matrices, mc.textRenderer, lit(mode.name), rect.x + 4, rect.y + 3, FONT_COLOR, textScaleSetting)
        DrawableUtil.drawText(matrices, mc.textRenderer, lit(mode.stringValue), rect.x + (rect.width - mc.textRenderer.getWidth(lit(mode.stringValue))), rect.y + 3, FONT_COLOR, textScaleSetting)

        if (hovering) {
            when {
                leftClicked -> mode.increment()
                rightClicked -> mode.decrement()
            }
        }
    }

    private fun drawColorPicker(matrices: MatrixStack, colorSetting: Setting.ColorSetting) {
        //TODO
    }

    fun level(): Int {
        var l = 1
        for ((module, expanded) in modsExpanded){
            l++

            if (expanded) {
                for (setting in SettingManager.getSettingsForMod(module)) {
                    if (!setting.isHidden) {
                        if (setting.type == Setting.Type.GROUP) {
                            l++

                            if ((setting as Setting.Group).isExpanded) {
                                for (subSetting in setting.settings) {
                                    l++
                                }
                            }
                        } else if (!setting.isGrouped) {
                            l++
                        }
                    }
                }
                l++ // Keybind
            }
        }

        return l
    }
}