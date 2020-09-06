package dev.toastmc.client.module.combat

import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.EntityUtils
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.block.Blocks
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.DamageUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.*
import net.minecraft.world.Difficulty
import net.minecraft.world.RaycastContext
import net.minecraft.world.explosion.Explosion
import java.util.*
import kotlin.collections.HashMap

@ModuleManifest(
    label = "AutoCrystal",
    description = "Hit crystals Automatically",
    category = Category.COMBAT,
    aliases = ["crystal", "crystalaura"]
)
class AutoCrystal : Module() {
    @Setting(name = "Players") var players = true
    @Setting(name = "Mobs") var mobs = true
    @Setting(name = "Animals") var animals = true
    @Setting(name = "Explode") var explode = true
    @Setting(name = "Place") var place = true
    @Setting(name = "Range") var range = 4
    @Setting(name = "MaxSelfDamage") var maxselfdamage = 0
    @Setting(name = "Rotate") var rotate = true
    @Setting(name = "AutoSwitch") var autoswitch = true
    @Setting(name = "AntiWeakness") var antiweakness = true
    @Setting(name = "IgnoreEating") var ignoreeating = true
    @Setting(name = "IgnoreTool") var ignoretool = false

    private val damageCache: HashMap<Entity, Float> = HashMap()

    private var togglePitch = false
    private var isAttacking = false
    private var oldSlot = -1
    private var newSlot = 0
    private var crystalSlot = 0
    private var breaks = 0
    private var offhand = false

    private val blackList = HashMap<BlockPos, Int>()

    override fun onDisable() {
        if (mc.player == null) return
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
    }

    override fun onEnable() {
        if (mc.player == null) return
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        damageCache.clear()
        var shortestDistance: Double? = null
        var crystal: EndCrystalEntity? = null
        for (entity in mc.world!!.entities) {
            if (entity == null || entity.removed || entity !is EndCrystalEntity) continue
            val p = entity.blockPos.down()
            if (blackList.containsKey(p)) {
                if (blackList[p]!! > 0) blackList.replace(p, blackList[p]!! - 1) else blackList.remove(p)
            }
            val distance = mc.player!!.distanceTo(entity)
            if (shortestDistance == null || distance < shortestDistance) {
                shortestDistance = distance.toDouble()
                crystal = entity
            }
        }
        offhand = mc.player!!.offHandStack.item === Items.END_CRYSTAL
        if (crystal == null) return@EventHook
        if (explode && mc.player?.distanceTo(crystal)!! <= range && safeToExplode(crystal.blockPos.down()) && !holdingFood(mc.player!!) && !holdingTool(mc.player!!)) {
            if (antiweakness && mc.player!!.hasStatusEffect(StatusEffects.WEAKNESS)) {
                newSlot = -1
                if (!isAttacking) {
                    oldSlot = mc.player!!.inventory.selectedSlot
                    isAttacking = true
                }
                if (!offhand) {
                    run {
                        for (l in 0..8) {
                            if (mc.player!!.inventory.getStack(l).item is ToolItem || mc.player!!.inventory.getStack(l).item is SwordItem) {
                                newSlot = l
                                break
                            }
                        }
                        if (newSlot != -1 ) {
                            mc.player!!.inventory.selectedSlot = newSlot
                            mc.interactionManager?.attackEntity(mc.player, crystal)
                            mc.player!!.swingHand(Hand.MAIN_HAND)
                            mc.player!!.inventory.selectedSlot = oldSlot
                        }
                    }
                } else {
                    run {
                        if (mc.player!!.inventory.mainHandStack.item is ToolItem || mc.player!!.inventory.mainHandStack.item is SwordItem) {
                            mc.interactionManager?.attackEntity(mc.player, crystal)
                            mc.player!!.swingHand(Hand.OFF_HAND)
                        } else {
                            for (l in 0..8) {
                                if (mc.player!!.inventory.getStack(l).item is ToolItem || mc.player!!.inventory.getStack(l).item is SwordItem) {
                                    newSlot = l
                                    break
                                }
                            }
                            if (newSlot != -1) {
                                mc.player!!.inventory.selectedSlot = newSlot
                                mc.interactionManager?.attackEntity(mc.player, crystal)
                                mc.player!!.swingHand(Hand.OFF_HAND)
                                mc.player!!.inventory.selectedSlot = oldSlot
                            }
                        }
                    }
                }
            }

            if(!offhand) {
                if (autoswitch && !isAttacking) {
                    mc.player!!.inventory.selectedSlot = crystalSlot;
                }
                mc.interactionManager?.attackEntity(mc.player, crystal)
                mc.player!!.swingHand(Hand.MAIN_HAND)
            } else {
                isAttacking = true
                mc.interactionManager?.attackEntity(mc.player, crystal)
                mc.player!!.swingHand(Hand.OFF_HAND)
            }

            ++breaks
            if (breaks == 2) {
                rotate = false
                this.breaks = 0
                return@EventHook
            }
        } else {
            rotate = false
            if (this.oldSlot != -1) {
                mc.player?.inventory?.selectedSlot = this.oldSlot
                this.oldSlot = -1
            }
            this.isAttacking = false
        }
        crystalSlot = if (mc.player!!.mainHandStack.item === Items.END_CRYSTAL) mc.player!!.inventory.selectedSlot else -1
        if (crystalSlot == -1) {
            for (l in 0..8) {
                if (mc.player!!.inventory.getStack(l).item === Items.END_CRYSTAL) {
                    crystalSlot = l
                    break
                }
            }
        }
        val entities: MutableList<Entity> = ArrayList()
        for (entity in mc.world!!.entities) {
            when {
                entity is PlayerEntity && players -> entities.add(entity)
                entity is MobEntity && mobs -> entities.add(entity)
                EntityUtils.isAnimal(entity) && animals -> entities.add(entity)
            }
        }
        val blocks = getCrystalPoses()
        var vec3i: BlockPos? = null
        var damage = 0.5
        val entityIter: Iterator<Entity> = entities.iterator()
        var blockPos: BlockPos
        var d: Double
        var self: Double
        var b: Double
        val blockIter = blocks!!.iterator()
        var entity: Entity

        main_loop@ while (true) {
            do {
                do {
                    if (!entityIter.hasNext()) {
                        if (place) {
                            if (!offhand && mc.player!!.inventory.selectedSlot != crystalSlot || vec3i == null) return@EventHook
                            val result: BlockHitResult = mc.world!!.raycast(
                                RaycastContext(
                                    Vec3d(
                                        mc.player!!.x,
                                        mc.player!!.y + mc.player!!.getEyeHeight(mc.player!!.pose),
                                        mc.player!!.z
                                    ),
                                    Vec3d(vec3i.x + 0.5, vec3i.y - 0.5, vec3i.z + 0.5),
                                    RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player
                                )
                            )
                            val side: Direction = if (result.side != null) result.side else Direction.UP
                            mc.interactionManager!!.interactBlock(
                                mc.player, mc.world, if (offhand) Hand.OFF_HAND else Hand.MAIN_HAND, BlockHitResult(
                                    Vec3d.of(
                                        vec3i
                                    ), side, vec3i, false
                                )
                            )
                            blackList[vec3i] = 5
                        }
                        if (rotate) {
                            if (togglePitch) {
                                mc.player!!.pitch += 4.0E-4f
                                togglePitch = false
                            } else {
                                mc.player!!.pitch -= 4.0E-4f
                                togglePitch = true
                            }
                        }
                        return@EventHook
                    }
                    entity = entityIter.next()
                } while (entity === mc.player)
            } while ((entity as LivingEntity).health <= 0.0f)
            while (true) {
                do {
                    do {
                        do {
                            if (!blockIter.hasNext()) {
                                continue@main_loop
                            }
                            blockPos = blockIter.next()
                            b = entity.getBlockPos().getSquaredDistance(blockPos)
                        } while (b >= 169.0)
                        d = getExplosionDamage(blockPos, entity).toDouble()
                    } while (d <= damage)
                    self = getExplosionDamage(blockPos, mc.player!!).toDouble()
                } while (self > d && d >= entity.health)
                if (self - 0.5 <= mc.player!!.health) {
                    damage = d
                    vec3i = blockPos
                }
            }
        }
    })

    private fun safeToExplode(blockPos: BlockPos) : Boolean {
        val p = mc.player!!
        if (p.isInvulnerable || p.isCreative || p.isSpectator) return true
        return getExplosionDamage(blockPos, p) - p.health <= maxselfdamage - 1
    }

    private fun holdingFood(player: PlayerEntity) : Boolean {
        if (ignoreeating) return false
        val selectedSlot = mc.player!!.inventory.selectedSlot
        return player.inventory.getStack(selectedSlot).isFood
    }

    private fun holdingTool(player: PlayerEntity) : Boolean {
        if (ignoretool) return false
        val selectedSlot = mc.player!!.inventory.selectedSlot
        return player.inventory.getStack(selectedSlot).item is ToolItem
    }

    private fun getCrystalPoses(): Set<BlockPos>? {
        val poses: MutableSet<BlockPos> = HashSet()
        val range = Math.ceil(range.toDouble()).toInt()
        for (x in -range until range + 1) {
            for (y in -range until range) {
                for (z in -range until range + 1) {
                    val basePos = mc.player!!.blockPos.add(x, y, z)
                    if (!canPlace(basePos) || blackList.containsKey(basePos)) continue
                    if (mc.player!!.pos.distanceTo(Vec3d.of(basePos).add(0.5, 1.0, 0.5)) <= range + 0.25
                    ) poses.add(basePos)
                }
            }
        }
        return poses
    }

    private fun canPlace(basePos: BlockPos): Boolean {
        val baseState = mc.world!!.getBlockState(basePos)
        if (baseState.block !== Blocks.BEDROCK && baseState.block !== Blocks.OBSIDIAN) return false
        val placePos = basePos.up()
        return if (!mc.world!!.isAir(placePos)) false
        else mc.world!!.getOtherEntities(null as Entity?, Box(placePos).stretch(0.0, 1.0, 0.0)).isEmpty()
    }

    private fun getExplosionDamage(basePos: BlockPos, target: LivingEntity): Float {
        if (mc.world!!.difficulty == Difficulty.PEACEFUL) return 0f
        if (damageCache.containsKey(target)) return damageCache[target]!!
        val crystalPos: Vec3d = Vec3d.of(basePos).add(0.5, 1.0, 0.5)
        val explosion = Explosion(
            mc.world,
            null,
            crystalPos.x,
            crystalPos.y,
            crystalPos.z,
            6f,
            false,
            Explosion.DestructionType.DESTROY
        )
        val power = 12.0
        if (!mc.world!!.getOtherEntities(
                null as Entity?, Box(
                    MathHelper.floor(crystalPos.x - power - 1.0).toDouble(),
                    MathHelper.floor(crystalPos.y - power - 1.0).toDouble(),
                    MathHelper.floor(crystalPos.z - power - 1.0).toDouble(),
                    MathHelper.floor(crystalPos.x + power + 1.0).toDouble(),
                    MathHelper.floor(crystalPos.y + power + 1.0).toDouble(),
                    MathHelper.floor(crystalPos.z + power + 1.0).toDouble()
                )
            ).contains(target)
        ) {
            damageCache[target] = 0f
            return 0f
        }
        if (!target.isImmuneToExplosion) {
            val double_8 = MathHelper.sqrt(target.squaredDistanceTo(crystalPos)) / power
            if (double_8 <= 1.0) {
                var double_9: Double = target.x - crystalPos.x
                var double_10: Double = target.y + target.standingEyeHeight - crystalPos.y
                var double_11: Double = target.z - crystalPos.z
                val double_12 =
                    MathHelper.sqrt(double_9 * double_9 + double_10 * double_10 + double_11 * double_11).toDouble()
                if (double_12 != 0.0) {
                    double_9 /= double_12
                    double_10 /= double_12
                    double_11 /= double_12
                    val double_13 = Explosion.getExposure(crystalPos, target).toDouble()
                    val double_14 = (1.0 - double_8) * double_13

                    // entity_1.damage(explosion.getDamageSource(), (float)((int)((double_14 *
                    // double_14 + double_14) / 2.0D * 7.0D * power + 1.0D)));
                    var toDamage =
                        Math.floor((double_14 * double_14 + double_14) / 2.0 * 7.0 * power + 1.0).toFloat()
                    if (target is PlayerEntity) {
                        if (mc.world!!.difficulty == Difficulty.EASY) toDamage = Math.min(
                            toDamage / 2.0f + 1.0f,
                            toDamage
                        ) else if (mc.world!!.difficulty == Difficulty.HARD) toDamage = toDamage * 3.0f / 2.0f
                    }

                    // Armor
                    toDamage = DamageUtil.getDamageLeft(
                        toDamage, target.armor.toFloat(), target.getAttributeInstance(
                            EntityAttributes.GENERIC_ARMOR_TOUGHNESS
                        )!!.value.toFloat()
                    )

                    // Enchantments
                    if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
                        val resistance = (target.getStatusEffect(StatusEffects.RESISTANCE)!!.amplifier + 1) * 5
                        val int_2 = 25 - resistance
                        val resistance_1 = toDamage * int_2
                        toDamage = Math.max(resistance_1 / 25.0f, 0.0f)
                    }
                    if (toDamage <= 0.0f) {
                        toDamage = 0.0f
                    } else {
                        val protAmount =
                            EnchantmentHelper.getProtectionAmount(target.armorItems, explosion.damageSource)
                        if (protAmount > 0) {
                            toDamage = DamageUtil.getInflictedDamage(toDamage, protAmount.toFloat())
                        }
                    }
                    damageCache[target] = toDamage
                    return toDamage
                }
            }
        }
        damageCache[target] = 0f
        return 0f
    }

    private fun getBlastReduction(entity: LivingEntity, damage: Float, explosion: Explosion): Float {
        var damage = damage
        return if (entity is PlayerEntity) {
            val ep = entity
            val ds = DamageSource.explosion(explosion)
            damage = DamageUtil.getDamageLeft(
                damage, entity.getArmor().toFloat(), entity.getAttributeInstance(
                    EntityAttributes.GENERIC_ARMOR_TOUGHNESS
                )!!.value.toFloat()
            )
            val k = EnchantmentHelper.getProtectionAmount(ep.armorItems, ds)
            val f = MathHelper.clamp(k.toFloat(), 0.0f, 20.0f)
            damage *= 1.0f - f / 25.0f
            if (entity.hasStatusEffect(StatusEffects.RESISTANCE)) {
                damage -= damage / 4.0f
            }
            damage = Math.max(damage - ep.absorptionAmount, 0.0f)
            damage
        } else {
            damage = DamageUtil.getDamageLeft(
                damage, entity.armor.toFloat(), entity.getAttributeInstance(
                    EntityAttributes.GENERIC_ARMOR_TOUGHNESS
                )!!.value.toFloat()
            )
            damage
        }
    }
}