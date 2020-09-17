package dev.toastmc.client.module.combat

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.FriendUtil
import dev.toastmc.client.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import org.lwjgl.glfw.GLFW

@ModuleManifest(
        label = "MiddleClickFriends",
        category = Category.COMBAT
)
class MiddleClickFriends : Module() {

    override fun onEnable() {
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
    }

    var antiSpam = false

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
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
                    if (FriendUtil.isFriend(uuid)) FriendUtil.remove(name, uuid) else FriendUtil.add(name, uuid)
                }
            }
        } else if (GLFW.glfwGetMouseButton(mc.window.handle, button) == 0){
            antiSpam = false
        }
    })
}