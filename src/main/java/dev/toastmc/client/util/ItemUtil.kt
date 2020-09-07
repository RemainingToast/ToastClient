package dev.toastmc.client.util

import net.minecraft.item.Item
import net.minecraft.item.Items

object ItemUtil {
    private val pickaxeList = listOf(
            Items.WOODEN_PICKAXE,
            Items.STONE_PICKAXE,
            Items.IRON_PICKAXE,
            Items.GOLDEN_PICKAXE,
            Items.DIAMOND_PICKAXE,
            Items.NETHERITE_PICKAXE
    )

    fun isPickaxe(item: Item): Boolean {
        return pickaxeList.contains(item)
    }

}