package dev.toastmc.toastclient.api.util

import dev.toastmc.toastclient.api.util.font.FontRenderer
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.opengl.GL11

object TwoDRenderUtil {

    fun drawText(
        matrices: MatrixStack,
        renderer: FontRenderer,
        text: String,
        x: Int,
        y: Int,
        dropShadow: Boolean,
        color: ToastColor,
    ) {
        startSmooth()
        matrices.push()
        renderer.drawString(
            text,
            x.toFloat(),
            y.toFloat(),
            color.rgb,
            dropShadow
        )
        matrices.pop()
        endSmooth()
    }

    fun drawCenteredText(
        matrices: MatrixStack,
        renderer: FontRenderer,
        text: String,
        centerX: Int,
        y: Int,
        dropShadow: Boolean,
        color: ToastColor,
    ) {
        startSmooth()
        matrices.push()
        renderer.drawString(
            text,
            centerX - (renderer.getWidth(text) shr 1).toFloat(),
            y.toFloat(),
            color.rgb,
            dropShadow
        )
        matrices.pop()
        endSmooth()
    }

    fun startSmooth() {
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH)
        GL11.glEnable(GL11.GL_POINT_SMOOTH)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST)
    }

    fun endSmooth() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH)
        GL11.glEnable(GL11.GL_POINT_SMOOTH)
    }


}