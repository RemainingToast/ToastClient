package me.remainingtoast.toastclient.client.module.combat

import me.remainingtoast.toastclient.ToastClient.Companion.EVENT_BUS
import me.remainingtoast.toastclient.api.event.TickEvent
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.util.DamageUtil
import me.remainingtoast.toastclient.api.util.EntityUtil
import me.remainingtoast.toastclient.api.util.ItemUtil.equipBestWeapon
import me.remainingtoast.toastclient.api.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

object KillAura : Module("KillAura", Category.COMBAT) {

    val reach = registerDouble("Reach", "",4.5, 1.0, 5.0, false)
    val rotate = registerBoolean("Rotate", true)
    val autoSwitch = registerBoolean("AutoSwitch", false)
    val ignoreEating = registerBoolean("IgnoreEating", true)
    val players = registerBoolean("Players", true)
    val hostile = registerBoolean("Hostile", false)
    val passive = registerBoolean("Passive", false)
    val vehicles = registerBoolean("Vehicles", false)

    private var oldSlot = -1
    private var newSlot = 0
    private var weaponSlot = 0

    var target: LivingEntity? = null

    override fun onEnable() {
        EVENT_BUS.subscribe(onTickEvent)
        target = null
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onTickEvent)
        target = null
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        val damageCache = DamageUtil.getDamageCache(); damageCache.clear()
        target = findTarget(reach.value) ?: return@EventHook
        if (target!!.removed || target!!.isDead)
            oldSlot = mc.player!!.inventory.selectedSlot
        val shield = mc.player!!.offHandStack.item === Items.SHIELD
        when {
            (mc.player!!.inventory.mainHandStack.isFood && ignoreEating.value) -> return@EventHook
            autoSwitch.value -> weaponSlot = equipBestWeapon()
        }
        if (mc.player!!.getAttackCooldownProgress(0f) < 1f || (shield && mc.player!!.isUsingItem)) return@EventHook
        mc.interactionManager?.attackEntity(mc.player, target)
        mc.player!!.swingHand(Hand.MAIN_HAND)
        target = findTarget(reach.value)
    })

    private fun findTarget(range: Double): LivingEntity? {
        var foundTarget: LivingEntity? = null
        var sd: Double? = null
        for (entity in mc.world!!.entities) {
            if (entity == null || entity.removed || entity !is LivingEntity || entity.isDead || !entity.isAttackable) continue
            if ((entity is PlayerEntity &&                                          players.value && entity != mc.player) ||
                (EntityUtil.isHostile(entity) &&                                   hostile.value)                           ||
                ((EntityUtil.isAnimal(entity) || EntityUtil.isNeutral(entity)) && passive.value)                        ||
                (EntityUtil.isVehicle(entity) &&                                   vehicles.value))                      {
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

    private fun canReach(point: Vec3d, aabb: Box, maxRange: Double): Boolean {
        return aabb.expand(maxRange).contains(point)
    }

}