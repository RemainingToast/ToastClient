package dev.toastmc.toastclient.impl.module.combat

import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.InventoryUtil.switchToHotbarItem
import dev.toastmc.toastclient.api.util.WorldUtil.blockPos
import dev.toastmc.toastclient.api.util.WorldUtil.isCrystalSpot
import dev.toastmc.toastclient.api.util.entity.DamageUtil.getCrystalDamage
import dev.toastmc.toastclient.api.util.entity.EntityUtil
import dev.toastmc.toastclient.api.util.entity.EntityUtil.predictEyePos
import dev.toastmc.toastclient.api.util.entity.canReach
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
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
    var autoSwitch = bool("AutoSwitch", true)
    var switchBack = bool("SwitchBack", true)
    var placeOptions = group("Place", placeRange, autoSwitch, switchBack)

    var breakToggle = bool("Break", true)
    var breakRange = number("BreakRange", 3.0, 0.0, 8.0, 1)
    var minDamage = number("MinDamage", 5.5, 1.0, 20.0, 1)
    var antiSuicide = bool("AntiSuicide", true)
    var maxSelfDamage = number("MaxSelfDmg", 2.0, 0.0, 20.0, 1)
    var maxPlaced = number("MaxPlaced", 2, 1, 10, 0)
    var breakOptions = group("Break", breakRange, minDamage, antiSuicide, maxSelfDamage, maxPlaced)

    var targetRange = number("TargetRange", 10.0, 0.0, 30.0, 1)
    var targetBy = mode("TargetBy", "Damage", "Distance", "MostHP", "LeastHP", "Damage", "NetDamage")
    var players = bool("Players", true)
    var passives = bool("Passives", false)
    var neutrals = bool("Neutrals", false)
    var hostiles = bool("Hostiles", false)
    var targetOptions =
        group("Targets", targetRange, targetBy, players, passives, neutrals, hostiles)

    var target: LivingEntity? = null
    var spot: BlockPos? = null
    var placed = 0

    var counter: Byte = 0

    override fun onUpdate() {
        if (mc.player == null) return
        counter++
        if (counter < 2) return
        else counter = 0

        GlobalScope.async {
            prePlace()
        }.start()

        if (!breakCrystals()) {
            place()
        }
    }

    private fun place(): Boolean {
        val slot = mc.player!!.inventory.selectedSlot
        return if (spot != null && (autoSwitch.value && mc.player!!.switchToHotbarItem(Items.END_CRYSTAL)) || mc.player!!.inventory.mainHandStack.item == Items.END_CRYSTAL) {
            placeCrystal(spot)
            if (autoSwitch.value && switchBack.value) {
                mc.player!!.inventory.selectedSlot = slot
            }
            ++placed
            true
        } else {
            false
        }
    }

    private fun breakCrystals(): Boolean {
        if (mc.player == null || !breakToggle.value) return false
        if (target == null) return false
        if (target!!.isDead) {
            target = null
            placed = 0
            return false
        }

        val range = breakRange.value
        val crystals = mc.world!!.entities.filterIsInstance(EndCrystalEntity::class.java).filter {
            mc.player!!.canReach(it, range) && target!!.getCrystalDamage(
                it
            ) > minDamage.value
                    && mc.player!!.getCrystalDamage(it) <= maxSelfDamage.value &&
                    (!antiSuicide.value || mc.player!!.health - mc.player!!.getCrystalDamage(
                        it
                    ) > 0)
        }
        val crystal =
            crystals.maxByOrNull { target!!.getCrystalDamage(it) }
                ?: return false
        if (antiSuicide.value && mc.player!!.health - mc.player!!.getCrystalDamage(crystal) <= 0) return false

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
        if (spot != null && (mc.player!!.mainHandStack.item === Items.END_CRYSTAL
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
            spot = null
        }
    }

    fun findTargets(): List<LivingEntity> {
        return mc.world!!.entities.filterIsInstance(LivingEntity::class.java).filter {
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
    }

    fun getIdealTarget(
        targets: List<LivingEntity>,
        spots: List<BlockPos>,
        eyePos: Vec3d
    ): LivingEntity? {
        return when (targetBy.value) {
            "Distance" -> {
                targets.minByOrNull {
                    it.pos.add(it.velocity).distanceTo(eyePos)
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
                targets.maxByOrNull {
                    val spot = spots.maxByOrNull { pos ->
                        it.getCrystalDamage(pos)
                    }
                    spot?.getCrystalDamage(it) ?: 0f
                }
            }
            else -> return null
        }
    }

    fun getCube(center: Vec3d, range: Int): List<BlockPos> {
        val cube = mutableListOf<BlockPos>()
        (-range..range).forEach { x ->
            (-range..range).forEach { y ->
                (-range..range).forEach { z ->
                    cube.add(center.blockPos.add(BlockPos(x, y, z)))
                }
            }
        }
        return cube
    }

    fun filterSpots(spots: List<BlockPos>, range: Double): List<BlockPos> {
        return spots.filter {
            mc.player!!.canReach(
                Box(it),
                range
            ) && it.isCrystalSpot && mc.player!!.getCrystalDamage(
                it
            ) <= maxSelfDamage.value && (!antiSuicide.value || mc.player!!.health - mc.player!!.getCrystalDamage(
                it
            ) > 0)
        }.toMutableList()
    }

    fun prePlace(): Boolean {
        if (mc.player == null || !placeToggle.value || (!autoSwitch.value && mc.player!!.inventory.mainHandStack.item != Items.END_CRYSTAL)) return false

        val range = placeRange.value
        val eyePos = mc.player!!.predictEyePos().add(mc.player!!.velocity)

        val targets = findTargets()
        val spots = filterSpots(getCube(eyePos, ceil(range).toInt()), range)

        target = getIdealTarget(targets, spots, eyePos) ?: return false

        spot = spots.maxByOrNull { target!!.getCrystalDamage(it) }
        if (spot == null) return false

        if (target!!.getCrystalDamage(spot!!) < minDamage.value) return false
        return true
    }

}
