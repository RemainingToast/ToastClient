package toast.client.gui.hud

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.InGameHud
import org.lwjgl.opengl.GL11
import toast.client.ToastClient
import toast.client.utils.TPSCalculator
import toast.client.utils.TwoDRenderUtils.drawRect
import java.awt.Color
import java.util.*
import kotlin.math.ceil


/**
 * Object containing the methods required to generate and render the HUD
 */
object HUD {
    private val mc = MinecraftClient.getInstance()
    private val hud = ToastClient.MODULE_MANAGER.getModule("HUD")
    private var rgb = false

    /**
     * Draws rainbow text at an X Y coordinate on the screen
     */
    private fun drawRainbowText(startX: Float, y: Float, text: String) {
        val size = (hud ?: return).getDouble("Watermark Size")
        GL11.glScaled(size, size, size)
        val positionArray: MutableMap<Float, Char> = TreeMap()
        var x: Float = startX
        for (char in text.toCharArray().toList()) {
            positionArray[x] = char
            x += mc.textRenderer.getCharWidth(char)
        }
        for ((charX: Float, char: Char) in positionArray) {
            mc.textRenderer.drawWithShadow(char.toString(), charX, y, rainbow(positionArray.keys.indexOf(charX) * 25))
        }
        GL11.glScaled(1.0 / size, 1.0 / size, 1.0 / size)
    }

    /**
     * Draws the current contents of the player's inventory on the screen
     */
    private fun drawInventory(startX: Int, startY: Int, drawBG: Boolean) {
        var x: Int = startX
        var y: Int = startY
        if (drawBG) drawRect(x, y, 153, 51, Color(125, 125, 125, 175).rgb)
        for ((i, itemStack) in (mc.player ?: return).inventory.main.withIndex()) {
            if (i > 8) {
                if (!itemStack.isEmpty) {
                    mc.itemRenderer.renderGuiItem(itemStack, x + 1, y + 1)
                    mc.itemRenderer.renderGuiItemOverlay(mc.textRenderer, itemStack, x + 1, y + 1)
                }
                if (x == 17 * 8 + startX) {
                    x = startX
                    y += 17
                } else x += 17
            }
        }
    }

    /**
     * Draws the current contents of the player's armor slots on the screen
     */
    private fun drawArmorSlots(startX: Int, y: Int) {
        var x = startX
        if (mc.player != null) {
            for (itemStack in (mc.player ?: return).inventory.armor) {
                if (!itemStack.isEmpty) {
                    var toAdd = 1
                    if ((mc.player ?: return).isSpectator || (mc.player ?: return).isCreative) toAdd = 21
                    mc.itemRenderer.renderGuiItem(itemStack, x + 1, y + toAdd)
                    mc.itemRenderer.renderGuiItemOverlay(mc.textRenderer, itemStack, x + 1, y + 1)
                }
                x -= 17
            }
        }
    }

    /**
     * Draws the list of enabled modules on the screen
     */
    private fun drawSortedSet() {
        if (hud != null) {
            val enabledModules: SortedSet<String> = TreeSet(Comparator.comparing { text: String? -> mc.textRenderer.getStringWidth(text) }.reversed()) //TODO: fix so this also works if there are 2 modules with the same name length rn if there are it will just include 1 in the list
            for (module in ToastClient.MODULE_MANAGER.modules) {
                if (module.enabled && module.getBool("Visible")) {
                    enabledModules.add(module.name)
                }
            }
            var count = 0
            for (moduleName in enabledModules) {
                count++
                val windowWidth = mc.window.scaledWidth
                val moduleNameWidth = mc.textRenderer.getStringWidth(moduleName)
                val fontHeight = mc.textRenderer.fontHeight
                var textcolor: Int
                var borderColor: Int
                if (rgb) {
                    textcolor = rainbowHTB(count * 35)
                    borderColor = rainbowHTB(50)
                } else {
                    textcolor = Color(255, 107, 66).rgb
                    borderColor = Color(255, 50, 66).rgb
                }
                var extralineoffset = 0
                if (hud.getBool("Right line")) {
                    extralineoffset = 1
                }

                //background
                InGameHud.fill(
                        windowWidth - moduleNameWidth - 3 - extralineoffset,
                        count * 12 - 12,
                        windowWidth - extralineoffset,
                        5 + fontHeight + count * 12 - 14,
                        Color(0, 0, 0, 90).rgb)

                //modulename
                mc.textRenderer.draw(moduleName,
                        windowWidth - moduleNameWidth - 1 - extralineoffset.toFloat(),
                        count * 12 - 10.toFloat(),
                        textcolor)
                if (hud.getBool("Left line")) {
                    // | <-- line
                    InGameHud.fill(
                            windowWidth - moduleNameWidth - 3 - extralineoffset,
                            count * 12 - 12,
                            windowWidth - moduleNameWidth - 4 - extralineoffset,
                            5 + fontHeight + count * 12 - 14,
                            borderColor)
                }
                if (hud.getBool("Middle line")) {
                    // __ \/ line
                    InGameHud.fill(
                            windowWidth - moduleNameWidth - 4,
                            5 + fontHeight + count * 12 - 14,
                            windowWidth - extralineoffset,
                            5 + fontHeight + count * 12 - 15,
                            borderColor)
                }
                if (extralineoffset >= 1) {
                    // --> | line
                    InGameHud.fill(
                            windowWidth,
                            0,
                            windowWidth - 1,
                            5 + fontHeight + count * 12 - 14,
                            borderColor)
                }
            }
        }
    }

    /**
     * Calls the methods to render the HUD
     */
    @JvmStatic
    fun drawHUD() {
        if (mc.options.debugEnabled) return
        if (hud != null) {
            if (hud.getBool("Rainbow")) {
                rgb = true
            }
            if (hud.getBool("Watermark")) {
                drawRainbowText(4f, 4f, ToastClient.cleanPrefix)
            }
            if (hud.getBool("Inventory")) {
                drawInventory(mc.window.scaledWidth / 2 + 100, mc.window.scaledHeight - 51, hud.getBool("Inventory BG"))
            }
            if (hud.getBool("Armor")) {
                drawArmorSlots(mc.window.scaledWidth / 2 + 74, mc.window.scaledHeight - 60)
            }
            if (hud.getBool("SortedSet")) {
                drawSortedSet()
            }
            if (hud.getBool("TPS")) {
                drawRainbowText(4f, 12f, "${TPSCalculator.tps}")
            }
        }
    }

    /**
     * Returns an rgb color value based on current time and a delay
     */
    private fun rainbow(delay: Int): Int {
        var rainbowState = ceil((System.currentTimeMillis() + delay shr 2.toDouble().toInt()).toDouble())
        rainbowState %= 360.0
        return Color.getHSBColor((rainbowState / 360.0).toFloat(), 0.2f, 3f).rgb
    }

    /**
     * Modification of above function
     */
    private fun rainbowHTB(delay: Int): Int {
        var rainbowState = ceil((System.currentTimeMillis() + delay shr 2.toDouble().toInt()).toDouble())
        rainbowState %= 360.0
        return Color.getHSBColor((rainbowState / 360.0).toFloat(), 0.3.toFloat(),
                0.97.toFloat()).rgb
    }
}