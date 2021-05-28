package dev.toastmc.toastclient.impl.module.player

import dev.toastmc.toastclient.api.managers.FriendManager
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.lit
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
                val name = entity.displayName.string
                val uuid = entity.uuid
                if (entity is PlayerEntity){
                    println("You middle clicked $name their uuid is $uuid")
                    if (FriendManager.isFriend(uuid)) FriendManager.removeFriend(name, uuid) else FriendManager.addFriend(name, uuid)
                    mc.player!!.sendMessage(lit("Friend: \"$name\" ${if (FriendManager.isFriend(uuid)) "added" else "removed"}").formatted(if (FriendManager.isFriend(uuid)) Formatting.GREEN else Formatting.RED), false)
                }
            }
        } else if (GLFW.glfwGetMouseButton(mc.window.handle, button) == 0){
            antiSpam = false
        }
    }

}