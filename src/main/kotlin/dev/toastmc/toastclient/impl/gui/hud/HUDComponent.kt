package dev.toastmc.toastclient.impl.gui.hud

import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.api.util.TwoDRenderUtil
import net.minecraft.client.util.math.MatrixStack
import java.awt.Color
import java.awt.Rectangle
import kotlin.math.roundToInt

open class HUDComponent(var name: String, var x: Double, var y: Double) : IToastClient {

    var enabled = false

    var width = 0
    var height = 0

    private var mouseX = 0.0
    private var mouseY = 0.0
    private var dragX = 0.0
    private var dragY = 0.0

    private var dragging = false
    private var clickedOnce = false
    private var rightClicked = false
    private var leftClicked = false
    private var labelHover = false

    private val hovering: Boolean
        get() {
            return hover(mouseX, mouseY, Rectangle(x.roundToInt(), y.roundToInt(), width, height))
        }

    fun updateMouse(mouseX: Double, mouseY: Double) {
        this.mouseX = mouseX
        this.mouseY = mouseY
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
            this.dragX = dragX
            this.dragY = dragY

            if(hovering || labelHover) {
                this.x += this.dragX
                this.y += this.dragY
            }
        }
    }

    open fun render(matrices: MatrixStack) {

    }

    open fun renderEditor(matrices: MatrixStack) {
        val buttonRect = Rectangle(x.roundToInt() + width - 5, y.roundToInt() - height, 5, 5)
        val hoverButton = hover(mouseX, mouseY, buttonRect)

        TwoDRenderUtil.drawRect(
            matrices,
            buttonRect,
            if(enabled) Color.green.rgb else Color.red.rgb
        )

        val labelRect = Rectangle(x.roundToInt(), y.roundToInt() - height, width, height * 2)
        labelHover = hover(mouseX, mouseY, Rectangle(x.roundToInt(), y.roundToInt() - height, width, height * 2))

        TwoDRenderUtil.drawRect(
            matrices,
            labelRect,
            if(dragging) 0x90303030.toInt() else 0x75101010
        )

        TwoDRenderUtil.drawHollowRect(
            matrices,
            x.roundToInt(),
            y.roundToInt() - height,
            width,
            height * 2,
            1,
            -0x6
        )

        if (hoverButton && leftClicked) {
            enabled = !enabled
        }

        if(clickedOnce) {
            leftClicked = false
            rightClicked = false
        }

        render(matrices)
    }

    private fun hover(mouseX: Double, mouseY: Double, rect: Rectangle): Boolean {
        return mouseX >= rect.x && mouseX <= rect.width + rect.x && mouseY >= rect.y && mouseY <= rect.height + rect.y
    }

}