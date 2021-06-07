package dev.toastmc.toastclient.impl.module.combat

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.InventoryUtil
import dev.toastmc.toastclient.api.util.WorldUtil.blockPos
import dev.toastmc.toastclient.api.util.WorldUtil.isCrystalSpot
import dev.toastmc.toastclient.api.util.entity.DamageUtil
import dev.toastmc.toastclient.api.util.entity.EntityUtil
import dev.toastmc.toastclient.api.util.entity.canReach
import dev.toastmc.toastclient.api.util.entity.eyePos
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.*
import kotlin.math.ceil

object CrystalAura : Module("CrystalAura", Category.COMBAT) {
    var placeToggle = bool("Place", true)
    var placeRange = number("PlaceRange", 4.5, 0.0, 8.0, 1)
    var placesPerTick = number("PlacesPerTick", 1, 1, 20, 0)
    var autoSwitch = bool("AutoSwitch", true)
    var switchBack = bool("SwitchBack", true)
    var placeOptions = group("PlaceOpt", placeRange, placesPerTick, autoSwitch)

    var breakToggle = bool("Break", true)
    var breakRange = number("BreakRange", 3.0, 0.0, 8.0, 1)
    var breakOptions = group("BreakOpt", breakRange)

    var targetRange = number("TargetRange", 10.0, 0.0, 30.0, 1)
    var targetBy = mode("TargetBy", "Damage", "Distance", "MostHP", "LeastHP", "Damage")
    var players = bool("Players", true)
    var passives = bool("Passives", false)
    var neutrals = bool("Neutrals", false)
    var hostiles = bool("Hostiles", false)
    var targetOptions =
        group("Targetting", targetRange, targetBy, players, passives, neutrals, hostiles)

    var minDamage = number("MinDamage", 5.5, 1.0, 20.0, 1)
    var antiSuicide = bool("AntiSuicide", true)
    var maxSelfDamage = number("MaxSelfDmg", 2.0, 0.0, 20.0, 1)
    var maxPlaced = number("MaxPlaced", 2, 1, 10, 0)
    var crystalOptions = group("Crystal", minDamage, maxSelfDamage, maxPlaced)

    var priority = mode("Priority", "Break", "Break", "Place")


    var target: LivingEntity? = null
    var placed = 0

    override fun onUpdate() {
        if (mc.player == null) return

        when (priority.value) {
            "Break" -> {
                if (!breakCrystals()) {
                    placeCrystals()
                }
            }
            "Place" -> {
                if (placed < maxPlaced.intValue && !placeCrystals()) {
                    breakCrystals()
                }
            }
        }
    }

    private fun placeCrystals(): Boolean {
        if (mc.player == null || !placeToggle.value || (!autoSwitch.value && mc.player!!.inventory.mainHandStack.item != Items.END_CRYSTAL)) return false

        val range = placeRange.value
        val blockRange = ceil(range).toInt()
        val eyePos = mc.player!!.eyePos
        val targets = mc.world!!.entities.filterIsInstance(LivingEntity::class.java).filter {
            it != mc.player
                    && !it.isDead
                    && !it.isImmuneToExplosion
                    && !it.isInvulnerable
                    && ((players.value && it is PlayerEntity)
                    || (passives.value && EntityUtil.isAnimal(it))
                    || (neutrals.value && EntityUtil.isNeutral(it))
                    || (hostiles.value && EntityUtil.isHostile(it)))
                    && mc.player!!.canReach(it, targetRange.value)
        }

        val potentialSpots = mutableListOf<BlockPos>()
        (-blockRange..blockRange).forEach { x ->
            (-blockRange..blockRange).forEach { y ->
                (-blockRange..blockRange).forEach { z ->
                    potentialSpots.add(eyePos.blockPos.add(BlockPos(x, y, z)))
                }
            }
        }

        for (attempt in 1..placesPerTick.intValue) {
            val spots = potentialSpots.filter {
                mc.player!!.canReach(
                    Box(it),
                    range
                ) && it.isCrystalSpot && (maxSelfDamage.value == 0.0 || DamageUtil.getCrystalDamage(
                    it,
                    mc.player!!
                ) <= maxSelfDamage.value)
            }

            target = when (targetBy.value) {
                "Distance" -> {
                    targets.minByOrNull {
                        it.pos.distanceTo(eyePos)
                    }
                }
                "MostHP" -> {
                    targets.maxByOrNull {
                        it.health
                    }
                }
                "LeastHP" -> {
                    targets.minByOrNull {
                        it.health
                    }
                }
                "Damage" -> {
                    val maxDamages = HashMap<LivingEntity, Double>()
                    targets.forEach { entity ->
                        maxDamages[entity] = spots.map { pos ->
                            DamageUtil.getCrystalDamage(pos, entity)
                        }.maxOrNull()?.toDouble() ?: 0.0
                    }
                    targets.maxByOrNull {
                        maxDamages[it]!!
                    }
                }
                else -> return false
            } ?: return false

            val spot = spots.maxByOrNull { DamageUtil.getCrystalDamage(it, target!!) } ?: return false

            if (DamageUtil.getCrystalDamage(spot, target!!) < minDamage.value) return false

            val slot = mc.player!!.inventory.selectedSlot
            if ((autoSwitch.value && InventoryUtil.switchToHotbarItem(Items.END_CRYSTAL)) || mc.player!!.inventory.mainHandStack.item == Items.END_CRYSTAL) {
                placeCrystal(spot)
                if (autoSwitch.value && switchBack.value) {
                    mc.player!!.inventory.selectedSlot = slot
                }
                ++placed
            } else {
                return false
            }
        }

        return true
    }

    private fun breakCrystals(): Boolean {
        if (mc.player == null || !breakToggle.value) return false
        if (target == null) return false
        if (target!!.isDead) {
            target = null
            return false
        }

        val range = breakRange.value
        val crystals = mc.world!!.entities.filterIsInstance(EndCrystalEntity::class.java).filter {
            mc.player!!.canReach(it, range) && DamageUtil.getCrystalDamage(
                it.blockPos.down(),
                target!!
            ) > minDamage.value && (maxSelfDamage.value == 0.0 || DamageUtil.getCrystalDamage(
                it.blockPos.down(),
                mc.player!!
            ) <= maxSelfDamage.value)
        }
        val crystal = crystals.maxByOrNull { DamageUtil.getCrystalDamage(it.blockPos.down(), target!!) } ?: return false
        if (mc.player!!.health - DamageUtil.getCrystalDamage(crystal.blockPos.down(), mc.player!!) <= 0 ) return false

        mc.interactionManager!!.attackEntity(mc.player, crystal)
        mc.player!!.swingHand(Hand.MAIN_HAND)
        placed = 0

        return true
    }

    private fun getYaw(vec: Vec3d): Double {
        return (mc.player!!.yaw + MathHelper.wrapDegrees(
            Math.toDegrees(
                kotlin.math.atan2(
                    vec.z - mc.player!!.z,
                    vec.x - mc.player!!.x
                )
            ) - 90f - mc.player!!.yaw
        ))
    }

    private fun getPitch(vec: Vec3d): Double {
        val diffX: Double = vec.x - mc.player!!.x
        val diffY: Double = vec.y - (mc.player!!.y + mc.player!!.getEyeHeight(mc.player!!.pose))
        val diffZ: Double = vec.z - mc.player!!.z
        val diffXZ: Double = kotlin.math.sqrt(diffX * diffX + diffZ * diffZ)
        return (mc.player!!.pitch + MathHelper.wrapDegrees(
            -Math.toDegrees(
                kotlin.math.atan2(
                    diffY,
                    diffXZ
                )
            ) - mc.player!!.pitch
        ))
    }

    private fun placeCrystal(pos: BlockPos?) {
        if ((mc.player!!.mainHandStack.item === Items.END_CRYSTAL
                    || mc.player!!.offHandStack.item === Items.END_CRYSTAL) && pos != null
        ) {
            val offhand = mc.player!!.offHandStack.item === Items.END_CRYSTAL
            val bop = pos.add(0.5, 0.5, 0.5)
            val vec = Vec3d(bop.x.toDouble(), bop.y.toDouble(), bop.z.toDouble())
            val yaw: Float = getYaw(vec).toFloat()
            val pitch: Float = getPitch(vec).toFloat()
            mc.player!!.networkHandler.sendPacket(
                PlayerMoveC2SPacket.LookOnly(
                    yaw,
                    pitch,
                    mc.player!!.isOnGround
                )
            )
            mc.interactionManager!!.interactBlock(
                mc.player,
                mc.world,
                if (offhand) Hand.OFF_HAND else Hand.MAIN_HAND,
                BlockHitResult(
                    Vec3d(
                        pos.x.toDouble(),
                        pos.y.toDouble(),
                        pos.z.toDouble()
                    ),
                    Direction.UP,
                    pos,
                    false
                )
            )
            mc.player!!.swingHand(if (offhand) Hand.OFF_HAND else Hand.MAIN_HAND)
            mc.player!!.networkHandler.sendPacket(
                PlayerMoveC2SPacket.LookOnly(
                    mc.player!!.yaw,
                    mc.player!!.pitch,
                    mc.player!!.isOnGround
                )
            )
        }
    }
}
