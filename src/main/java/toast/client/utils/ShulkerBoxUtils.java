package toast.client.utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShulkerBoxUtils {

    private static final List<Block> shulkerList = Arrays.asList(
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
    );

    public static boolean isShulkerBox(Item item) {
        return item instanceof BlockItem && isShulkerBox(((BlockItem) item).getBlock());
    }

    public static boolean isShulkerBox(Block block) {
        return shulkerList.contains(block);
    }

    public static boolean isShulkerBox(ItemStack item) {
        return ((BlockItem) item.getItem()).getBlock() instanceof ShulkerBoxBlock;
    }


    public static List<ItemStack> getItemsInShulker(ItemStack item) {
        List<ItemStack> items = new ArrayList<>(Collections.nCopies(27, new ItemStack(Items.AIR)));
        CompoundTag nbt = item.getTag();

        if (nbt != null && nbt.contains("BlockEntityTag")) {
            CompoundTag nbtt = nbt.getCompound("BlockEntityTag");
            if (nbtt.contains("Items")) {
                ListTag nbtList = (ListTag) nbtt.get("Items");
                for (int i = 0; i < nbtList.size(); i++) {
                    items.set(nbtList.getCompound(i).getByte("Slot"), ItemStack.fromTag(nbtList.getCompound(i)));
                }
            }
        }
        return items;
    }
}
