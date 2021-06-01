package dev.toastmc.toastclient.impl.gui.hud

import dev.toastmc.toastclient.api.util.TwoDRenderUtil
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.impl.gui.hud.components.Watermark
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import java.util.*
import kotlin.math.roundToInt

class HUDEditorScreen : Screen(lit("HUDEditor")) {

    private val components: ArrayList<HUDComponent> = ArrayList()

    init {
        components.add(Watermark)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        TwoDRenderUtil.drawRect(matrices, 1, 1, 1, 1, -1)

        for (component in components) {
            if (component.enabled) {
                component.updateMouse(mouseX.toDouble(), mouseY.toDouble())

                TwoDRenderUtil.drawHollowRect(
                    matrices,
                    component.x.roundToInt(),
                    component.y.roundToInt(),
                    component.width,
                    component.height,
                    1,
                    if(component.dragging) 0x90303030.toInt() else 0x75101010
                )

                component.render(matrices)
            }
        }
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (component in components) {
            component.mouseReleased(mouseX, mouseY, button)
        }
        return false
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        for (component in components) {
            component.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        }
        return false
    }

    fun getComponents(): ArrayList<HUDComponent> {
        return components
    }

}