package dev.toastmc.toastclient.impl.gui.hud

import dev.toastmc.toastclient.api.config.ConfigSaver
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.impl.gui.hud.components.*
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import java.util.*

class HUDEditorScreen : Screen(lit("HUDEditor")) {

    private val components: ArrayList<HUDComponent> = ArrayList()

    init {
        components.add(Watermark)
        components.add(Totems)
        components.add(Welcomer)
        components.add(Coordinates)
        components.add(FPS)
        components.add(TPS)
        components.add(Time)
        components.add(Armor)
    }

    override fun onClose() {
        ConfigSaver.saveComponents()
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        for (component in components) {
            component.updateMouse(mouseX.toDouble(), mouseY.toDouble())
            component.renderEditor(matrices)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (component in components) {
            component.mouseClicked(mouseX, mouseY, button)
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (component in components) {
            component.mouseReleased(mouseX, mouseY, button)
        }
        return false
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        for (component in components) {
            if(component.hovering || component.labelHover) {
                component.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
                components.remove(component)
                components.add(0, component)
                break
            }
        }
        return false
    }

    fun getComponents(): ArrayList<HUDComponent> {
        return components
    }

    override fun shouldCloseOnEsc(): Boolean {
        return true
    }

}