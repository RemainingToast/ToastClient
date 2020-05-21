package toast.client.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class WorldInteractionUtil {
    public static final List<Block> AIR = Arrays.asList(Blocks.AIR, Blocks.CAVE_AIR);

    public static final List<Block> REPLACEABLE = Arrays.asList(Blocks.AIR, Blocks.CAVE_AIR, Blocks.LAVA, Blocks.WATER,
            Blocks.GRASS, Blocks.TALL_GRASS, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.FERN, Blocks.DEAD_BUSH,
            Blocks.VINE, Blocks.FIRE, Blocks.STRUCTURE_VOID);
                        //what i mean with special items are like if you rightclick a cauldron with a waterbottle it fills it
    public static final List<Block> RIGHTCLICKABLE_NOSPECIALITEM = Arrays.asList(Blocks.DISPENSER, Blocks.NOTE_BLOCK, Blocks.WHITE_BED,
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
       return REPLACEABLE.contains(b);
   }

   public static boolean isRightClickable(Block b) {
       return RIGHTCLICKABLE_NOSPECIALITEM.contains(b);
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

   public static boolean placeBlock(BlockPos pos, Hand hand, boolean doRotations) {
       MinecraftClient mc = MinecraftClient.getInstance();
       if (mc.world == null || mc.player == null) return false;
       ClientPlayerEntity player = mc.player;
       for (Direction direction : Direction.values()) {
           BlockPos offsetPos = pos.offset(direction);
           Block offsetBlock = mc.world.getBlockState(offsetPos).getBlock();
           if (!isReplaceable(offsetBlock)) {
               if (doRotations) {
                   double dx = (offsetPos.getX() + 0.5) - player.getX();
                   double dy = (offsetPos.getY() + 0.5) - (player.getY() + player.getStandingEyeHeight());
                   double dz = (offsetPos.getZ() + 0.5) - player.getZ();
                   double dh = Math.sqrt(dx * dx + dz * dz);
                   float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
                   float pitch = (float) -Math.toDegrees(Math.atan2(dy, dh));
                   player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(yaw, pitch, player.onGround));
               }
               if (isRightClickable(offsetBlock)) {
                   player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
               }
               if (player.getStackInHand(hand).getItem() != null && player.getStackInHand(hand).getItem() != Items.AIR) {
                   mc.interactionManager.interactBlock(player, mc.world, hand,
                           new BlockHitResult(new Vec3d(pos), direction.getOpposite(), offsetPos, false));
                   player.swingHand(hand);
               } else {
                   return false;
               }
               if (isRightClickable(offsetBlock)) {
                   player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
               }
               return true;
           }
       }

       return false;
   }
}
