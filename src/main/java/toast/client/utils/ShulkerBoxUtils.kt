package toast.client.utils

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.ShulkerBoxBlock
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.ListTag
import java.util.*

object ShulkerBoxUtils {
    private val shulkerList = listOf(
        Blocks.SHULKER_BOX,
        Blocks.WHITE_SHULKER_BOX,
        Blocks.ORANGE_SHULKER_BOX,
        Blocks.MAGENTA_SHULKER_BOX,
        Blocks.LIGHT_BLUE_SHULKER_BOX,
        Blocks.YELLOW_SHULKER_BOX,
        Blocks.LIME_SHULKER_BOX,
        Blocks.PINK_SHULKER_BOX,
        Blocks.GRAY_SHULKER_BOX,
        Blocks.LIGHT_GRAY_SHULKER_BOX,
        Blocks.CYAN_SHULKER_BOX,
        Blocks.PURPLE_SHULKER_BOX,
        Blocks.BLUE_SHULKER_BOX,
        Blocks.BROWN_SHULKER_BOX,
        Blocks.GREEN_SHULKER_BOX,
        Blocks.RED_SHULKER_BOX,
        Blocks.BLACK_SHULKER_BOX
    )

    fun isShulkerBox(item: Item): Boolean {
        return item is BlockItem && isShulkerBox(item.block)
    }

    fun isShulkerBox(block: Block): Boolean {
        return shulkerList.contains(block)
    }

    @JvmStatic
    fun isShulkerBox(item: ItemStack): Boolean {
        return (item.item as BlockItem).block is ShulkerBoxBlock
    }

    @JvmStatic
    fun getItemsInShulker(item: ItemStack): List<ItemStack> {
        val items: MutableList<ItemStack> =
            ArrayList(Collections.nCopies(27, ItemStack(Items.AIR)))
        val nbt = item.tag
        if (nbt != null && nbt.contains("BlockEntityTag")) {
            val nbtt = nbt.getCompound("BlockEntityTag")
            if (nbtt.contains("Items")) {
                val nbtList = nbtt["Items"] as ListTag?
                for (i in nbtList!!.indices) {
                    items[nbtList.getCompound(i).getByte("Slot").toInt()] = ItemStack.fromTag(nbtList.getCompound(i))
                }
            }
        }
        return items
    }
}