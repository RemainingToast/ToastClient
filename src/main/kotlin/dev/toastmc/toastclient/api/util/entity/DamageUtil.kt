package dev.toastmc.toastclient.api.util.entity

import dev.toastmc.toastclient.api.util.mc
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.DamageUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Difficulty
import net.minecraft.world.explosion.Explosion
import java.util.*

object DamageUtil {
    private val damageCache: HashMap<Entity, Float> = HashMap()

    fun getExplosionDamage(basePos: BlockPos, target: LivingEntity): Float {
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
                    MathHelper.floor(
                        crystalPos.y - power - 1.0
                    ).toDouble(),
                    MathHelper.floor(crystalPos.z - power - 1.0).toDouble(),
                    MathHelper.floor(crystalPos.x + power + 1.0).toDouble(),
                    MathHelper.floor(
                        crystalPos.y + power + 1.0
                    ).toDouble(),
                    MathHelper.floor(crystalPos.z + power + 1.0).toDouble()
                )
            ).contains(target)) {
            damageCache[target] = 0f
            return 0f
        }
        if (!target.isImmuneToExplosion) {
            val double_8 = MathHelper.sqrt(target.squaredDistanceTo(crystalPos)) / power
            if (double_8 <= 1.0) {
                var x: Double = target.x - crystalPos.x
                var y: Double = target.y + target.standingEyeHeight - crystalPos.y
                var z: Double = target.z - crystalPos.z
                val double_12 = MathHelper.sqrt(x * x + y * y + z * z).toDouble()
                if (double_12 != 0.0) {
                    x /= double_12
                    y /= double_12
                    z /= double_12
                    val double_13 = Explosion.getExposure(crystalPos, target).toDouble()
                    val double_14 = (1.0 - double_8) * double_13

                    // entity_1.damage(explosion.getDamageSource(), (float)((int)((double_14 *
                    // double_14 + double_14) / 2.0D * 7.0D * power + 1.0D)));
                    var toDamage = Math.floor((double_14 * double_14 + double_14) / 2.0 * 7.0 * power + 1.0).toFloat()
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
                    if (toDamage <= 0.0f) { toDamage = 0.0f } else {
                        val protAmount = EnchantmentHelper.getProtectionAmount(
                            target.armorItems,
                            explosion.damageSource
                        )
                        if (protAmount > 0) { toDamage = DamageUtil.getInflictedDamage(toDamage, protAmount.toFloat()) }
                    }
                    damageCache[target] = toDamage
                    return toDamage
                }
            }
        }
        damageCache[target] = 0f
        return 0f
    }

    fun getDamageCache(): HashMap<Entity, Float> {
        return damageCache
    }
}