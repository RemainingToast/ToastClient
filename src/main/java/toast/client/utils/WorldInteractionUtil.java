package toast.client.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.Arrays;
import java.util.List;

public class WorldInteractionUtil {
    public static List<Block> REPLACEABLE = Arrays.asList(Blocks.AIR, Blocks.CAVE_AIR, Blocks.LAVA, Blocks.WATER,
            Blocks.GRASS, Blocks.TALL_GRASS, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.FERN, Blocks.DEAD_BUSH,
            Blocks.VINE, Blocks.FIRE, Blocks.STRUCTURE_VOID);
                        //what i mean with special items are like if you rightclick a cauldron with a waterbottle it fills it
    public static List<Block> RIGHTCLICKABLE_NOSPECIALITEM = Arrays.asList(Blocks.WATER, Blocks.LAVA, Blocks.DISPENSER, Blocks.NOTE_BLOCK, Blocks.WHITE_BED,
            Blocks.ORANGE_BED, Blocks.MAGENTA_BED, Blocks.LIGHT_BLUE_BED, Blocks.YELLOW_BED, Blocks.LIME_BED, Blocks.PINK_BED, Blocks.GRAY_BED, Blocks.LIGHT_GRAY_BED,
            Blocks.CYAN_BED, Blocks.PURPLE_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.GREEN_BED, Blocks.RED_BED, Blocks.BLACK_BED, Blocks.CHEST,
            Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.LEVER, Blocks.STONE_BUTTON, Blocks.CAKE, Blocks.REPEATER, Blocks.OAK_TRAPDOOR, Blocks.SPRUCE_TRAPDOOR,
            Blocks.BIRCH_TRAPDOOR, Blocks.JUNGLE_TRAPDOOR, Blocks.ACACIA_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.OAK_FENCE_GATE, Blocks.BREWING_STAND,
            Blocks.ENDER_CHEST, Blocks.COMMAND_BLOCK, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON,
            Blocks.ACACIA_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.TRAPPED_CHEST, Blocks.COMPARATOR,
            Blocks.DAYLIGHT_DETECTOR, Blocks.HOPPER, Blocks.DROPPER, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE,
            Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR,
            Blocks.REPEATING_COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX,
            Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX,
            Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX, Blocks.LOOM, Blocks.BARREL, Blocks.SMOKER, Blocks.BLAST_FURNACE, Blocks.GRINDSTONE, Blocks.LECTERN,
            Blocks.STONECUTTER, Blocks.BELL, Blocks.SWEET_BERRY_BUSH, Blocks.STRUCTURE_BLOCK, Blocks.JIGSAW);

   public static boolean isReplaceable(Block b) {
       boolean replaceable = false;
       for (Block block : REPLACEABLE) {
           if (b == block) {
               replaceable = true;
               break;
           }
       }
       return replaceable;
   }

   public static boolean isRightClickable(Block b) {
       boolean rightclickable = false;
       for (Block block : REPLACEABLE) {
           if (b == block) {
               rightclickable = true;
               break;
           }
       }
       return rightclickable;
   }

   public static boolean isFluid(Block b) {
       return b == Blocks.WATER || b == Blocks.LAVA;
   }

   public static boolean isRightClickable(BlockState b) {
       return isRightClickable(b.getBlock());
   }

   public static boolean isReplaceable(BlockState b) {
       return isReplaceable(b.getBlock());
   }

}
