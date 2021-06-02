package dev.toastmc.toastclient.api.util

import com.mojang.authlib.GameProfile
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.network.OtherClientPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

fun ClientPlayerEntity.clone(): OtherClientPlayerEntity {
    return clone(gameProfile)
}

fun ClientPlayerEntity.clone(gameProfile: GameProfile): OtherClientPlayerEntity {
    val clone = OtherClientPlayerEntity(clientWorld, gameProfile)
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
