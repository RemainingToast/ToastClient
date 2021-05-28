package dev.toastmc.toastclient.impl.clickgui

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.lit
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import java.util.*

class ClickGUIScreen : Screen(lit("ClickGUI")) {

    private val panels: EnumMap<Module.Category, ClickGUIPanel> = EnumMap(Module.Category::class.java)

    private var clickedOnce = false
    private var leftClicked = false
    private var rightClicked = false

    init {
        var x = 20
        for (category in Module.Category.values()) {
            if(category == Module.Category.NONE) continue
            panels.putIfAbsent(category, ClickGUIPanel(category, x.toDouble(), 20.0))
            x += 93
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        for (panel in panels.values) {
            panel.render(matrices, mouseX.toDouble(), mouseY.toDouble(), leftClicked, rightClicked)
        }
        if (clickedOnce) {
            leftClicked = false
            rightClicked  = false
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
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
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if(button == 0 || button == 1) {
            leftClicked = false
            rightClicked  = false
            clickedOnce = false
        }
        return false
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (button == 0) {
            for (panel in panels.values) {
                panel.updatePosition(deltaX, deltaY)
            }
        }
        return false
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        for (panel in panels.values) {
            panel.updateMousePos(mouseX, mouseY)
        }
    }

    override fun isPauseScreen(): Boolean {
        return false
    }
}