package dev.toastmc.toastclient.api.util.render

import com.mojang.blaze3d.systems.RenderSystem
import dev.toastmc.toastclient.api.util.ToastColor
import dev.toastmc.toastclient.api.util.mc
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.DiffuseLighting
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.Vector3f
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Matrix4f
import org.lwjgl.opengl.GL11

object RenderUtil : RenderExtensions {

    fun drawBox(pos: BlockPos, color: ToastColor) {
        drawBox(Box(pos), color)
    }

    fun drawBox(box: Box, color: ToastColor) {
        enable()

        val matrix = mc.gameRenderer.camera.originMatrix()
        val tessellator = RenderSystem.renderThreadTesselator()

        tessellator.buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR)
        box(
            matrix,
            tessellator.buffer,
            box,
            true,
            color.red.toFloat(),
            color.green.toFloat(),
            color.blue.toFloat(),
            color.alpha.toFloat()
        )
        tessellator.draw()

        RenderSystem.lineWidth(2.5f)

        tessellator.buffer.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR)
        box(
            matrix,
            tessellator.buffer,
            box,
            false,
            color.red.toFloat(),
            color.green.toFloat(),
            color.blue.toFloat(),
            color.alpha.toFloat()
        )
        tessellator.draw()

        disable()
    }

    fun drawOutline(blockPos: BlockPos, color: ToastColor, width: Float) {
        drawOutline(Box(blockPos), color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat(), width)
    }

    fun drawOutline(box: Box, r: Float, g: Float, b: Float, a: Float, width: Float) {
        enable()

        val matrix = mc.gameRenderer.camera.originMatrix()
        val tessellator = RenderSystem.renderThreadTesselator()
        val buffer = tessellator.buffer

        // Outline
        RenderSystem.lineWidth(width)

        buffer.begin(3, VertexFormats.POSITION_COLOR)
        box(
            matrix,
            tessellator.buffer,
            box,
            false,
            r,
            g,
            b,
            a
        )
        tessellator.draw()

        disable()
    }


    /**
     * Draws text in the world.
     * @author BleachDrinker420
     **/
    fun draw3DText(text: Text, x: Double, y: Double, z: Double, scale: Double, shadow: Boolean) {
        draw3DText(text, x, y, z, 0.0, 0.0, scale, false, shadow)
    }

    /**
     * Draws text in the world.
     * @author BleachDrinker420
     **/
    fun draw3DText(
        text: Text,
        x: Double,
        y: Double,
        z: Double,
        offX: Double,
        offY: Double,
        scale: Double,
        background: Boolean,
        shadow: Boolean,
    ) {
        enable()

        val camera = mc.gameRenderer.camera
        val matrix = camera.matrixFrom(x, y, z)
        val vertex = mc.bufferBuilders.outlineVertexConsumers

        matrix.push()
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-camera.yaw))
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.pitch))
        matrix.translate(offX, offY, 0.0)
        matrix.scale(-0.025f * scale.toFloat(), -0.025f * scale.toFloat(), 1f)

        if (background) {
            mc.textRenderer.draw(
                text,
                -mc.textRenderer.getWidth(text) / 2f,
                0f,
                553648127,
                false,
                matrix.peek().model,
                vertex,
                true,
                (MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f) * 255.0f).toInt() shl 24,
                0xf000f0
            )
        }

        if (shadow) {
            matrix.translate(1.0, 1.0, 0.0)
            mc.textRenderer.draw(
                text.copy(),
                -mc.textRenderer.getWidth(text) / 2f,
                0f,
                0x202020,
                false,
                matrix.peek().model,
                vertex,
                true,
                0,
                0xf000f0
            )
        }

        mc.textRenderer.draw(
            text,
            -mc.textRenderer.getWidth(text) / 2f,
            0f,
            -1,
            false,
            matrix.peek().model,
            vertex,
            true,
            0,
            0xf000f0
        )

        vertex.draw()
        matrix.pop()
        disable()
    }

    /**
     * Draws a 2D ItemStack in the world.
     * @author BleachDrinker420
     **/
    fun drawGuiItem(x: Double, y: Double, z: Double, offX: Double, offY: Double, scale: Double, item: ItemStack) {
        if (item.isEmpty) {
            return
        }
        enable()

        val camera = mc.gameRenderer.camera
        val matrix = camera.matrixFrom(x, y, z)
        val vertex = mc.bufferBuilders.entityVertexConsumers

        matrix.push()
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-camera.yaw))
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.pitch))

        matrix.translate(offX, offY, 0.0)
        matrix.scale(scale.toFloat(), scale.toFloat(), 0.001f)
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180f))
        vertex.draw()

        val light1 = FloatArray(4)
        val light2 = FloatArray(4)
        GL11.glGetLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, light1)
        GL11.glGetLightfv(GL11.GL_LIGHT1, GL11.GL_POSITION, light2)
        val currentLight = arrayOf(Vector3f(light1[0], light1[1], light1[2]), Vector3f(light2[0], light2[1], light2[2]))

        DiffuseLighting.disableGuiDepthLighting()
        GL11.glDepthFunc(GL11.GL_ALWAYS)
        mc.itemRenderer.renderItem(item, ModelTransformation.Mode.GUI, 0xF000F0, OverlayTexture.DEFAULT_UV, matrix, vertex)
        vertex.draw()

        GL11.glDepthFunc(GL11.GL_LEQUAL)
        RenderSystem.setupLevelDiffuseLighting(currentLight[0], currentLight[1], Matrix4f.translate(0f, 0f, 0f))
        matrix.pop()

        disable()
    }

}