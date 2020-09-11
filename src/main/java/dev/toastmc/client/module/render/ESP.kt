package dev.toastmc.client.module.render

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.RenderEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.module.combat.KillAura
import dev.toastmc.client.util.*
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import org.lwjgl.opengl.GL11.GL_QUADS

@ModuleManifest(
    label = "ESP",
    description = "Renders boxes around entities",
    category = Category.RENDER
)
class ESP : Module(){
    // TODO: figure out how to save Color4i/Color4f with fiber
    @Setting(name = "Render Players")   var renderPlayers = true
    /*@Setting(name = "Player Color")*/     var playerColor = Color4i(255, 0, 255, 128)
    @Setting(name = "Render Hostiles")  var renderHostiles = true
    /*@Setting(name = "Hostile Color")*/    var hostileColor = Color4i(255, 0, 0, 128)
    @Setting(name = "Render Neutrals")  var renderNeutrals = true
    /*@Setting(name = "Neutral Color")*/    var neutralColor = Color4i(255, 255, 255, 128)
    @Setting(name = "Render Passives")  var renderPassives = true
    /*@Setting(name = "Passive Color")*/    var passiveColor = Color4i(0, 255, 0, 128)
    @Setting(name = "Render Items")     var renderItems = true
    /*@Setting(name = "Item Color")*/       var itemColor = Color4i(255, 255, 0, 128)
    @Setting(name = "Render Vehicles")     var renderVehicles = true
    /*@Setting(name = "Vehicle Color")*/       var vehicleColor = Color4i(255, 165, 0, 128)
    @Setting(name = "Render Self")      var renderSelf = false
    /*@Setting(name = "Self Color")*/       var selfColor = Color4i(0, 255, 255, 128)
    @Setting(name = "Render Others")     var renderOthers = true

    @EventHandler
    val onWorldRenderEvent = Listener(EventHook<RenderEvent.World> {
        draw3d(translate = true) {
            begin(GL_QUADS) {
                if (renderSelf) {
                    color(selfColor)
                    box(mc.player!!.boundingBox)
                }
                for (i in 0 until mc.world!!.entities!!.count()) {
                    val entity: Entity? = mc.world!!.entities!!.elementAtOrNull(i)
                    if (entity == null || !entity.isAlive || entity == mc.player!! || entity == KillAura.target) continue
                    when {
                        renderPlayers && entity is PlayerEntity -> {
                            color(playerColor)
                        }
                        renderVehicles && EntityUtils.isVehicle(entity) -> {
                            color(vehicleColor)
                        }
                        renderHostiles && EntityUtils.isHostile(entity) -> {
                            color(hostileColor)
                        }
                        renderNeutrals && EntityUtils.isNeutral(entity) -> {
                            color(neutralColor)
                        }
                        renderPassives && EntityUtils.isAnimal(entity) -> {
                            color(passiveColor)
                        }
                        renderItems && entity is Item -> {
                            color(itemColor)
                        }
                        renderOthers -> {
                            color(Color4i(0, 0, 0, 128))
                        }
                    }
                    box(entity.boundingBox)
                }
            }
        }
    })

    override fun onEnable() {
        ToastClient.EVENT_BUS.subscribe(onWorldRenderEvent)
    }

    override fun onDisable() {
        ToastClient.EVENT_BUS.unsubscribe(onWorldRenderEvent)
    }
}