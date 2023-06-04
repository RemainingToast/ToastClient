    package dev.toastmc.toastclient.api.util.entity

import com.mojang.authlib.GameProfile
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.OtherClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.*

fun ClientPlayerEntity.clone(): OtherClientPlayerEntity {
    return clone(gameProfile)
}

fun ClientPlayerEntity.clone(gameProfile: GameProfile): OtherClientPlayerEntity {
    val clone = OtherClientPlayerEntity(clientWorld, gameProfile, publicKey)
    clone.copyPositionAndRotation(this)
    clone.setHeadYaw(headYaw)
    return clone
}

fun ClientPlayerEntity.totemCount(): Int {
    return inventory.main
        .stream()
        .filter { item -> item.item === Items.TOTEM_OF_UNDYING }
        .mapToInt { item: ItemStack -> item.count }
        .sum() + inventory.offHand
        .stream()
        .filter { item -> item.item === Items.TOTEM_OF_UNDYING }
        .mapToInt { item: ItemStack -> item.count }
        .sum()
}

fun ClientPlayerEntity.getMovementYaw(): Double {
    var strafe = 90f
    strafe += if (input.movementForward != 0F) input.movementForward * 0.5F else 1F
    var yaw = yaw - strafe
    yaw -= if (input.movementForward < 0F) 180 else 0
    return Math.toRadians(yaw.toDouble())
}

fun ClientPlayerEntity.getSqrtSpeed(): Double {
    return sqrt(velocity.x.pow(2) + velocity.z.pow(2))
}

fun ClientPlayerEntity.setSpeed(speed: Double) {
    setVelocity(-sin(getMovementYaw()) * speed, velocity.y, cos(getMovementYaw()) * speed)
}

fun ClientPlayerEntity.lookAt(block: Vec3d, packet: Boolean) {
    var x = x - block.getX()
    var y = y - block.getY()
    var z = z - block.getZ()
    val s = sqrt(x * x + y * y + z * z)

    x /= s
    y /= s
    z /= s

    var pitch = asin(y)
    var yaw = atan2(z, x)

    pitch = pitch * 180.0 / Math.PI
    yaw = yaw * 180.0 / Math.PI
    yaw += 90.0

    when {
        packet -> lookPacket(yaw.toFloat(), pitch.toFloat())
        else -> lookClient(yaw.toFloat(), pitch.toFloat())
    }
}

fun ClientPlayerEntity.lookPacket(yaw: Float, pitch: Float) {
    networkHandler.sendPacket(
        PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, isOnGround)
    )
}

fun ClientPlayerEntity.lookClient(yaw: Float, pitch: Float) {
    this.yaw = yaw
    this.pitch = pitch
}

val PlayerEntity.eyePos
    get() = this.pos.add(
        0.0,
        this.getEyeHeight(this.pose).toDouble(),
        0.0
    )

private fun canReach(vec: Vec3d, aabb: Box, range: Double): Boolean {
    return aabb.expand(range).contains(vec)
}

fun PlayerEntity.canReach(aabb: Box, range: Double): Boolean {
    return canReach(
        this.eyePos,
        aabb,
        range
    )
}

fun PlayerEntity.canReach(entity: Entity, range: Double): Boolean {
    return canReach(
        this.eyePos,
        entity.boundingBox,
        range
    )
}
