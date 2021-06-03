package dev.toastmc.toastclient.api.util.font

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormats
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11C
import java.awt.Font
import java.util.*

class FontRenderer private constructor(
    private val regularGlyphPage: GlyphPage,
    private val boldGlyphPage: GlyphPage,
    private val italicGlyphPage: GlyphPage,
    private val boldItalicGlyphPage: GlyphPage,
) {
    private var posX = 0f
    private var posY = 0f
    private val colorCode = IntArray(32)
    private var red = 0f
    private var blue = 0f
    private var green = 0f
    private var alpha = 0f
    private var boldStyle = false
    private var italicStyle = false
    private var underlineStyle = false
    private var strikethroughStyle = false

    fun drawString(text: String?, x: Float, y: Float, color: Int, dropShadow: Boolean): Int {
        GlStateManager.enableAlphaTest()
        resetStyles()
        var i: Int
        if (dropShadow) {
            i = renderString(text, x + 1.0f, y + 1.0f, color, true)
            i = Math.max(i, renderString(text, x, y, color, false))
        } else {
            i = renderString(text, x, y, color, false)
        }
        return i
    }

    private fun renderString(
        text: String?,
        x: Float,
        y: Float,
        color: Int,
        dropShadow: Boolean
    ): Int {
        var color = color
        return if (text == null) {
            0
        } else {
            if (color and -67108864 == 0) {
                color = color or -16777216
            }
            if (dropShadow) {
                color = color and 16579836 shr 2 or color and -16777216
            }
            red = (color shr 16 and 255).toFloat() / 255.0f
            blue = (color shr 8 and 255).toFloat() / 255.0f
            green = (color and 255).toFloat() / 255.0f
            alpha = (color shr 24 and 255).toFloat() / 255.0f
            GlStateManager.color4f(red, blue, green, alpha)
            posX = x * 2.0f
            posY = y * 2.0f
            renderStringAtPos(text, dropShadow)
            (posX / 4.0f).toInt()
        }
    }

    private fun renderStringAtPos(text: String, shadow: Boolean) {
        var glyphPage = currentGlyphPage
        GL11.glPushMatrix()
        GL11.glScaled(0.5, 0.5, 0.5)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11C.GL_SRC_ALPHA, GL11C.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.enableTexture()
        glyphPage.bindTexture()
        GL11.glTexParameteri(GL11C.GL_TEXTURE_2D, GL11C.GL_TEXTURE_MAG_FILTER, GL11C.GL_LINEAR)
        var i = 0
        while (i < text.length) {
            val c0 = text[i]
            if (c0.toInt() == 167 && i + 1 < text.length) {
                var i1 = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH)[i + 1])
                if (i1 < 16) {
                    boldStyle = false
                    strikethroughStyle = false
                    underlineStyle = false
                    italicStyle = false
                    if (i1 < 0) {
                        i1 = 15
                    }
                    if (shadow) {
                        i1 += 16
                    }
                    val j1 = colorCode[i1]
                    GlStateManager.color4f(
                        (j1 shr 16).toFloat() / 255.0f,
                        (j1 shr 8 and 255).toFloat() / 255.0f,
                        (j1 and 255).toFloat() / 255.0f,
                        alpha
                    )
                } else if (i1 == 17) {
                    boldStyle = true
                } else if (i1 == 18) {
                    strikethroughStyle = true
                } else if (i1 == 19) {
                    underlineStyle = true
                } else if (i1 == 20) {
                    italicStyle = true
                } else {
                    boldStyle = false
                    strikethroughStyle = false
                    underlineStyle = false
                    italicStyle = false
                    GlStateManager.color4f(red, blue, green, alpha)
                }
                ++i
            } else {
                glyphPage = currentGlyphPage
                glyphPage.bindTexture()
                val f = glyphPage.drawChar(c0, posX, posY)
                doDraw(f, glyphPage)
            }
            ++i
        }
        glyphPage.unbindTexture()
        GL11.glPopMatrix()
    }

    private fun doDraw(f: Float, glyphPage: GlyphPage) {
        if (strikethroughStyle) {
            val tessellator1 = Tessellator.getInstance()
            val worldrenderer1 = tessellator1.buffer
            worldrenderer1.begin(7, VertexFormats.POSITION)
            worldrenderer1.vertex(
                posX.toDouble(),
                (posY + (glyphPage.maxFontHeight.toFloat() / 2)).toDouble(),
                0.0
            )
                .next()
            worldrenderer1.vertex(
                (posX + f).toDouble(),
                (posY + (glyphPage.maxFontHeight.toFloat() / 2)).toDouble(),
                0.0
            ).next()
            worldrenderer1.vertex(
                (posX + f).toDouble(),
                (posY + (glyphPage.maxFontHeight.toFloat() / 2) - 1.0f).toDouble(),
                0.0
            ).next()
            worldrenderer1.vertex(
                posX.toDouble(),
                (posY + (glyphPage.maxFontHeight.toFloat() / 2) - 1.0f).toDouble(),
                0.0
            ).next()
            tessellator1.draw()
        }
        if (underlineStyle) {
            val tessellator1 = Tessellator.getInstance()
            val worldrenderer1 = tessellator1.buffer
            GlStateManager.disableTexture()
            worldrenderer1.begin(7, VertexFormats.POSITION)
            val l = if (underlineStyle) -1 else 0
            worldrenderer1.vertex(
                (posX + l.toFloat()).toDouble(),
                (posY + glyphPage.maxFontHeight.toFloat()).toDouble(),
                0.0
            ).next()
            worldrenderer1.vertex(
                (posX + f).toDouble(),
                (posY + glyphPage.maxFontHeight.toFloat()).toDouble(),
                0.0
            )
                .next()
            worldrenderer1.vertex(
                (posX + f).toDouble(),
                (posY + glyphPage.maxFontHeight.toFloat() - 1.0f).toDouble(),
                0.0
            ).next()
            worldrenderer1.vertex(
                (posX + l.toFloat()).toDouble(),
                (posY + glyphPage.maxFontHeight.toFloat() - 1.0f).toDouble(),
                0.0
            ).next()
            tessellator1.draw()
            GlStateManager.enableTexture()
        }
        posX += f
    }

    private val currentGlyphPage: GlyphPage
        private get() = if (boldStyle && italicStyle) boldItalicGlyphPage else if (boldStyle) boldGlyphPage else if (italicStyle) italicGlyphPage else regularGlyphPage

    private fun resetStyles() {
        boldStyle = false
        italicStyle = false
        underlineStyle = false
        strikethroughStyle = false
    }

    val height: Int
        get() = regularGlyphPage.maxFontHeight / 2

    fun getWidth(text: String?): Int {
        if (text == null) {
            return 0
        }
        var width = 0
        var currentPage: GlyphPage
        val size = text.length
        var on = false
        var i = 0
        while (i < size) {
            var character = text[i]
            if (character == 'ง' || character == 'ย') on =
                true else if (on && character >= '0' && character <= 'r') {
                val colorIndex = "0123456789abcdefklmnor".indexOf(character)
                if (colorIndex < 16) {
                    boldStyle = false
                    italicStyle = false
                } else if (colorIndex == 17) {
                    boldStyle = true
                } else if (colorIndex == 20) {
                    italicStyle = true
                } else if (colorIndex == 21) {
                    boldStyle = false
                    italicStyle = false
                }
                i++
                on = false
            } else {
                if (on) i--
                character = text[i]
                currentPage = currentGlyphPage
                width += (currentPage.getWidth(character) - 8).toInt()
            }
            i++
        }
        return width / 2
    }

    fun trimStringToWidth(text: String, width: Int): String {
        return this.trimStringToWidth(text, width, false)
    }

    fun trimStringToWidth(text: String, maxWidth: Int, reverse: Boolean): String {
        val stringbuilder = StringBuilder()
        var on = false
        val j = if (reverse) text.length - 1 else 0
        val k = if (reverse) -1 else 1
        var width = 0
        var currentPage: GlyphPage
        var i = j
        while (i >= 0 && i < text.length && i < maxWidth) {
            var character = text[i]
            if (character == 'ง' || character == 'ย') on =
                true else if (on && character >= '0' && character <= 'r') {
                val colorIndex = "0123456789abcdefklmnor".indexOf(character)
                if (colorIndex < 16) {
                    boldStyle = false
                    italicStyle = false
                } else if (colorIndex == 17) {
                    boldStyle = true
                } else if (colorIndex == 20) {
                    italicStyle = true
                } else if (colorIndex == 21) {
                    boldStyle = false
                    italicStyle = false
                }
                i++
                on = false
            } else {
                if (on) i--
                character = text[i]
                currentPage = currentGlyphPage
                width += (currentPage.getWidth(character) - 8).toInt() / 2
            }
            if (i > width) {
                break
            }
            if (reverse) {
                stringbuilder.insert(0, character)
            } else {
                stringbuilder.append(character)
            }
            i += k
        }
        return stringbuilder.toString()
    }

    companion object {
        @JvmOverloads
        fun create(
            fontName: String?,
            size: Int,
            bold: Boolean = false,
            italic: Boolean = false,
            boldItalic: Boolean = false,
        ): FontRenderer {
            val chars = CharArray(256)
            for (i in chars.indices) {
                chars[i] = i.toChar()
            }
            val regularPage = GlyphPage(Font(fontName, Font.PLAIN, size), true, true)
            regularPage.generateGlyphPage(chars)
            regularPage.setupTexture()
            var boldPage = regularPage
            var italicPage = regularPage
            var boldItalicPage = regularPage
            if (boldItalic) {
                boldItalicPage =
                    GlyphPage(Font(fontName, Font.BOLD or Font.ITALIC, size), true, true)
                boldItalicPage.generateGlyphPage(chars)
                boldItalicPage.setupTexture()
            } else {
                if (bold) {
                    boldPage = GlyphPage(Font(fontName, Font.BOLD, size), true, true)
                    boldPage.generateGlyphPage(chars)
                    boldPage.setupTexture()
                }
                if (italic) {
                    italicPage = GlyphPage(Font(fontName, Font.ITALIC, size), true, true)
                    italicPage.generateGlyphPage(chars)
                    italicPage.setupTexture()
                }
            }
            return FontRenderer(regularPage, boldPage, italicPage, boldItalicPage)
        }
    }

    init {
        for (i in 0..31) {
            val j = (i shr 3 and 1) * 85
            var k = (i shr 2 and 1) * 170 + j
            var l = (i shr 1 and 1) * 170 + j
            var i1 = (i and 1) * 170 + j
            if (i == 6) {
                k += 85
            }
            if (i >= 16) {
                k /= 4
                l /= 4
                i1 /= 4
            }
            colorCode[i] = k and 255 shl 16 or (l and 255 shl 8) or (i1 and 255)
        }
    }
}