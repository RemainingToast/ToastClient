package dev.toastmc.toastclient.impl.gui.hud

import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.api.util.ToastColor
import dev.toastmc.toastclient.api.util.render.DrawableUtil
import net.minecraft.client.util.math.MatrixStack
import java.awt.Color
import java.awt.Point
import java.awt.Rectangle
import kotlin.math.roundToInt

open class HUDComponent(var name: String) : IToastClient {

    var enabled = false
    var x = 0.0
    var y = 0.0
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
        // Background
        DrawableUtil.drawRect(
            matrices,
            Rectangle(x.roundToInt(), y.roundToInt(), width, height),
            ToastColor(if(dragging) 0x90303030.toInt() else 0x75101010, true)
        )

        // Outline
        DrawableUtil.drawHollowRect(
            matrices,
            x.roundToInt(),
            y.roundToInt(),
            width,
            height,
            1,
            ToastColor(-0x000000)
        )

        // Render HUDComponent
        render(matrices)

        // Button
        val rect = Rectangle(x.roundToInt() + width - 5, y.roundToInt(), 5, 5)
        val hover = hover(mouseX, mouseY, rect)

        if (hover && leftClicked) {
            enabled = !enabled
        }

        DrawableUtil.drawRect(
            matrices,
            rect,
            ToastColor(if(enabled) Color.green else Color.red)
        )

        // Keep Last
        if(clickedOnce) {
            leftClicked = false
            rightClicked = false
        }
    }

    private fun hover(mouseX: Double, mouseY: Double, rect: Rectangle): Boolean {
        return mouseX >= rect.x && mouseX <= rect.width + rect.x && mouseY >= rect.y && mouseY <= rect.height + rect.y
    }

    enum class SnapPoint {
        TOP_RIGHT,
        TOP_LEFT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT;
    }

    fun getSnapPoint(snapPoint: SnapPoint, width: Int, height: Int): Point {
        when (snapPoint) {
            SnapPoint.TOP_RIGHT -> {
                return Point(
                    mc.window.scaledWidth - width - 2,
                    height - 2
                )
            }
            SnapPoint.TOP_LEFT -> {
                return Point(
                    width - 2,
                    height - 2
                )
            }
            SnapPoint.BOTTOM_RIGHT -> {
                return Point(
                    mc.window.scaledWidth - width - 2,
                    mc.window.scaledHeight - height - 2
                )
            }
            SnapPoint.BOTTOM_LEFT -> {
                return Point(
                    width - 2,
                    mc.window.scaledHeight - height - 2
                )
            }
            else -> {
                return Point(-1,-1)
            }
        }
    }
}