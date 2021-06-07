package dev.toastmc.toastclient.api.util

import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.DamageUtil
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.explosion.Explosion
import kotlin.math.max

object CrystalUtil {

    fun LivingEntity.calculateCrystalDamage(entity: LivingEntity) {
        calculateCrystalDamage(entity.pos)
    }

    fun LivingEntity.calculateCrystalDamage(pos: Vec3d): Float {
        val distance = (squaredDistanceTo(pos).toFloat() / 12.0f) - 1f
        val damage = (distance * distance + distance) / 2.0f * 7.0f * 12.0f + 1.0f
        val explosion = Explosion(mc.world, null, pos.x, pos.y, pos.z, 6.0f, false, Explosion.DestructionType.DESTROY)
        return getBlastReduction(damage * world.getDamageMultiplier(), explosion)
    }

    fun LivingEntity.getBlastReduction(incomingDamage: Float, explosion: Explosion): Float {
        var damage = incomingDamage
        if (this is PlayerEntity) {
            val player: PlayerEntity = this
            val source = DamageSource.explosion(explosion)

            damage = DamageUtil.getDamageLeft(
                damage,
                player.armor.toFloat(),
                player.attributes.getValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).toFloat()
            )

            val protection = EnchantmentHelper.getProtectionAmount(player.armorItems, source).toFloat()
            val clamped = MathHelper.clamp(protection, 0.0f, 20.0f)

            damage *= 1.0f - clamped / 25.0f

            if (hasStatusEffect(StatusEffects.RESISTANCE)) {
                damage -= damage / 4.0f
            }

            return max(damage, 0.0f)
        }
        return DamageUtil.getDamageLeft(damage, armor.toFloat(), attributes.getValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).toFloat())
    }

    fun World.getDamageMultiplier(): Float {
        return if (difficulty.id == 0) 0.0f else if (difficulty.id == 2) 1.0f else if (difficulty.id == 1) 0.5f else 1.5f
    }

}