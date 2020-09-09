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
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11.GL_LINE_STRIP


@ModuleManifest(
    label = "Tracers",
    description = "Traces lines to entities",
    category = Category.RENDER
)
class Tracers : Module(){
    // TODO: figure out how to save Color3i/Color3f with fiber
    @Setting(name = "Trace Players")   var renderPlayers = true
    /*@Setting(name = "Player Color")*/     var playerColor = Color3i(255, 0, 255)
    @Setting(name = "Trace Hostiles")  var renderHostiles = true
    /*@Setting(name = "Hostile Color")*/    var hostileColor = Color3i(255, 0, 0)
    @Setting(name = "Trace Neutrals")  var renderNeutrals = true
    /*@Setting(name = "Neutral Color")*/    var neutralColor = Color3i(255, 255, 255)
    @Setting(name = "Trace Passives")  var renderPassives = true
    /*@Setting(name = "Passive Color")*/    var passiveColor = Color3i(0, 255, 0)
    @Setting(name = "Trace Items")     var renderItems = true
    /*@Setting(name = "Item Color")*/       var itemColor = Color3i(255, 255, 0)
    @Setting(name = "Trace Vehicles")     var renderVehicles = true
    /*@Setting(name = "Vehicle Color")*/       var vehicleColor = Color3i(255, 165, 0)

    @EventHandler
    val onWorldRenderEvent = Listener(EventHook<RenderEvent.World> {
        draw3d(translate = true) {
            begin(GL_LINE_STRIP) {
                for (i in 0 until mc.world!!.entities!!.count()) {
                    val entity: Entity? = mc.world!!.entities!!.elementAtOrNull(i)
                    if (entity == null || !entity.isAlive || entity == mc.player!!) continue
                    when {
                        entity == KillAura.target -> {
                            color(255, 0, 0, 255)
                        }
                        renderPlayers && entity is PlayerEntity && entity != mc.player!! -> {
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
                    }
                    val camera =
                        Vec3d(0.0, 0.0, 75.0).rotateX((-Math.toRadians(mc.cameraEntity!!.pitch.toDouble())).toFloat())
                            .rotateY((-Math.toRadians(mc.cameraEntity!!.yaw.toDouble())).toFloat())
                            .add(
                                mc.cameraEntity!!.pos.add(
                                    0.0,
                                    mc.cameraEntity!!.getEyeHeight(mc.cameraEntity!!.pose).toDouble(),
                                    0.0
                                )
                            )
                    line(entity.pos.add(0.0, entity.height / 2.0, 0.0), camera)
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