package me.remainingtoast.toastclient.api.gui

import com.lukflug.panelstudio.CollapsibleContainer
import com.lukflug.panelstudio.Context
import com.lukflug.panelstudio.DraggableContainer
import com.lukflug.panelstudio.SettingsAnimation
import com.lukflug.panelstudio.hud.HUDClickGUI
import com.lukflug.panelstudio.hud.HUDPanel
import com.lukflug.panelstudio.mc16.MinecraftHUDGUI
import com.lukflug.panelstudio.settings.*
import com.lukflug.panelstudio.theme.FixedDescription
import com.lukflug.panelstudio.theme.SettingsColorScheme
import com.lukflug.panelstudio.theme.Theme
import me.remainingtoast.toastclient.ToastClient
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.HUDModule
import me.remainingtoast.toastclient.api.setting.type.ColorSetting
import me.remainingtoast.toastclient.client.module.client.ClickGUIModule
import me.remainingtoast.toastclient.client.module.client.Colors
import me.remainingtoast.toastclient.client.module.client.Font
import net.minecraft.client.MinecraftClient
import net.minecraft.text.LiteralText
import net.minecraft.util.Identifier
import java.awt.Color
import java.awt.Point


class ToastGUI(boolean: Boolean) : MinecraftHUDGUI() {

    private var colorToggle: Toggleable = SimpleToggleable(boolean)
    lateinit var guiInterface: GUIInterface
    private lateinit var theme: Theme
    private lateinit var gui: HUDClickGUI
    private val WIDTH = 100
    private val HEIGHT = 12
    private val HUD_BORDER = 2

    constructor() : this(false) {
        guiInterface = object : GUIInterface(true) {
            override fun getResourcePrefix(): String {
                return "toastclient:gui/"
            }

            override fun drawString(pos: Point, s: String, c: Color) {
                if (matrixStack == null) return
                end()
                val text = LiteralText(s).styled { style -> style.withFont(Font.getFontFromType(Font.fontType.value)) }
                MinecraftClient.getInstance().textRenderer.drawWithShadow(
                        matrixStack,
                        text,
                        (pos.x + 2).toFloat(),
                        (pos.y + 2).toFloat(),
                        c.rgb
                )
                begin()
            }

            override fun getFontWidth(s: String): Int {
                return MinecraftClient.getInstance().textRenderer.getWidth(s) + 4
            }

            override fun getFontHeight(): Int {
                return MinecraftClient.getInstance().textRenderer.fontHeight + 4
            }
        }
        theme = ToastTheme(
                SettingsColorScheme(
                        Colors.activeColor,
                        Colors.inactiveColor,
                        Colors.backgroundColor,
                        Colors.outlineColor,
                        Colors.fontColor,
                        Colors.opacity
                ), HEIGHT, 2, 5
        )
        gui = HUDClickGUI(guiInterface, FixedDescription(Point(0, 0)))

        val hudToggle: Toggleable = object : Toggleable {
            override fun toggle() {}
            override fun isOn(): Boolean {
                return hudEditor
            }
        }

        for (module in ToastClient.MODULE_MANAGER.modules) {
            if (module is HUDModule) {
                module.populate(theme)
                gui.addHUDComponent(
                        HUDPanel(
                                module.component,
                                theme.panelRenderer,
                                module,
                                SettingsAnimation(ClickGUIModule.animationSpeed),
                                hudToggle,
                                HUD_BORDER
                        )
                )
            }
        }

        var x = 10
        for (category in Category.values()) {
            if(category == Category.NONE) continue
            if(ToastClient.MODULE_MANAGER.getModulesByCategory(category)!!.size == 0) continue
            val panel = object : DraggableContainer(
                    category.toString(),
                    null,
                    theme.panelRenderer,
                    SimpleToggleable(false),
                    SettingsAnimation(ClickGUIModule.animationSpeed),
                    null,
                    Point(x, 10),
                    WIDTH
            ) {
                override fun render(context: Context) {
                    if(category!=Category.HUD || hudEditor) super.render(context)
                }
                override fun handleKey(context: Context, scancode: Int) {
                    if(category!=Category.HUD || hudEditor) super.handleKey(context, scancode)
                }
            }

            if(category!=Category.HUD) gui.addComponent(panel)
            else gui.addHUDComponent(panel)
            for (module in ToastClient.MODULE_MANAGER.getModulesByCategory(category)!!) {
                val container = CollapsibleContainer(
                        module.name,
                        null,
                        theme.containerRenderer,
                        SimpleToggleable(false),
                        SettingsAnimation(ClickGUIModule.animationSpeed),
                        module
                )
                panel.addComponent(container)
                for (setting in ToastClient.SETTING_MANAGER.getSettingsForModule(module)) {
                    when (setting) {
                        is Toggleable -> container.addComponent(BooleanComponent(
                                setting.name,
                                setting.description,
                                theme.componentRenderer,
                                setting as Toggleable
                        )
                        )
                        is NumberSetting -> container.addComponent(NumberComponent(
                                setting.name,
                                setting.description,
                                theme.componentRenderer,
                                setting as NumberSetting,
                                (setting as NumberSetting).minimumValue,
                                (setting as NumberSetting).maximumValue
                        )
                        )
                        is EnumSetting -> container.addComponent(EnumComponent(
                                setting.name,
                                setting.description,
                                theme.componentRenderer,
                                setting as EnumSetting
                        )
                        )
                        is ColorSetting -> container.addComponent(ColorComponent(
                                setting.name,
                                setting.description,
                                theme.containerRenderer,
                                SettingsAnimation(ClickGUIModule.animationSpeed),
                                theme.componentRenderer,
                                setting,
                                setting.alphaEnabled,
                                setting.rainbowEnabled,
                                colorToggle
                        )
                        )
                    }
                }
                if(module !is HUDModule){
                    container.addComponent(ToastKeybind(theme.componentRenderer, module))
                }
            }
            x += WIDTH + 10
        }
    }

    override fun getHUDGUI(): HUDClickGUI {
        return gui
    }

    override fun getInterface(): GUIInterface {
        return guiInterface
    }

    override fun getScrollSpeed(): Int {
        return ClickGUIModule.scrollSpeed.value
    }
}