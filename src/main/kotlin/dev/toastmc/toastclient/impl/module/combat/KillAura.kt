package dev.toastmc.toastclient.impl.module.combat

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.ItemUtil.equipBestWeapon
import dev.toastmc.toastclient.api.util.entity.EntityUtil
import dev.toastmc.toastclient.api.util.entity.canReach
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.Hand

// TODO Improve
object KillAura : Module("KillAura", Category.COMBAT) {

    val reach = number("Reach", 4.5, 1.0, 5.0, 2)

    //    val rotate = registerBoolean("Rotate", true)
    val autoSwitch = bool("AutoSwitch", false)
    val ignoreEating = bool("IgnoreEating", true)
    val players = bool("Players", true)
    val hostile = bool("Hostile", false)
    val passive = bool("Passive", false)
    val vehicles = bool("Vehicles", false)

    private var oldSlot = -1
    private var newSlot = 0
    private var weaponSlot = 0

    var target: LivingEntity? = null

    override fun onUpdate() {
        target = findTarget(reach.value) ?: return
        if (target!!.removed || target!!.isDead) {
            oldSlot = mc.player!!.inventory.selectedSlot
        }
        val shield = mc.player!!.offHandStack.item === Items.SHIELD
        when {
            mc.player!!.inventory.mainHandStack.isFood && ignoreEating.value -> return
            autoSwitch.value -> weaponSlot = equipBestWeapon()
        }
        if (mc.player!!.getAttackCooldownProgress(0f) < 1f || (shield && mc.player!!.isUsingItem)) return
        mc.interactionManager?.attackEntity(mc.player, target)
        mc.player!!.swingHand(Hand.MAIN_HAND)
        target = findTarget(reach.value)
        mc.player!!.resetLastAttackedTicks()
    }

    private fun findTarget(range: Double): LivingEntity? {
        val target = mc.world!!.entities.toList().parallelStream().filter { entity ->
            return@filter entity != null && !entity.removed && entity is LivingEntity && !entity.isDead && entity.isAttackable
                    && ((entity is PlayerEntity && players.value && entity != mc.player)
                    || (EntityUtil.isHostile(entity) && hostile.value)
                    || ((EntityUtil.isAnimal(entity) || EntityUtil.isNeutral(entity)) && passive.value)
                    || (EntityUtil.isVehicle(entity) && vehicles.value))
                    && mc.player!!.canReach(
                        entity.boundingBox,
                        range
                    )
        }.sorted { a, b ->
            mc.player!!.distanceTo(a).compareTo(mc.player!!.distanceTo(b))
        }.findFirst().orElse(null)
        return if (target == null) null else target as LivingEntity
    }

}