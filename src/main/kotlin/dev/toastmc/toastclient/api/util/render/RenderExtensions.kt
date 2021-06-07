package dev.toastmc.toastclient.api.util.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper


interface RenderExtensions {

    fun box(
        matrix: MatrixStack,
        vertex: VertexConsumer,
        box: Box,
        filled: Boolean,
        r: Float,
        g: Float,
        b: Float,
        a: Float,
    ) {
        val x1 = box.minX.toFloat()
        val y1 = box.minY.toFloat()
        val z1 = box.minZ.toFloat()
        val x2 = box.maxX.toFloat()
        val y2 = box.maxY.toFloat()
        val z2 = box.maxZ.toFloat()

        if (filled) {
            vertex.vertex(matrix.peek().model, x1, y1, z1).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x2, y1, z1).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x2, y1, z2).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x1, y1, z2).color(r, g, b, a).next()

            vertex.vertex(matrix.peek().model, x1, y1, z2).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x1, y2, z2).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x1, y2, z1).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x1, y1, z1).color(r, g, b, a).next()

            vertex.vertex(matrix.peek().model, x2, y1, z1).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x2, y2, z1).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x2, y2, z2).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x2, y1, z2).color(r, g, b, a).next()

            vertex.vertex(matrix.peek().model, x1, y1, z1).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x1, y2, z1).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x2, y2, z1).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x2, y1, z1).color(r, g, b, a).next()

            vertex.vertex(matrix.peek().model, x2, y1, z2).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x2, y2, z2).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x1, y2, z2).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x1, y1, z2).color(r, g, b, a).next()

            vertex.vertex(matrix.peek().model, x1, y2, z2).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x2, y2, z2).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x2, y2, z1).color(r, g, b, a).next()
            vertex.vertex(matrix.peek().model, x1, y2, z1).color(r, g, b, a).next()
        } else {
            line(matrix, vertex, x1, y1, z1, x2, y1, z1, r, g, b, a)
            line(matrix, vertex, x2, y1, z1, x2, y1, z2, r, g, b, a)
            line(matrix, vertex, x2, y1, z2, x1, y1, z2, r, g, b, a)
            line(matrix, vertex, x1, y1, z2, x1, y1, z1, r, g, b, a)

            line(matrix, vertex, x1, y1, z1, x1, y2, z1, r, g, b, a)
            line(matrix, vertex, x2, y1, z1, x2, y2, z1, r, g, b, 0f)
            line(matrix, vertex, x2, y1, z1, x2, y2, z1, r, g, b, a)
            line(matrix, vertex, x2, y1, z2, x2, y2, z2, r, g, b, 0f)
            line(matrix, vertex, x2, y1, z2, x2, y2, z2, r, g, b, a)
            line(matrix, vertex, x1, y1, z2, x1, y2, z2, r, g, b, 0f)
            line(matrix, vertex, x1, y1, z2, x1, y2, z2, r, g, b, a)

            line(matrix, vertex, x1, y2, z1, x2, y2, z1, r, g, b, a)
            line(matrix, vertex, x2, y2, z1, x2, y2, z2, r, g, b, a)
            line(matrix, vertex, x2, y2, z2, x1, y2, z2, r, g, b, a)
            line(matrix, vertex, x1, y2, z2, x1, y2, z1, r, g, b, a)
        }
    }

    fun line(
        matrix: MatrixStack,
        vertexConsumer: VertexConsumer,
        x1: Float,
        y1: Float,
        z1: Float,
        x2: Float,
        y2: Float,
        z2: Float,
        r: Float,
        g: Float,
        b: Float,
        a: Float,
    ) {
        val model = matrix.peek().model
        val normal = matrix.peek().normal
        val xNormal = x2 - x1
        val yNormal = y2 - y1
        val zNormal = z2 - z1
        val normalSqrt = MathHelper.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal)
        val normalVec = Vector3f(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt)
        vertexConsumer.vertex(model, x1, y1, z1).color(r, g, b, a).normal(normal, normalVec.x, normalVec.y, normalVec.z).next()
        vertexConsumer.vertex(model, x2, y2, z2).color(r, g, b, a).normal(normal, normalVec.x, normalVec.y, normalVec.z).next()
    }

    fun enable() {
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableTexture()
    }

    fun disable() {
        RenderSystem.disableBlend()
        RenderSystem.enableTexture()
    }

    fun Camera.matrixFrom(x: Double, y: Double, z: Double): MatrixStack {
        val matrix = MatrixStack()
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(pitch))
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(yaw + 180.0f))
        matrix.translate(x - pos.x, y - pos.y, z - pos.z)
        return matrix
    }

    fun Camera.originMatrix(): MatrixStack {
        val matrix = MatrixStack()
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(pitch))
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(yaw + 180.0f))
        matrix.translate(-pos.x, -pos.y, -pos.z)
        return matrix
    }

}