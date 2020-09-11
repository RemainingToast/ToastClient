package dev.toastmc.client.gui.click

import dev.toastmc.client.ToastClient.Companion.MODULE_MANAGER
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.render.ClickGUI
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import org.lwjgl.glfw.GLFW
import java.util.*
import kotlin.collections.HashMap

class ClickGuiScreen : Screen(LiteralText("ClickGuiScreen")) {
    companion object {
        private val clickGui = MODULE_MANAGER.getModuleByClass(ClickGUI::class)
        var settings: ClickGuiSettings = ClickGuiSettings()
        var categoryRenderers = HashMap<Category, CategoryRenderer>()
        var width = 50
        var height = 10
        var keybindingPressedCategory: CategoryRenderer? = null
    }
    private var mouseIsClickedL = false
    private var mouseIsClickedR = false
    private var clickedOnce = false

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        height = MinecraftClient.getInstance().textRenderer.getStringBoundedHeight("> A", 100) + 3
        categoryRenderers.clear()
        for (category in Category.values()) {
            categoryRenderers[category] = CategoryRenderer(matrixStack, mouseX, mouseY, category, mouseIsClickedL, mouseIsClickedR)
        }
        if (clickedOnce) {
            mouseIsClickedL = false
            mouseIsClickedR = false
        }
//        if (descriptions) {
//            for ((_, categoryRenderer) in categoryRenderers.entries) {
//                if (categoryRenderer.keybindPressed) {
//                    ClickGuiScreen.keybindPressedCategory = categoryRenderer
//                }
//                if (categoryRenderer.description != null) {
//                    drawTextBox(matrixStack, categoryRenderer.description.descPosX, categoryRenderer.description.descPosY,
//                        textRenderer.getWidth("${settings.colors + categoryRenderer.description.desc}") + 4, h,
//                        settings.colors.descriptionBoxColor,
//                        settings.colors.descriptionTextColor,
//                        settings.colors.categoryPrefixColor,
//                        settings.colors.descriptionBgColor,
//                        settings.colors.descriptionPrefix,
//                        categoryRenderer.description.desc
//                    )
//                    break
//                }
//            }
//        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!clickedOnce) {
            if (button == 0) {
                mouseIsClickedL = true
                mouseIsClickedR = false
                clickedOnce = true
            } else if (button == 1) {
                mouseIsClickedL = false
                mouseIsClickedR = true
                clickedOnce = true
            }
        } else {
            mouseIsClickedL = false
            mouseIsClickedR = false
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 || button == 1) {
            mouseIsClickedL = false
            mouseIsClickedR = false
            clickedOnce = false
        }
        return false
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (button == 0) {
            for ((_, categoryRenderer) in categoryRenderers.entries) {
                if (categoryRenderer.updatePosition(deltaX, deltaY)) {
                    return false
                }
            }
        }
        return false
    }


    private fun updateMousePos(mouseX: Double, mouseY: Double) {
        for ((_, categoryRenderer) in categoryRenderers.entries) {
            categoryRenderer.updateMousePos(mouseX, mouseY)
        }
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        updateMousePos(mouseX, mouseY)
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun onClose() {
        settings.savePositions()
        settings.saveColors()
        Objects.requireNonNull(MODULE_MANAGER.getModuleByClass(ClickGUI::class))!!.disable()
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode != GLFW.GLFW_KEY_UNKNOWN) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) clickGui!!.onDisable()
            if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) clickGui!!.onDisable()
            if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) clickGui!!.onDisable() else keybindingPressedCategory?.setKeyPressed(keyCode)
            if (keyCode == Objects.requireNonNull(MODULE_MANAGER.getModuleByClass(ClickGUI::class))!!.key) clickGui!!.onDisable()
        }
        return false
    }

    fun reloadConfig() {
        settings.loadColors()
        settings.loadPositions()
    }

    @JvmName("getSettings1")
    fun getSettings(): ClickGuiSettings? {
        return settings
    }

}