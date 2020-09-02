package dev.toastmc.client.module.render

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.RenderEvent
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.EntityUtils
import dev.toastmc.client.util.box
import dev.toastmc.client.util.color
import dev.toastmc.client.util.draw3d
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import io.netty.util.internal.ConcurrentSet
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Box
import org.lwjgl.opengl.GL11.GL_QUADS

@ModuleManifest(
    label = "ESP",
    description = "Renders boxes around entities",
    category = Category.RENDER
)
class ESP : Module(){
    @Setting(name = "Render Players") var renderPlayers = true
    @Setting(name = "Render Hostiles") var renderHostiles = true
    @Setting(name = "Render Passives") var renderPassives = true
    @Setting(name = "Render Neutrals") var renderNeutrals = true
    @Setting(name = "Render Items") var renderItems = true
    @Setting(name = "Render Self") var renderSelf = false

    private var renderSet = ConcurrentSet<Box>()

    private var isCoroutineRunning = false;

    @EventHandler
    val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (isCoroutineRunning) return@EventHook
        GlobalScope.launch {
            renderSet.clear()
            isCoroutineRunning = true
            for (i in 0 until mc.world!!.entities!!.count()) {
                val entity: Entity? = mc.world!!.entities!!.elementAtOrNull(i)
                if (entity == null || !entity.isAlive) continue
                if ((renderSelf && entity == mc.player!!) ||
                    (renderPlayers && entity is PlayerEntity && entity != mc.player!!) ||
                    (renderHostiles && EntityUtils.isHostile(entity)) ||
                    (renderNeutrals && EntityUtils.isNeutral(entity)) ||
                    (renderPassives && EntityUtils.isAnimal(entity)) ||
                    (renderItems && entity is ItemEntity)
                ) {
                    renderSet.add(entity.boundingBox)
                }
            }
            isCoroutineRunning = false
        }
    })

    @EventHandler
    val onWorldRenderEvent = Listener(EventHook<RenderEvent.World> {
        draw3d(translate = true) {
            begin(GL_QUADS) {
                for (i in 0 until renderSet.count()) {
                    color(0f, 1f, 0f, 0.5f)
                    box(renderSet.elementAt(i))
                }
            }
        }
    })

    override fun onEnable() {
        ToastClient.EVENT_BUS.subscribe(onWorldRenderEvent)
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
        renderSet.clear()
    }

    override fun onDisable() {
        ToastClient.EVENT_BUS.unsubscribe(onWorldRenderEvent)
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
        renderSet.clear()
    }
}