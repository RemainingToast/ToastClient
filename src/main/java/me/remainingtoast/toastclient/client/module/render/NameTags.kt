package me.remainingtoast.toastclient.client.module.render

import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Formatting


object NameTags : Module("NameTags", Category.RENDER){

    fun renderNameTag(entity: Entity, name: String, matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider?, int_1: Int) {
        var name = name
        val distance: Double = MinecraftClient.getInstance().entityRenderDispatcher.getSquaredDistanceToCamera(entity)
        if (entity is LivingEntity) {
            if(entity is PlayerEntity){
                name = entity.getDisplayName().asString().toString() + " " + (if (entity.health > entity.maxHealth / 3.0f) if (entity.health > entity.maxHealth / 3.0f * 2.0f) Formatting.GREEN else Formatting.YELLOW else Formatting.DARK_RED) + (entity.health * 2.0f).toInt() / 2.0f + "HP"

            }
        }
        if (distance <= 4096.0) {
            val notSneaking: Boolean = !entity.isSneaky
            val height: Float = entity.height + 0.5f
            matrixStack.push()
            matrixStack.translate(0.0, height.toDouble(), 0.0)
            matrixStack.multiply(MinecraftClient.getInstance().entityRenderDispatcher.rotation)
            matrixStack.scale(-0.025f, -0.025f, 0.025f)
            val matrix4f = matrixStack.peek().model
            val backgroundOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f)
            val backgroundcolor = (backgroundOpacity * 255.0f).toInt() shl 24
            val textRenderer: TextRenderer = MinecraftClient.getInstance().textRenderer
            val nameWidth = (-textRenderer.getWidth(name) / 2).toFloat()
            textRenderer.draw(name, nameWidth, 0f, 553648127, false, matrix4f, vertexConsumerProvider, notSneaking, backgroundcolor, int_1)
            textRenderer.draw(name, nameWidth, 0f, -1, false, matrix4f, vertexConsumerProvider, false, 0, int_1)
            matrixStack.pop()
        }
    }

}