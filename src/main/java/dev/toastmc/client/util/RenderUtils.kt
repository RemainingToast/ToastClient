package dev.toastmc.client.util

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.render.*
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.*
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_QUADS
import org.lwjgl.opengl.GL14
import java.awt.Color
import kotlin.math.ceil
import kotlin.random.Random


/**
 * @credit wnuke
 */
object TwoDRenderUtils {
    private val textRenderer = MinecraftClient.getInstance().textRenderer

    /**
     * Draws text at the given coordinates
     *
     * @param text  Text to draw
     * @param x     X coordinate of the top left corner of the text
     * @param y     Y coordinate of the top left corner of the text
     * @param color Color of the box
     */
    @JvmStatic
    fun drawText(matrix: MatrixStack, text: String?, x: Int, y: Int, color: Int) {
        textRenderer.drawWithShadow(matrix, text, x.toFloat(), y.toFloat(), color)
        RenderSystem.pushMatrix()
        RenderSystem.popMatrix()
    }

    /**
     * Draws a rectangle at the given coordinates
     *
     * @param x      X coordinate of the top left corner of the rectangle
     * @param y      Y coordinate of the top left corner of the rectangle
     * @param width  Width of the box
     * @param height Height of the box
     * @param color  Color of the box
     */
    @JvmStatic
    fun drawRect(matrix: MatrixStack, x: Int, y: Int, width: Int, height: Int, color: Int) {
        InGameHud.fill(matrix, x, y, x + width, y + height, color)
        RenderSystem.pushMatrix()
        RenderSystem.popMatrix()
    }

    /**
     * Draws a box at the given coordinates
     *
     * @param x      X coordinate of the top left corner of the inside of the box
     * @param y      Y coordinate of the top left corner of the inside of the box
     * @param width  Width of the inside of the box
     * @param height Height of the inside of the box
     * @param lW     Width of the lines of the box
     * @param color  Color of the box
     */
    @JvmStatic
    fun drawHollowRect(matrix: MatrixStack, x: Int, y: Int, width: Int, height: Int, lW: Int, color: Int) {
        drawRect(matrix,x - lW, y - lW, width + lW * 2, lW, color) // top line
        drawRect(matrix,x - lW, y, lW, height, color) // left line
        drawRect(matrix,x - lW, y + height, width + lW * 2, lW, color) // bottom line
        drawRect(matrix,x + width, y, lW, height, color) // right line
    }

    /**
     * Draw a text box at the given coordinates
     *
     * @param x           X coordinate of the top left corner of the inside of the text box
     * @param y           Y coordinate of the top left corner of the inside of the text box
     * @param width       Width of the inside of the box
     * @param height      Height of the inside of the box
     * @param color       Color of the box outlines
     * @param textColor   Color of the text
     * @param prefixColor Color of the text prefix
     * @param bgColor     Color of the box's background
     * @param prefix      Text to prepend to the main text
     * @param text        Main text to put in the box
     */
    @JvmStatic
    fun drawTextBox(
        matrix: MatrixStack,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        color: Int,
        textColor: Int,
        prefixColor: Int,
        bgColor: Int,
        prefix: String?,
        text: String?
    ) {
        drawRect(matrix,x - 2, y - 2, width, height, bgColor)
        drawHollowRect(matrix, x - 2, y - 2, width, height, 1, color)
        drawText(matrix, prefix, x, y, prefixColor)
        drawText(matrix, text, x + textRenderer.getWidth(prefix), y, textColor)
    }

    /**
     * Check if the mouse if over a box on screen
     *
     * @param mouseX Current X coordinate of the mouse
     * @param mouseY Current Y coordinate of the mouse
     * @param x      X coordinate of the top left corner of the inside of the text box
     * @param y      Y coordinate of the top left corner of the inside of the text box
     * @param width  Width of the inside of the box
     * @param height Height of the inside of the box
     */
    @JvmStatic
    fun isMouseOverRect(
        mouseX: Double,
        mouseY: Double,
        x: Double,
        y: Double,
        width: Int,
        height: Int
    ): Boolean {
        var xOver = false
        var yOver = false
        if (mouseX >= x && mouseX <= width + x) {
            xOver = true
        }
        if (mouseY >= y && mouseY <= height + y) {
            yOver = true
        }
        return xOver && yOver
    }
}

/**
 * @credit cookiedragon234 26/Jun/2020
 * @from https://github.com/cookiedragon234/Raion-116/
 */
open class RenderBuilder {
    var translation: Vec3d = Vec3d.ZERO

    inline fun begin(glMode: Int, action: RenderBuilder.() -> Unit) {
        try {
            GL11.glBegin(glMode)
            action()
        } finally {
            GL11.glEnd()
        }
    }
}

fun drawFilledBox(box: Box, color: Color) {
    draw3d {
        begin(GL_QUADS){
            color(color.red, color.green, color.blue, color.alpha)
            box(box)
        }
    }
}

fun <T : RenderBuilder> T.vertex(x: Float, y: Float, z: Float): T = this.apply {
    GL11.glVertex3d(x + translation.x, y + translation.y, z + translation.z)
}

fun <T : RenderBuilder> T.vertex(x: Double, y: Double, z: Double): T = this.apply {
    GL11.glVertex3d(x + translation.x, y + translation.y, z + translation.z)
}

fun <T : RenderBuilder> T.vertex(vec3d: Vec3d): T = this.apply {
    GL11.glVertex3d(vec3d.x + translation.x, vec3d.y + translation.y, vec3d.z + translation.z)
}

fun <T : RenderBuilder> T.color(r: Float, g: Float, b: Float, a: Float): T = this.apply {
    GlStateManager.color4f(r, g, b, a)
}

fun <T : RenderBuilder> T.color(r: Float, g: Float, b: Float): T = this.apply {
    GlStateManager.color4f(r, g, b, 1f)
}

fun <T : RenderBuilder> T.color(r: Int, g: Int, b: Int, a: Int): T = this.apply {
    GlStateManager.color4f(r / 255f, g / 255f, b / 255f, a / 255f)
}

fun <T : RenderBuilder> T.color(r: Int, g: Int, b: Int): T = this.apply {
    GlStateManager.color4f(r / 255f, g / 255f, b / 255f, 1f)
}

fun <T : RenderBuilder> T.color(color4f: Color4f): T = this.apply {
    GlStateManager.color4f(color4f.r, color4f.g, color4f.b, color4f.a)
}

fun <T : RenderBuilder> T.color(color3f: Color3f): T = this.apply {
    GlStateManager.color4f(color3f.r, color3f.g, color3f.b, 1f)
}

fun <T : RenderBuilder> T.color(color4i: Color4i): T = this.apply {
    GlStateManager.color4f(color4i.r / 255f, color4i.g / 255f, color4i.b / 255f, color4i.a / 255f)
}

fun <T : RenderBuilder> T.color(color3i: Color3i): T = this.apply {
    GlStateManager.color4f(color3i.r / 255f, color3i.g / 255f, color3i.b / 255f, 1f)
}

data class Color4f(var r: Float, var g: Float, var b: Float, var a: Float)

data class Color4i(var r: Int, var g: Int, var b: Int, var a: Int)

data class Color3f(var r: Float, var g: Float, var b: Float)

data class Color3i(var r: Int, var g: Int, var b: Int)

fun getRandomRainbow(seed: Int): Color {
    return getRainbow(
        Random(seed).nextFloat(),
        Random(seed).nextFloat(),
        Random(seed).nextDouble(),
        Random(seed).nextInt()
    )
}

fun getRainbow(sat: Float, bri: Float, speed: Double, offset: Int): Color {
    var rainbowState = ceil((System.currentTimeMillis() + offset) / speed)
    rainbowState %= 360.0
    return Color.getHSBColor((rainbowState / 360.0).toFloat(), sat, bri)
}

fun <T : RenderBuilder> T.text(matrixStack: MatrixStack, text: String, blockPos: BlockPos): T = this.apply {
    //TODO: 3d Text at BlockPos
//    glSetup(x, y, z);
//    GL11.glScaled(-0.025, -0.025, 0.025);
//    val i = mc.textRenderer.getWidth(text) / 2
//    val tessellator = Tessellator.getInstance()
//    GL11.glDisable(GL11.GL_TEXTURE_2D)
//    color(0, 0, 255, 128)
//    vertex(-i - 1.0, -1.0, 0.0)
//    vertex(x + 0.5, y + 0.5, z + 0.5)
//    vertex(x - 0.5, y - 0.5, z - 0.5)
//    vertex(x + 0.5, y + 0.5, z + 0.5)
//    tessellator.draw()
//    GL11.glEnable(GL11.GL_TEXTURE_2D)
//    mc.textRenderer.draw(matrixStack, text, x.toFloat() - 0.5f, y.toFloat() - 0.5f, -1)
//    mc.textRenderer.draw(matrixStack, text, x.toFloat() - 0.5f, y.toFloat() - 0.5f, -1)
//    glCleanup()
}

fun <T : RenderBuilder> T.box(bp: BlockPos): T = this.apply {
    box(Box(bp))
}

fun <T : RenderBuilder> T.box(bb: Box): T = this.apply {
    vertex(bb.minX, bb.minY, bb.minZ)
    vertex(bb.maxX, bb.minY, bb.minZ)
    vertex(bb.maxX, bb.minY, bb.maxZ)
    vertex(bb.minX, bb.minY, bb.maxZ)
    vertex(bb.minX, bb.maxY, bb.minZ)
    vertex(bb.minX, bb.maxY, bb.maxZ)
    vertex(bb.maxX, bb.maxY, bb.maxZ)
    vertex(bb.maxX, bb.maxY, bb.minZ)
    vertex(bb.minX, bb.minY, bb.minZ)
    vertex(bb.minX, bb.maxY, bb.minZ)
    vertex(bb.maxX, bb.maxY, bb.minZ)
    vertex(bb.maxX, bb.minY, bb.minZ)
    vertex(bb.maxX, bb.minY, bb.minZ)
    vertex(bb.maxX, bb.maxY, bb.minZ)
    vertex(bb.maxX, bb.maxY, bb.maxZ)
    vertex(bb.maxX, bb.minY, bb.maxZ)
    vertex(bb.minX, bb.minY, bb.maxZ)
    vertex(bb.maxX, bb.minY, bb.maxZ)
    vertex(bb.maxX, bb.maxY, bb.maxZ)
    vertex(bb.minX, bb.maxY, bb.maxZ)
    vertex(bb.minX, bb.minY, bb.minZ)
    vertex(bb.minX, bb.minY, bb.maxZ)
    vertex(bb.minX, bb.maxY, bb.maxZ)
    vertex(bb.minX, bb.maxY, bb.minZ)
}

fun <T : RenderBuilder> T.line(start: Vec3d, end: Vec3d): T = this.apply {
    vertex(start)
    vertex(start)
    vertex(end)
}

inline fun draw3d(translate: Boolean = false, color: Color = Color(1f, 1f, 1f, 1f), action: RenderBuilder.() -> Unit) {
    val builder = RenderBuilder()

    try {
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GlStateManager.lineWidth(2f)
        GlStateManager.disableTexture()
        GlStateManager.enableCull()
        GlStateManager.disableDepthTest()
        GlStateManager.disableLighting()

        if (translate) {
            val camera = mc.gameRenderer.camera
            GlStateManager.rotatef(MathHelper.wrapDegrees(camera.pitch), 1f, 0f, 0f)
            GlStateManager.rotatef(MathHelper.wrapDegrees(camera.yaw + 180f), 0f, 1f, 0f)
            builder.translation = builder.translation.subtract(camera.pos)
        }

        builder.action()
    } finally {
        GlStateManager.color4f(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat())
        GlStateManager.enableDepthTest()
        GlStateManager.enableTexture()
        GlStateManager.enableBlend()
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GlStateManager.popMatrix()
    }
}

fun glCleanup() {
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glDepthFunc(GL11.GL_LEQUAL);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPopMatrix();
}

fun glSetup(x: Double, y: Double, z: Double) {
    GL11.glPushMatrix()
    offsetRender()
    GL11.glTranslated(x, y, z)
    GL11.glNormal3f(0.0f, 1.0f, 0.0f)
    GL11.glRotatef(-mc.player!!.yaw, 0.0f, 1.0f, 0.0f)
    GL11.glRotatef(mc.player!!.pitch, 1.0f, 0.0f, 0.0f)
    GL11.glDepthFunc(GL11.GL_ALWAYS)
    GL11.glEnable(GL11.GL_BLEND)
    GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
}

private fun RenderBuilder.translateVec(camera: Camera) {

}

class KOpenGLRenderer(val matrices: Matrix4f, val bufferBuilder: BufferBuilder) {
}
class KOpenGLVertex(val matrices: Matrix4f, val bufferBuilder: BufferBuilder)

inline fun <T> MatrixStack.draw(
    translate: Camera? = null,
    glMode: Int? = null,
    format: VertexFormat? = null,
    action: KOpenGLRenderer.() -> T
): T {
    RenderSystem.enableBlend()
    RenderSystem.disableTexture()
    RenderSystem.defaultBlendFunc()
    push()
    val model = peek().model
    val ts = Tessellator.getInstance()
    val bb = ts.buffer
    try {
        if (glMode != null && format != null) bb.begin(glMode, format)
        if (translate != null) this.translateToRender(translate)
        return action(KOpenGLRenderer(model, bb))
    } finally {
        try {
            bb.end()
            BufferRenderer.draw(bb)
            //ts.draw()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        pop()
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }
}

inline fun KOpenGLRenderer.vertex(action: KOpenGLVertex.() -> Unit): KOpenGLRenderer = this.apply {
    try {
        action(KOpenGLVertex(this.matrices, this.bufferBuilder))
    } finally {
        this.bufferBuilder.next()
    }
}

fun <T : MatrixStack> T.translateToRender(camera: Camera): T = this.apply {
    val pos = camera.pos
    this.translate(
        -pos.x,
        -pos.y,
        -pos.z
    )
}

fun KOpenGLRenderer.next() = this.apply {
    this.bufferBuilder.next()
}

fun KOpenGLRenderer.box(box2f: Box2f, color: Color, offset: Float = 0f) = this.apply {
    for (corner in box2f.getCornersOffset(offset)) {
        vertex {
            vertex(corner)
            color(color)
        }
    }
}

fun KOpenGLRenderer.box(x1: Float, y1: Float, x2: Float, y2: Float, color: Color) = this.apply {
    var x1 = x1
    var y1 = y1
    var x2 = x2
    var y2 = y2
    if (x1 < x2) {
        val j = x1
        x1 = x2
        x2 = j
    }

    if (y1 < y2) {
        val j = y1
        y1 = y2
        y2 = j
    }
    vertex {
        vertex(x1, y2, 0f)
        color(color)
    }
    vertex {
        vertex(x2, y2, 0f)
        color(color)
    }
    vertex {
        vertex(x2, y1, 0f)
        color(color)
    }
    vertex {
        vertex(x1, y1, 0f)
        color(color)
    }
}

fun KOpenGLVertex.vertex(x: Double, y: Double, z: Double): KOpenGLVertex = this.apply {
    this.bufferBuilder.vertex(x, y, z)
}

fun KOpenGLVertex.vertex(x: Float, y: Float, z: Float): KOpenGLVertex = this.apply {
    this.vertex(x.toDouble(), y.toDouble(), z.toDouble())
}

fun KOpenGLVertex.vertex(pos: Vec2f): KOpenGLVertex = this.apply {
    this.vertex(pos.x.toDouble(), pos.y.toDouble(), 0.0)
}

fun KOpenGLVertex.vertex(pos: Vec3d, xOffset: Double = 0.0, yOffset: Double = xOffset, zOffset: Double = xOffset): KOpenGLVertex = this.apply {
    this.vertex(pos.x + xOffset, pos.y + yOffset, pos.z + zOffset)
}

fun KOpenGLVertex.vertex(pos: Vec3i, xOffset: Double = 0.0, yOffset: Double = xOffset, zOffset: Double = xOffset): KOpenGLVertex = this.apply {
    this.vertex(pos.x.toDouble() + xOffset, pos.y.toDouble() + yOffset, pos.z.toDouble() + zOffset)
}

fun KOpenGLVertex.color(red: Int, green: Int, blue: Int, alpha: Int): KOpenGLVertex = this.apply {
    this.bufferBuilder.color(red, green, blue, alpha)
}

fun KOpenGLVertex.color(color: Color): KOpenGLVertex = this.apply {
    this.color(color.red, color.green, color.blue, color.alpha)
}

fun KOpenGLVertex.color(color: Int): KOpenGLVertex = this.apply {
    val alpha = (color shr 24 and 255)
    val red = (color shr 16 and 255)
    val green = (color shr 8 and 255)
    val blue = (color and 255)
    color(alpha, red, green, blue)
}

fun KOpenGLRenderer.bb(bb: Box, red: Int? = null, green: Int? = null, blue: Int? = null, alpha: Int? = null): KOpenGLRenderer = this.apply {
    vertex {
        vertex(bb.minX, bb.minY, bb.minZ)
        if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
    }
    vertex {
        vertex(bb.minX, bb.maxY, bb.minZ)
        if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
    }
    vertex {
        vertex(bb.maxX, bb.minY, bb.minZ)
        if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
    }
    vertex {
        vertex(bb.maxX, bb.maxY, bb.minZ)
        if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
    }
    vertex {
        vertex(bb.maxX, bb.minY, bb.maxZ)
        if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
    }
    vertex {
        vertex(bb.maxX, bb.maxY, bb.maxZ)
        if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
    }
    vertex {
        vertex(bb.minX, bb.minY, bb.maxZ)
        if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
    }
    vertex {
        vertex(bb.minX, bb.maxY, bb.maxZ)
        if (red != null && green != null && blue != null && alpha != null) { color(red, green, blue, alpha) }
    }
}

open class Box2f(
    posX: Float = 0f,
    posY: Float = 0f,
    sizeX: Float = 0f,
    sizeY: Float = 0f
) {
    var topLeft = Vec2fMutable(posX, posY)
    var bottomRight = Vec2fMutable(posX + sizeX, posY + sizeY)
    var posX: Float
        get() = topLeft.x
        set(value) { topLeft.x = value }
    var posY: Float
        get() = topLeft.y
        set(value) { topLeft.y = value }
    var sizeX: Float
        get() = bottomRight.x - topLeft.x
        set(value) { bottomRight.x = posX + value }
    var sizeY: Float
        get() = bottomRight.y - topLeft.y
        set(value) { bottomRight.y = posY + value }
    var rightX: Float
        get() = bottomRight.x
        set(value) { bottomRight.x = value }
    var bottomY: Float
        get() = bottomRight.y
        set(value) { bottomRight.y = value }

    fun contains(position: Vec2f): Boolean {
        return position.x in posX..rightX && position.y in posY..bottomY
    }

    val corners: Array<Vec2f>
        get() = arrayOf(Vec2f(posX, bottomY), bottomRight, Vec2f(rightX, posY), topLeft)

    fun getCornersOffset(offset: Float = 0f): Array<Vec2f> {
        if (offset == 0f) return corners
        return arrayOf(
            Vec2f(posX - offset, bottomY + offset), Vec2f(rightX + offset, bottomY + offset), Vec2f(
                rightX + offset,
                posY - offset
            ), Vec2f(posX - offset, posY - offset)
        )
    }
}

open class Vec2f(
    x: Float,
    y: Float
) {
    open var x: Float = x
        protected set
    open var y: Float = y
        protected set

    operator fun plus(other: Vec2f): Vec2f {
        return Vec2f(this.x + other.x, this.y + other.y)
    }
    operator fun minus(other: Vec2f): Vec2f {
        return Vec2f(this.x - other.x, this.y - other.y)
    }
}
/*data class Vec2fImpl(
	override val x: Float,
	override val y: Float
): Vec2f(x, y)*/
class Vec2fMutable(
    x: Float,
    y: Float
): Vec2f(x, y) {
    override var x: Float = x
        public set
    override var y: Float = y
        public set
}

fun offsetRender() {
    val camera: Camera = BlockEntityRenderDispatcher.INSTANCE.camera
    val camPos: Vec3d = camera.pos
    GL11.glRotated(MathHelper.wrapDegrees(camera.pitch).toDouble(), 1.0, 0.0, 0.0)
    GL11.glRotated(MathHelper.wrapDegrees(camera.yaw + 180.0), 0.0, 1.0, 0.0)
    GL11.glTranslated(-camPos.x, -camPos.y, -camPos.z)
}