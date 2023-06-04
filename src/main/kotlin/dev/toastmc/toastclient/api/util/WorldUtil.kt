package dev.toastmc.toastclient.api.util

import dev.toastmc.toastclient.ToastClient
import dev.toastmc.toastclient.api.events.ChunkEvent
import io.netty.util.internal.ConcurrentSet
import net.minecraft.block.*
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.DefaultedRegistry
import net.minecraft.util.registry.Registry
import net.minecraft.world.Heightmap
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import org.quantumclient.energy.Subscribe
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.reflect.jvm.javaMethod

object WorldUtil {

    var loadedChunks: ConcurrentSet<Chunk> = ConcurrentSet()

    /**
     * Get all tile entities inside a given Chunk and collect them into a map of BlockPos and Block
     *
     * @param world  World of the chunk
     * @param chunkX Chunk-level X value (normal X / 16.0D)
     * @param chunkZ Chunk-level Z value (normal Z / 16.0D)
     * @return Tile entities inside the Chunk collected as a map of BlockPos and Block
     * @see Chunk.getBlockEntityPositions
     */
    fun getTileEntitiesInChunk(
        world: World,
        chunkX: Int,
        chunkZ: Int
    ): LinkedHashMap<BlockPos, Block> {
        val map = LinkedHashMap<BlockPos, Block>()
        if (!world.isChunkLoaded(
                BlockPos(
                    (chunkX shr 4).toDouble(),
                    80.0,
                    (chunkX shr 4).toDouble()
                )
            )
        ) {
            return map
        }

        val chunk: Chunk = world.getChunk(chunkX, chunkZ)
        chunk.blockEntityPositions.forEach(Consumer { tilePos: BlockPos ->
            map[tilePos] = world.getBlockState(tilePos).block
        })
        return map
    }

    /**
     * Get all tile entities inside a given world and collect them into a map of BlockPos and Block
     *
     * @param world World of the chunk
     * @return Tile entities inside the world which are loaded by the player collected as a map of BlockPos and Block
     * @see Chunk.getBlockEntityPositions
     */
    /*fun World.getTileEntitiesInWorld(): LinkedHashMap<BlockPos, Block> {
        val map =
            LinkedHashMap<BlockPos, Block>()
        this.blockEntities.forEach(Consumer { tilePos: BlockEntity ->
            val pos = tilePos.pos
            map[pos] = this.getBlockState(pos).block
        })
        return map
    }*/

    /**
     * Gets distance between two vectors
     *
     * @param vecA First Vector
     * @param vecB Second Vector
     * @return the distance between two vectors
     */
    fun getDistance(vecA: Vec3d, vecB: Vec3d): Double {
        return sqrt(
            (vecA.x - vecB.x).pow(2.0) + (vecA.y - vecB.y).pow(2.0) + (vecA.z - vecB.z).pow(2.0)
        )
    }

    /**
     * Gets vectors between two given vectors (startVec and destinationVec) every (distance between the given vectors) / steps
     *
     * @param startVec       Beginning vector
     * @param destinationVec Ending vector
     * @param steps          distance between given vectors
     * @return all vectors between startVec and destinationVec divided by steps
     */
    fun Vec3d.extendVec(
        destinationVec: Vec3d,
        steps: Int
    ): ArrayList<Vec3d> {
        val returnList =
            ArrayList<Vec3d>(steps + 1)
        val stepDistance = getDistance(this, destinationVec) / steps
        for (i in 0 until steps.coerceAtLeast(1) + 1) {
            returnList.add(this.advanceVec(destinationVec, stepDistance * i))
        }
        return returnList
    }

    /**
     * Moves a vector towards a destination based on distance
     *
     * @param startVec       Starting vector
     * @param destinationVec returned vector
     * @param distance       distance to move startVec by
     * @return vector based on startVec that is moved towards destinationVec by distance
     */
    fun Vec3d.advanceVec(
        destinationVec: Vec3d,
        distance: Double
    ): Vec3d {
        val advanceDirection = destinationVec.subtract(this).normalize()
        return if (destinationVec.distanceTo(this) < distance) destinationVec else advanceDirection.multiply(
            distance
        )
    }

    /**
     * Get all rounded block positions inside a 3-dimensional area between pos1 and pos2.
     *
     * @param end Ending vector
     * @return rounded block positions inside a 3d area between pos1 and pos2
     */
    fun Vec3d.getBlockPositionsInArea(
        end: Vec3d
    ): List<BlockPos> {
        val minX: Int = this.x.roundToInt()
        val maxX: Int = end.x.roundToInt()
        val minY: Int = this.y.roundToInt()
        val maxY: Int = end.y.roundToInt()
        val minZ: Int = this.z.roundToInt()
        val maxZ: Int = end.z.roundToInt()
        return BlockPos.stream(minX, minY, minZ, maxX, maxY, maxZ).toList()
    }

    /**
     * Get all block positions inside a 3d area between pos1 and pos2
     *
     * @param end Ending blockPos
     * @return block positions inside a 3d area between pos1 and pos2
     */
    fun BlockPos.getBlockPositionsInArea(end: BlockPos): List<BlockPos> {
        val minX: Int = this.x
        val maxX: Int = end.x
        val minY: Int = this.y
        val maxY: Int = end.y
        val minZ: Int = this.z
        val maxZ: Int = end.z
        return BlockPos.stream(minX, minY, minZ, maxX, maxY, maxZ).toList()
    }

    /**
     * Get all positions of block matches below the surface of an area
     *
     * @param startX
     * @param startZ
     * @param endX
     * @param endZ
     * @return block positions inside a 3d area between pos1 and pos2
     */
    fun getBlockMatchesBelowSurface(
        match: Block,
        startX: Int,
        startZ: Int,
        endX: Int,
        endZ: Int
    ): List<BlockPos> {
        val returnList = mutableListOf<BlockPos>()
        for (x in startX..endX) {
            for (z in startZ..endZ) {
                for (y in 0..getHighestYAtXZ(x, z)) {
                    if (mc.world!!.getBlockState(BlockPos(x, y, z)).block == match) returnList.add(
                        BlockPos(x, y, z)
                    )
                }
            }
        }
        return returnList
    }

    /**
     * Gets the Y coordinate of the highest non-air block in the column of x, z
     *
     * @param x X coordinate of the column
     * @param z Z coordinate of the column
     * @return Y coordinate of the highest non-air block in the column
     */
    fun getHighestYAtXZ(x: Int, z: Int): Int {
        return mc.world!!.getChunk(BlockPos(x, 0, z))
            .sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x, z)
    }

    val BlockPos.block: Block
        get() = mc.world!!.getBlockState(this).block

    val BlockPos.vec: Vec3d
        get() = Vec3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())

    val Vec3d.blockPos: BlockPos
        get() = BlockPos(this.x.roundToInt(), this.y.roundToInt(), this.z.roundToInt())

    val BlockPos.centeredVec3d: Vec3d
        get() = Vec3d(this.x.toDouble() + 0.5, this.y.toDouble(), this.z.toDouble() + 0.5)

    fun Block.matches(vararg blocks: Block): Boolean {
        return blocks.contains(this)
    }

    @Subscribe
    fun on(event: ChunkEvent) {
        if (mc.world!!.isChunkLoaded(event.chunk!!.pos.startX, event.chunk.pos.startZ))
            loadedChunks.add(event.chunk)
        else
            loadedChunks.remove(event.chunk)
    }

    init {
        ToastClient.eventBus.register(this)
    }

    fun getSphere(
        loc: BlockPos,
        r: Int,
        h: Int,
        hollow: Boolean,
        sphere: Boolean,
        plus_y: Int
    ): List<BlockPos> {
        val circleblocks: ArrayList<BlockPos> = ArrayList()
        val cx = loc.x
        val cy = loc.y
        val cz = loc.z
        var x = cx - r
        while (x.toFloat() <= cx.toFloat() + r) {
            var z = cz - r
            while (z.toFloat() <= cz.toFloat() + r) {
                var y = if (sphere) cy - r else cy
                do {
                    val f = if (sphere) cy.toFloat() + r else (cy + h).toFloat()
                    if (y >= f) break
                    val dist =
                        (cx - x) * (cx - x) + (cz - z) * (cz - z) + (if (sphere) (cy - y) * (cy - y) else 0).toDouble()
                    if (!(dist >= (r * r).toDouble() || hollow && dist < ((r - 1.0f) * (r - 1.0f)).toDouble())) {
                        val l = BlockPos(x, y + plus_y, z)
                        circleblocks.add(l)
                    }
                    ++y
                } while (true)
                ++z
            }
            ++x
        }
        return circleblocks
    }

    fun sphere(localPos: BlockPos, range: Int): List<BlockPos> {
        val blocks: MutableList<BlockPos> = ArrayList()
        val x = localPos.x
        val y = localPos.y
        val z = localPos.z
        for (i in -range..range) {
            for (j in -range..range) {
                for (k in -range..range) {
                    val distance: Double =
                        ((x + x - i) * (x + x - i) + (y + y - j) * (y + y - j) + (z + z - k) * (z + z - k)).toDouble()
                    if (distance < range * range) {
                        val block = BlockPos(x, y, z)
                        blocks.add(block)
                    }
                }
            }
        }
        return blocks
    }

    val BEDS = listOf(
        Blocks.BLACK_BED,
        Blocks.BLUE_BED,
        Blocks.BROWN_BED,
        Blocks.CYAN_BED,
        Blocks.GRAY_BED,
        Blocks.GREEN_BED,
        Blocks.LIGHT_BLUE_BED,
        Blocks.LIGHT_GRAY_BED,
        Blocks.LIME_BED,
        Blocks.MAGENTA_BED,
        Blocks.ORANGE_BED,
        Blocks.PINK_BED,
        Blocks.PURPLE_BED,
        Blocks.RED_BED,
        Blocks.WHITE_BED,
        Blocks.YELLOW_BED
    )
    val AIR = listOf(Blocks.AIR, Blocks.CAVE_AIR)
    val REPLACEABLE = listOf(
        Blocks.AIR,
        Blocks.CAVE_AIR,
        Blocks.LAVA,
        Blocks.WATER,
        Blocks.GRASS,
        Blocks.TALL_GRASS,
        Blocks.SEAGRASS,
        Blocks.TALL_SEAGRASS,
        Blocks.FERN,
        Blocks.DEAD_BUSH,
        Blocks.VINE,
        Blocks.FIRE,
        Blocks.STRUCTURE_VOID
    )

    val BlockPos.isAir: Boolean
        get() = mc.world?.isAir(this) ?: false

    fun openBlock(pos: BlockPos) {
        val direction: Array<Direction> = Direction.values()
        for (f in direction) {
            val neighborBlock = mc.world!!.getBlockState(pos.offset(f)).block
            val vecPos = Vec3d(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
            if (REPLACEABLE.contains(neighborBlock)) {
                mc.interactionManager!!.interactBlock(
                    mc.player,
                    Hand.MAIN_HAND,
                    BlockHitResult(vecPos, f.opposite, pos, false)
                )
                return
            }
        }
    }

    //what i mean with special items are like if you rightclick a cauldron with a waterbottle it fills it
    private val NONSPECIAL_INTERACTIVE: MutableList<Block> = listOf(
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
        return INTERACTIVE.contains(b)
    }

    fun isFluid(pos: BlockPos): Boolean {
        val fluids: List<Material> =
            Arrays.asList(Material.WATER, Material.LAVA, Material.UNDERWATER_PLANT)
        return fluids.contains(MinecraftClient.getInstance().world!!.getBlockState(pos).material)
    }

    fun isRightClickable(b: BlockState): Boolean {
        return isRightClickable(b.block)
    }

    fun isReplaceable(b: BlockState): Boolean {
        return isReplaceable(b.block)
    }

    fun placeBlock(pos: BlockPos, hand: Hand, doRotations: Boolean): Boolean {
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
                    player.networkHandler.sendPacket(
                        PlayerMoveC2SPacket.LookAndOnGround(
                            yaw,
                            pitch,
                            player.isOnGround
                        )
                    )
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
                        player, hand,
                        BlockHitResult(
                            Vec3d(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()),
                            direction.opposite,
                            offsetPos,
                            false
                        )
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


    val surroundOffsets = listOf(
        BlockPos(1, 0, 0),
        BlockPos(-1, 0, 0),
        BlockPos(0, 0, 1),
        BlockPos(0, 0, -1),
    )

    fun BlockPos.isSurrounded(
        vararg acceptableBlocks: Block = arrayOf(
            Blocks.OBSIDIAN,
            Blocks.BEDROCK
        )
    ): Boolean {
        for (offset in surroundOffsets) {
            if (!acceptableBlocks.contains(this.add(offset).block)) {
                return false
            }
        }
        return true
    }

    fun BlockPos.isHole(
        airOnly: Boolean = false,
        vararg acceptableBlocks: Block = arrayOf(Blocks.OBSIDIAN, Blocks.BEDROCK)
    ): Boolean {
        if (airOnly && !AIR.contains(this.block)) {
            return false
        }

        return acceptableBlocks.contains(
            this.add(
                0,
                -1,
                0
            ).block
        ) && this.isSurrounded(*acceptableBlocks)
    }

    val BlockPos.isCrystalSpot: Boolean
        get() = (this.block == Blocks.BEDROCK || this.block == Blocks.OBSIDIAN)
                && this.up().isAir
                && mc.world!!.getOtherEntities(
            null,
            Box(
                this.vec.x,
                this.vec.y + 1.0,
                this.vec.z,
                this.vec.x + 1.0,
                this.vec.y + 3.0,
                this.vec.z + 1.0
            )
        ).isEmpty()

    private val onUseMethod = AbstractBlock::onUse::javaMethod.get()!!

    val ALL_BLOCKS: DefaultedRegistry<Block> = Registry.BLOCK
    val INTERACTIVE: List<Block> = ALL_BLOCKS.stream().filter {
        for (method in it::class.java.declaredMethods) {
            if (method.name == onUseMethod.name) {
                return@filter true
            }
        }
        return@filter false
    }.collect(Collectors.toList())

    fun World.crystalMultiplier(): Float {
        return if (difficulty.id == 0) 0.0f else if (difficulty.id == 2) 1.0f else if (difficulty.id == 1) 0.5f else 1.5f
    }

}