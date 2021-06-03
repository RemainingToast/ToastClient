package dev.toastmc.toastclient.api.util.render

import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.mc
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.opengl.GL11
import java.awt.Color

object RenderUtil {

    fun draw3DText(
        matrices: MatrixStack,
        posX: Float,
        posY: Float,
        posZ: Float,
        yaw: Float,
        pitch: Float,
        scale: Float,
        text: String,
        color: Color,
        ignoreZ: Boolean,
    ) {
        val offsetX: Double = mc.textRenderer.getWidth(text) / 2.0
        val offsetY: Double = mc.textRenderer.fontHeight / 2.0
        GL11.glPushMatrix()
        if (ignoreZ) {
            GL11.glDisable(GL11.GL_DEPTH_TEST)
        } else {
            GL11.glEnable(GL11.GL_DEPTH_TEST)
        }
        GL11.glTranslated(posX.toDouble(), posY.toDouble(), posZ.toDouble())
        GL11.glScaled(scale.toDouble(), scale.toDouble(), scale.toDouble())
        GL11.glTranslated(offsetX, offsetY, 0.0)
        GL11.glPushMatrix()
        GL11.glRotated(180.0, 0.0, 0.0, 1.0)
        GL11.glTranslated(offsetX, offsetY, 0.0)
        GL11.glRotated(yaw.toDouble(), 0.0, 1.0, 0.0)
        GL11.glRotated(-pitch.toDouble(), 1.0, 0.0, 0.0)
        GL11.glTranslated(-offsetX, -mc.textRenderer.fontHeight / 2.0, 0.0)
        mc.textRenderer.draw(matrices, lit(text), 0f, 0f, color.rgb)
        GL11.glPopMatrix()
        GL11.glPopMatrix()
        GL11.glEnable(GL11.GL_DEPTH_TEST)
    }

}