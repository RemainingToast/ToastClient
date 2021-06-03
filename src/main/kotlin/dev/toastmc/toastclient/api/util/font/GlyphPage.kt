package dev.toastmc.toastclient.api.util.font

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.texture.AbstractTexture
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11C
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.font.FontRenderContext
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class GlyphPage(
    private val font: Font,
    val isAntiAliasingEnabled: Boolean,
    val isFractionalMetricsEnabled: Boolean
) {
    private var imgSize = 0
    private val glyphCharacterMap = HashMap<Char, Glyph>()
    private var bufferedImage: BufferedImage? = null
    private var loadedTexture: AbstractTexture? = null

    var maxFontHeight = -1
        private set

    fun generateGlyphPage(chars: CharArray) {
        var maxWidth = -1.0
        var maxHeight = -1.0
        val affineTransform = AffineTransform()
        val fontRenderContext = FontRenderContext(
            affineTransform,
            isAntiAliasingEnabled,
            isFractionalMetricsEnabled
        )
        for (ch in chars) {
            val bounds = font.getStringBounds(Character.toString(ch), fontRenderContext)
            if (maxWidth < bounds.width) maxWidth = bounds.width
            if (maxHeight < bounds.height) maxHeight = bounds.height
        }

        // Leave some additional space
        maxWidth += 2.0
        maxHeight += 2.0
        imgSize = Math.ceil(
            Math.max(
                Math.ceil(Math.sqrt(maxWidth * maxWidth * chars.size) / maxWidth),
                Math.ceil(Math.sqrt(maxHeight * maxHeight * chars.size) / maxHeight)
            )
                    * Math.max(maxWidth, maxHeight)
        ).toInt() + 1
        bufferedImage = BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB)
        val g = bufferedImage!!.graphics as Graphics2D
        g.font = font
        // Set Color to Transparent
        g.color = Color(255, 255, 255, 0)
        // Set the image background to transparent
        g.fillRect(0, 0, imgSize, imgSize)
        g.color = Color.white
        g.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS,
            if (isFractionalMetricsEnabled) RenderingHints.VALUE_FRACTIONALMETRICS_ON else RenderingHints.VALUE_FRACTIONALMETRICS_OFF
        )
        g.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            if (isAntiAliasingEnabled) RenderingHints.VALUE_ANTIALIAS_OFF else RenderingHints.VALUE_ANTIALIAS_ON
        )
        g.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            if (isAntiAliasingEnabled) RenderingHints.VALUE_TEXT_ANTIALIAS_ON else RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
        )
        val fontMetrics = g.fontMetrics
        var currentCharHeight = 0
        var posX = 0
        var posY = 1
        for (ch in chars) {
            val glyph = Glyph()
            val bounds = fontMetrics.getStringBounds(Character.toString(ch), g)
            glyph.width = bounds.bounds.width + 8 // Leave some additional space
            glyph.height = bounds.bounds.height
            check(posY + glyph.height < imgSize) { "Not all characters will fit" }
            if (posX + glyph.width >= imgSize) {
                posX = 0
                posY += currentCharHeight
                currentCharHeight = 0
            }
            glyph.x = posX
            glyph.y = posY
            if (glyph.height > maxFontHeight) maxFontHeight = glyph.height
            if (glyph.height > currentCharHeight) currentCharHeight = glyph.height
            g.drawString(Character.toString(ch), posX + 2, posY + fontMetrics.ascent)
            posX += glyph.width
            glyphCharacterMap[ch] = glyph
        }
    }

    fun setupTexture() {
        var texture1: AbstractTexture?
        try {
            val baos = ByteArrayOutputStream()
            ImageIO.write(bufferedImage, "png", baos)
            val bytes = baos.toByteArray()
            val data = BufferUtils.createByteBuffer(bytes.size).put(bytes)
            data.flip()
            texture1 = NativeImageBackedTexture(NativeImage.read(data))
        } catch (e: Exception) {
            texture1 = null
            e.printStackTrace()
        }
        loadedTexture = texture1
    }

    fun bindTexture() {
        GlStateManager.bindTexture(loadedTexture!!.glId)
    }

    fun unbindTexture() {
        GlStateManager.bindTexture(0)
    }

    fun drawChar(ch: Char, x: Float, y: Float): Float {
        val glyph = glyphCharacterMap[ch] ?: throw IllegalArgumentException("'$ch' wasn't found")
        val pageX = glyph.x / imgSize.toFloat()
        val pageY = glyph.y / imgSize.toFloat()
        val pageWidth = glyph.width / imgSize.toFloat()
        val pageHeight = glyph.height / imgSize.toFloat()
        val width = glyph.width.toFloat()
        val height = glyph.height.toFloat()
        GL11.glBegin(GL11C.GL_TRIANGLES)
        GL11.glTexCoord2f(pageX + pageWidth, pageY)
        GL11.glVertex2f(x + width, y)
        GL11.glTexCoord2f(pageX, pageY)
        GL11.glVertex2f(x, y)
        GL11.glTexCoord2f(pageX, pageY + pageHeight)
        GL11.glVertex2f(x, y + height)
        GL11.glTexCoord2f(pageX, pageY + pageHeight)
        GL11.glVertex2f(x, y + height)
        GL11.glTexCoord2f(pageX + pageWidth, pageY + pageHeight)
        GL11.glVertex2f(x + width, y + height)
        GL11.glTexCoord2f(pageX + pageWidth, pageY)
        GL11.glVertex2f(x + width, y)
        GL11.glEnd()
        return width - 8
    }

    fun getWidth(ch: Char): Float {
        return glyphCharacterMap[ch]!!.width.toFloat()
    }

    internal class Glyph {
        var x = 0
        var y = 0
        var width = 0
        var height = 0

        constructor(x: Int, y: Int, width: Int, height: Int) {
            this.x = x
            this.y = y
            this.width = width
            this.height = height
        }

        constructor()
    }
}
