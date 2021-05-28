package dev.toastmc.toastclient.impl.module.player

import dev.toastmc.toastclient.api.managers.FriendManager
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.lit
import dev.toastmc.toastclient.api.util.message
import dev.toastmc.toastclient.impl.command.FriendCommand
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Formatting
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import org.lwjgl.glfw.GLFW

object MCF : Module("MCF", Category.PLAYER) {

    var antiSpam = false

    override fun onUpdate() {
        val button = GLFW.GLFW_MOUSE_BUTTON_MIDDLE
        if(GLFW.glfwGetMouseButton(mc.window.handle, button) == 1 && !antiSpam){
            antiSpam = true
            if(mc.crosshairTarget!!.type == HitResult.Type.ENTITY){
                val hitResult = mc.crosshairTarget
                val entityHitResult = hitResult as EntityHitResult
                val entity = entityHitResult.entity

                if (entity is PlayerEntity){
                    val name = entity.displayName.string

                    if (FriendManager.isFriend(entity)) {
                        FriendManager.removeFriend(entity)
                        message(lit("${FriendCommand.prefix} $name has been ${Formatting.RED}removed${Formatting.GRAY} as friend").formatted(Formatting.GRAY))
                    }
                    else {
                        FriendManager.addFriend(entity)
                        message(lit("${FriendCommand.prefix} $name has been ${Formatting.GREEN}added${Formatting.GRAY} as friend").formatted(Formatting.GRAY))
                    }
                }
            }
        } else if (GLFW.glfwGetMouseButton(mc.window.handle, button) == 0){
            antiSpam = false
        }
    }

}