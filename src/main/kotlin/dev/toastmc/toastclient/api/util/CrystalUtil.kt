package dev.toastmc.toastclient.api.util

import dev.toastmc.toastclient.api.util.WorldUtil.crystalMultiplier
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.DamageUtil
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
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
        return calculateCrystalDamage(damage * world.crystalMultiplier(), explosion)
    }

    private fun LivingEntity.calculateCrystalDamage(incomingDamage: Float, explosion: Explosion): Float {
        var damage = incomingDamage
        if (this is PlayerEntity) {
            val player: PlayerEntity = this
            val source = explosion.damageSource

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

}