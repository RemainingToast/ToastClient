package dev.toastmc.toastclient.api.util

import com.mojang.authlib.GameProfile
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.OtherClientPlayerEntity

fun ClientPlayerEntity.clone(): OtherClientPlayerEntity {
    return clone(gameProfile)
}

fun ClientPlayerEntity.clone(gameProfile: GameProfile): OtherClientPlayerEntity {
    val clone = OtherClientPlayerEntity(clientWorld, gameProfile)
    clone.copyPositionAndRotation(this)
    clone.setHeadYaw(headYaw)
    return clone
}
