package dev.toastmc.toastclient.impl.clickgui

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.managers.module.ModuleManager
import dev.toastmc.toastclient.api.util.TwoDRenderUtil
import net.minecraft.client.util.math.MatrixStack
import java.awt.Rectangle
import kotlin.math.roundToInt

class ClickGUIPanel(category: Module.Category, var x: Double, var y: Double) {

    var width = 90
    var height = 12

    var category: Module.Category = Module.Category.NONE
    var categoryExpanded = false
    var modsExpanded: HashMap<Module, Boolean> = java.util.HashMap<Module, Boolean>()

    private var level = 0

    private var mouseX = 0.0
    private var mouseY = 0.0

    private val hovering: Boolean
        get() {
            return hover(mouseX, mouseY, Rectangle(x.roundToInt(), y.roundToInt(), width, height))
        }

    init {
        this.category = category
    }

    fun render(
        matrices: MatrixStack,
        mouseX: Double,
        mouseY: Double,
        leftClicked: Boolean,
        rightClicked: Boolean
    ) {
        drawCategory(matrices, category, mouseX, mouseY, leftClicked, rightClicked)
    }

    fun updatePosition(dragX: Double, dragY: Double) {
        if (hovering) {
            this.x += dragX
            this.y += dragY
        }
    }

    fun updateMousePos(mouseX: Double, mouseY: Double){
        this.mouseX = mouseX
        this.mouseY = mouseY
    }

    private fun drawCategory(
        matrices: MatrixStack,
        category: Module.Category,
        mouseX: Double,
        mouseY: Double,
        leftClicked: Boolean,
        rightClicked: Boolean
    ) {
        level = 1

        TwoDRenderUtil.drawCenteredTextBox(
            matrices,
            category.name,
            Rectangle(
                x.roundToInt(),
                y.roundToInt(),
                width,
                height
            ),
            if (hovering)
                -0x66ff0100
            else
                -0x7fff0100, -0x1
        )
        if (hovering) {
            if (rightClicked) {
                categoryExpanded = !categoryExpanded
            }
        }
        if (categoryExpanded) {
            for (mod in ModuleManager.getModulesByCategory(category)!!) {
//                drawModule(matrices, mod, mouseX, mouseY, lastMouseX, lastMouseY, leftClicked, rightClicked)
            }
        }
        TwoDRenderUtil.drawHollowRect(
            matrices,
            (x - 2).roundToInt(),
            (y - 2).roundToInt(),
            width,
            level + height * level,
            1,
            -0x7fff0100
        )
    }

    private fun hover(mouseX: Double, mouseY: Double, rect: Rectangle): Boolean {
        return mouseX >= rect.x && mouseX <= rect.width + rect.x && mouseY >= rect.y && mouseY <= rect.height + rect.y
    }

}