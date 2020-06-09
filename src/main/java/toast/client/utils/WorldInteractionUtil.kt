package toast.client.utils

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookOnly
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import java.util.*
import kotlin.math.atan2
import kotlin.math.sqrt

object WorldInteractionUtil {
    val AIR = Arrays.asList(Blocks.AIR, Blocks.CAVE_AIR)
    val REPLACEABLE = Arrays.asList(
        Blocks.AIR, Blocks.CAVE_AIR, Blocks.LAVA, Blocks.WATER,
        Blocks.GRASS, Blocks.TALL_GRASS, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.FERN, Blocks.DEAD_BUSH,
        Blocks.VINE, Blocks.FIRE, Blocks.STRUCTURE_VOID
    )

    //what i mean with special items are like if you rightclick a cauldron with a waterbottle it fills it
    private val RIGHTCLICKABLE_NOSPECIALITEM: MutableList<Block> = listOf(
        Blocks.DISPENSER,
        Blocks.NOTE_BLOCK,
        Blocks.WHITE_BED,
        Blocks.ORANGE_BED,
        Blocks.MAGENTA_BED,
        Blocks.LIGHT_BLUE_BED,
        Blocks.YELLOW_BED,
        Blocks.LIME_BED,
        Blocks.PINK_BED,
        Blocks.GRAY_BED,
        Blocks.LIGHT_GRAY_BED,
        Blocks.CYAN_BED,
        Blocks.PURPLE_BED,
        Blocks.BLUE_BED,
        Blocks.BROWN_BED,
        Blocks.GREEN_BED,
        Blocks.RED_BED,
        Blocks.BLACK_BED,
        Blocks.CHEST,
        Blocks.FURNACE,
        Blocks.OAK_DOOR,
        Blocks.LEVER,
        Blocks.STONE_BUTTON,
        Blocks.CAKE,
        Blocks.REPEATER,
        Blocks.OAK_TRAPDOOR,
        Blocks.SPRUCE_TRAPDOOR,
        Blocks.BIRCH_TRAPDOOR,
        Blocks.JUNGLE_TRAPDOOR,
        Blocks.ACACIA_TRAPDOOR,
        Blocks.DARK_OAK_TRAPDOOR,
        Blocks.OAK_FENCE_GATE,
        Blocks.BREWING_STAND,
        Blocks.ENDER_CHEST,
        Blocks.COMMAND_BLOCK,
        Blocks.OAK_BUTTON,
        Blocks.SPRUCE_BUTTON,
        Blocks.BIRCH_BUTTON,
        Blocks.JUNGLE_BUTTON,
        Blocks.ACACIA_BUTTON,
        Blocks.DARK_OAK_BUTTON,
        Blocks.ANVIL,
        Blocks.CHIPPED_ANVIL,
        Blocks.DAMAGED_ANVIL,
        Blocks.TRAPPED_CHEST,
        Blocks.COMPARATOR,
        Blocks.DAYLIGHT_DETECTOR,
        Blocks.HOPPER,
        Blocks.DROPPER,
        Blocks.SPRUCE_FENCE_GATE,
        Blocks.BIRCH_FENCE_GATE,
        Blocks.JUNGLE_FENCE_GATE,
        Blocks.ACACIA_FENCE_GATE,
        Blocks.DARK_OAK_FENCE_GATE,
        Blocks.SPRUCE_DOOR,
        Blocks.BIRCH_DOOR,
        Blocks.JUNGLE_DOOR,
        Blocks.ACACIA_DOOR,
        Blocks.DARK_OAK_DOOR,
        Blocks.REPEATING_COMMAND_BLOCK,
        Blocks.CHAIN_COMMAND_BLOCK,
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
        Blocks.BLACK_SHULKER_BOX,
        Blocks.LOOM,
        Blocks.BARREL,
        Blocks.SMOKER,
        Blocks.BLAST_FURNACE,
        Blocks.GRINDSTONE,
        Blocks.LECTERN,
        Blocks.STONECUTTER,
        Blocks.BELL,
        Blocks.SWEET_BERRY_BUSH,
        Blocks.STRUCTURE_BLOCK,
        Blocks.JIGSAW
    ) as MutableList<Block>

    fun isReplaceable(b: Block): Boolean {
        return REPLACEABLE.contains(b)
    }

    fun isRightClickable(b: Block): Boolean {
        return RIGHTCLICKABLE_NOSPECIALITEM.contains(b)
    }

    fun isFluid(b: Block): Boolean {
        return b === Blocks.WATER || b === Blocks.LAVA
    }

    fun isRightClickable(b: BlockState): Boolean {
        return isRightClickable(b.block)
    }

    fun isReplaceable(b: BlockState): Boolean {
        return isReplaceable(b.block)
    }

    fun placeBlock(pos: BlockPos, hand: Hand?, doRotations: Boolean): Boolean {
        val mc = MinecraftClient.getInstance()
        if (mc.world == null || mc.player == null) return false
        val player = mc.player
        for (direction in Direction.values()) {
            val offsetPos = pos.offset(direction)
            val offsetBlock = mc.world!!.getBlockState(offsetPos).block
            if (!isReplaceable(offsetBlock)) {
                if (doRotations) {
                    val dx = offsetPos.x + 0.5 - player!!.x
                    val dy = offsetPos.y + 0.5 - (player.y + player.standingEyeHeight)
                    val dz = offsetPos.z + 0.5 - player.z
                    val dh = sqrt(dx * dx + dz * dz)
                    val yaw = Math.toDegrees(atan2(dz, dx)).toFloat() - 90
                    val pitch = (-Math.toDegrees(atan2(dy, dh))).toFloat()
                    player.networkHandler.sendPacket(LookOnly(yaw, pitch, player.onGround))
                }
                if (isRightClickable(offsetBlock)) {
                    player!!.networkHandler.sendPacket(
                        ClientCommandC2SPacket(
                            player,
                            ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY
                        )
                    )
                }
                if (player!!.getStackInHand(hand).item != null && player.getStackInHand(hand)
                        .item !== Items.AIR
                ) {
                    mc.interactionManager!!.interactBlock(
                        player, mc.world, hand,
                        BlockHitResult(Vec3d(pos), direction.opposite, offsetPos, false)
                    )
                    player.swingHand(hand)
                } else {
                    return false
                }
                if (isRightClickable(offsetBlock)) {
                    player.networkHandler.sendPacket(
                        ClientCommandC2SPacket(
                            mc.player,
                            ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY
                        )
                    )
                }
                return true
            }
        }
        return false
    }
}