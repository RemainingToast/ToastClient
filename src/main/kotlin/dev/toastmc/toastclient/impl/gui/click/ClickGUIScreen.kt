package dev.toastmc.toastclient.impl.gui.click

import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.api.config.ConfigSaver
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.lit
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import java.util.*

class ClickGUIScreen : Screen(lit("ClickGUI")), IToastClient {

    private val panels: EnumMap<Module.Category, ClickGUIPanel> = EnumMap(Module.Category::class.java)

    var keybindingModule: Module? = null

    init {
        var x = 20
        for (category in Module.Category.values()) {
            if(category == Module.Category.NONE) continue

            panels.putIfAbsent(category, ClickGUIPanel(category, x.toDouble(), 20.0))
            x += 103
        }
    }

    override fun onClose() {
        ConfigSaver.saveModules()
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        for (panel in panels.values) {
            panel.render(matrices, mouseX.toDouble(), mouseY.toDouble())
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (panel in panels.values) {
            panel.mouseClicked(mouseX, mouseY, button)
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (panel in panels.values) {
            panel.mouseReleased(mouseX, mouseY, button)
        }
        return false
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        for (panel in panels.values) {
            panel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        }
        return false
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        for (panel in panels.values) {
            panel.keyReleased(keyCode, scanCode)
        }
        return false
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun shouldCloseOnEsc(): Boolean {
        return true
    }
}