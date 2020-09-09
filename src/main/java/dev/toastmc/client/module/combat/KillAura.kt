package dev.toastmc.client.module.combat

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.RenderEvent
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.*
import dev.toastmc.client.util.ItemUtil.equipBestWeapon
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11

@ModuleManifest(
    label = "KillAura",
    description = "Hit mobs automatically",
    category = Category.COMBAT,
    aliases = ["aura"]
)
class KillAura : Module() {
    @Setting(name = "Reach") var reach = 4.5
    @Setting(name = "AutoSwitch") var autoswitch = true
    @Setting(name = "IgnoreEating") var ignoreeating = true
    @Setting(name = "Players") var players = true
    @Setting(name = "Mobs") var mobs = true
    @Setting(name = "Animals") var animals = true
    @Setting(name = "Vehicles") var vehicles = true
    @Setting(name = "Rotate") var rotate = true
    @Setting(name = "Render") var render = true

    private var oldSlot = -1
    private var newSlot = 0
    private var weaponSlot = 0

    companion object {
        var target: LivingEntity? = null
    }

    override fun onEnable() {
        if (mc.player == null) return
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
        ToastClient.EVENT_BUS.subscribe(onWorldRenderEvent)
        target = null
    }

    override fun onDisable() {
        if (mc.player == null) return
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
        ToastClient.EVENT_BUS.unsubscribe(onWorldRenderEvent)
        target = null
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        val damageCache = DamageUtil.getDamageCache(); damageCache.clear()
        if (target == null || target!!.removed || target!!.isDead) {
            target = findTarget(reach)
        }
        if (target == null) {
            return@EventHook
        }
        oldSlot = mc.player!!.inventory.selectedSlot
        val shield = mc.player!!.offHandStack.item === Items.SHIELD
        when {
            (mc.player!!.inventory.mainHandStack.isFood && ignoreeating) ->
                return@EventHook
            autoswitch ->
                weaponSlot = equipBestWeapon()
        }
        if (mc.player!!.getAttackCooldownProgress(0f) < 1f ||
            (shield && mc.player!!.isUsingItem))
            return@EventHook
        mc.interactionManager?.attackEntity(mc.player, target)
        mc.player!!.swingHand(Hand.MAIN_HAND)
        target = findTarget(reach)
    })

    private fun findTarget(range: Double): LivingEntity? {
        var foundTarget: LivingEntity? = null
        var sd: Double? = null
        for (entity in mc.world!!.entities) {
            if (entity == null || entity.removed || entity !is LivingEntity || entity.isDead || !entity.isAttackable) continue
            if ((entity is PlayerEntity &&                                          players && entity != mc.player) ||
                (EntityUtils.isHostile(entity) &&                                   mobs)                           ||
                ((EntityUtils.isAnimal(entity) || EntityUtils.isNeutral(entity)) && animals)                        ||
                (EntityUtils.isVehicle(entity) &&                                   vehicles))                      {
                val distance = mc.player!!.distanceTo(entity)
                if (canReach(
                        mc.player!!.pos.add(0.0, mc.player!!.getEyeHeight(mc.player!!.pose).toDouble(), 0.0),
                        entity.boundingBox,
                        range
                    ) && (sd == null || distance < sd)
                ) {
                    sd = distance.toDouble()
                    foundTarget = entity
                }
            }
        }
        return foundTarget
    }

    fun canReach(point: Vec3d, aabb: Box, maxRange: Double): Boolean {
        return aabb.expand(maxRange).contains(point)
    }

    @EventHandler
    val onWorldRenderEvent = Listener(EventHook<RenderEvent.World> {
        if (!render || target == null || target!!.removed) return@EventHook
        draw3d(translate = true) {
            begin(GL11.GL_QUADS) {
                color(255, 0, 0, 128)
                box(target!!.boundingBox)
            }
        }
    })
}