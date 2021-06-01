package dev.toastmc.toastclient.impl.gui.hud

import dev.toastmc.toastclient.IToastClient
import net.minecraft.client.util.math.MatrixStack
import java.awt.Rectangle
import kotlin.math.roundToInt

open class HUDComponent(var name: String, var x: Double, var y: Double) : IToastClient {

    var enabled = false
    var dragging = false

    var width = 0
    var height = 0

    private var mouseX = 0.0
    private var mouseY = 0.0

    private val hovering: Boolean
        get() {
            return hover(mouseX, mouseY, Rectangle(x.roundToInt(), y.roundToInt(), width, height))
        }

    fun updateMouse(mouseX: Double, mouseY: Double) {
        this.mouseX = mouseX
        this.mouseY = mouseY
    }

    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if(button == 0 || button == 1) {
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

    open fun render(matrices: MatrixStack) {

    }

    private fun hover(mouseX: Double, mouseY: Double, rect: Rectangle): Boolean {
        return mouseX >= rect.x && mouseX <= rect.width + rect.x && mouseY >= rect.y && mouseY <= rect.height + rect.y
    }

}