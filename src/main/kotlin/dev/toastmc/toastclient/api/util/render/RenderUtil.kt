package dev.toastmc.toastclient.api.util.render

import com.mojang.blaze3d.systems.RenderSystem
import dev.toastmc.toastclient.api.util.mc
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.math.Matrix4f
import org.lwjgl.opengl.GL11

/**
 * Original @author BleachDrinker420
 */
object RenderUtil {

    /** Draws text in the world. */
    fun draw3DText(text: Text, x: Double, y: Double, z: Double, scale: Double, shadow: Boolean) {
        draw3DText(text, x, y, z, 0.0, 0.0, scale, false, shadow)
    }

    /** Draws text in the world. */
    fun draw3DText(
        text: Text,
        x: Double,
        y: Double,
        z: Double,
        offX: Double,
        offY: Double,
        scale: Double,
        background: Boolean,
        shadow: Boolean
    ) {
        val matrix = matrixFrom(x, y, z)
        val camera = mc.gameRenderer.camera

        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-camera.yaw))
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.pitch))

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        matrix.translate(offX, offY, 0.0)
        matrix.scale(-0.025f * scale.toFloat(), -0.025f * scale.toFloat(), 1f)

        if (background) {
            mc.textRenderer.draw(text,
                -mc.textRenderer.getWidth(text) / 2f,
                0f,
                553648127,
                false,
                matrix.peek().model,
                mc.bufferBuilders.entityVertexConsumers,
                true,
                (MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f) * 255.0f).toInt() shl 24,
                0xf000f0
            )
        }

        if (shadow) {
            matrix.push()
            matrix.translate(1.0, 1.0, 0.0)
            mc.textRenderer.draw(text.copy(), -mc.textRenderer.getWidth(text) / 2f, 0f, 0x202020, false, matrix.peek().model, mc.bufferBuilders.entityVertexConsumers, true, 0, 0xf000f0)
        }

        mc.textRenderer.draw(text, -mc.textRenderer.getWidth(text) / 2f, 0f, -1, false, matrix.peek().model, mc.bufferBuilders.entityVertexConsumers, true, 0, 0xf000f0)
        mc.bufferBuilders.entityVertexConsumers.draw()
        matrix.pop()

        RenderSystem.disableBlend()
    }

    /** Draws a 2D ItemStack in the world. */
    fun drawGuiItem(x: Double, y: Double, z: Double, offX: Double, offY: Double, scale: Double, item: ItemStack) {
        if (item.isEmpty) {
            return
        }

        val matrix = matrixFrom(x, y, z)
        val camera = mc.gameRenderer.camera
        val vertex = mc.bufferBuilders.entityVertexConsumers

        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-camera.yaw))
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.pitch))

        matrix.translate(offX, offY, 0.0)
        matrix.scale(scale.toFloat(), scale.toFloat(), 0.001f)
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180f))
        vertex.draw()

        val currentLight = currentLight
        DiffuseLighting.disableGuiDepthLighting()
        GL11.glDepthFunc(GL11.GL_ALWAYS)
        mc.itemRenderer.renderItem(item, ModelTransformation.Mode.GUI, 0xF000F0, OverlayTexture.DEFAULT_UV, matrix, vertex)
        vertex.draw()

        GL11.glDepthFunc(GL11.GL_LEQUAL)
        RenderSystem.setupLevelDiffuseLighting(currentLight[0], currentLight[1], Matrix4f.translate(0f, 0f, 0f))
    }

    private fun matrixFrom(x: Double, y: Double, z: Double): MatrixStack {
        val matrix = MatrixStack()
        val camera = mc.gameRenderer.camera
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.pitch))
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(camera.yaw + 180.0f))
        matrix.translate(x - camera.pos.x, y - camera.pos.y, z - camera.pos.z)
        return matrix
    }

    private val currentLight: Array<Vector3f>
        get() {
            val light1 = FloatArray(4)
            val light2 = FloatArray(4)
            GL11.glGetLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, light1)
            GL11.glGetLightfv(GL11.GL_LIGHT1, GL11.GL_POSITION, light2)
            return arrayOf(Vector3f(light1[0], light1[1], light1[2]), Vector3f(light2[0], light2[1], light2[2]))
        }
}