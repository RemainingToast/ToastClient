package toast.client.utils

import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Heightmap
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Some utilities for getting information about the world surrounding the player
 */
object WorldUtil {
    /**
     * Asynchronously get all matches of given Block(s) inside a given Chunk and collect them into a map of BlockPos and Block
     *
     * @param world   World of the chunk
     * @param chunkX  Chunk-level X value (normal X / 16.0D)
     * @param chunkZ  Chunk-level Z value (normal Z / 16.0D)
     * @param matches List of blocks to match against blocks inside the Box
     * @return Matches inside the Box collected as a map of BlockPos and Block
     */
    @Throws(InterruptedException::class)
    fun searchChunk(
            world: World,
            chunkX: Int,
            chunkZ: Int,
            matches: List<Block?>
    ): ConcurrentHashMap<BlockPos, Block> {
        val queue =
                ConcurrentHashMap<BlockPos, Block>()
        if (!world.isChunkLoaded(BlockPos(chunkX * 16.0, 80.0, chunkZ * 16.0))) {
            return queue
        }
        val latch = CountDownLatch(1)
        val chunk: Chunk = world.getChunk(chunkX, chunkZ)
        Thread(Runnable {
            for (x in chunkX * 16 until chunkX * 16 + 15) {
                for (z in chunkZ * 16 until chunkZ * 16 + 15) {
                    val block =
                            world.getBlockState(BlockPos(x, chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE)[x, z], z))
                                    .block
                    if (matches.contains(block)) {
                        queue[BlockPos(x, chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE)[x, z], z)] = block
                    }
                }
            }
            latch.countDown()
        }).start()
        latch.await()
        return queue
    }

    /**
     * Asynchronously get all matches of given Block(s) inside a given Box and collect them into a map of BlockPos and Block
     *
     * @param world   World of the Box
     * @param box     Box to search for matches within
     * @param matches List of blocks to match against blocks inside the Box
     * @return Matches inside the Box collected as a map of BlockPos and Block
     */
    @Throws(InterruptedException::class)
    fun searchBox(
            world: World,
            box: Box,
            matches: List<Block?>
    ): ConcurrentHashMap<BlockPos, Block> {
        val map =
                ConcurrentHashMap<BlockPos, Block>()
        // I'm trusting that the chunk is loaded
        val latch = CountDownLatch(1)
        Thread(Runnable {
            val x1 = MathHelper.floor(box.x1)
            val x2 = MathHelper.ceil(box.x2)
            val y1 = MathHelper.floor(box.y1)
            val y2 = MathHelper.ceil(box.y2)
            val z1 = MathHelper.floor(box.z1)
            val z2 = MathHelper.ceil(box.z2)
            BlockPos.stream(x1, y1, z1, x2 - 1, y2 - 1, z2 - 1)
                    .forEach { pos: BlockPos ->
                        val block = world.getBlockState(pos).block
                        if (matches.contains(block)) {
                            map[pos] = block
                        }
                    }
            latch.countDown()
        }).start()
        latch.await()
        return map
    }

    /**
     * Asynchronously get all matches of given Block(s) inside a given List and collect them into a map of BlockPos and Block
     *
     * @param world   World of the Box
     * @param toCheck List of block positions to check against matches
     * @param matches List of blocks to match against blocks inside the Box
     * @return Matches inside the List collected as a map of BlockPos and Block
     */
    @Throws(InterruptedException::class)
    fun searchList(
            world: World,
            toCheck: List<BlockPos>,
            matches: List<Block?>
    ): ConcurrentHashMap<BlockPos, Block> {
        val map =
                ConcurrentHashMap<BlockPos, Block>()
        // I'm trusting that the chunk is loaded
        val latch = CountDownLatch(1)
        Thread(Runnable {
            toCheck.forEach(Consumer { pos: BlockPos ->
                val block = world.getBlockState(pos).block
                if (matches.contains(block)) {
                    map[pos] = block
                }
            })
            latch.countDown()
        }).start()
        latch.await()
        return map
    }

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
        val map =
                LinkedHashMap<BlockPos, Block>()
        val chunk: Chunk = world.getChunk(chunkX, chunkZ)
        if (!world.isChunkLoaded(BlockPos(chunkX * 16.0, 80.0, chunkZ * 16.0))) {
            return map
        }
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
    fun getTileEntitiesInWorld(world: World): LinkedHashMap<BlockPos, Block> {
        val map =
                LinkedHashMap<BlockPos, Block>()
        world.blockEntities.forEach(Consumer { tilePos: BlockEntity ->
            val pos = tilePos.pos
            map[pos] = world.getBlockState(pos).block
        })
        return map
    }

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
    fun extendVec(
            startVec: Vec3d,
            destinationVec: Vec3d,
            steps: Int
    ): ArrayList<Vec3d> {
        val returnList =
                ArrayList<Vec3d>(steps + 1)
        val stepDistance = getDistance(startVec, destinationVec) / steps
        for (i in 0 until steps.coerceAtLeast(1) + 1) {
            returnList.add(advanceVec(startVec, destinationVec, stepDistance * i))
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
    fun advanceVec(
            startVec: Vec3d?,
            destinationVec: Vec3d,
            distance: Double
    ): Vec3d {
        val advanceDirection = destinationVec.subtract(startVec).normalize()
        return if (destinationVec.distanceTo(startVec) < distance) destinationVec else advanceDirection.multiply(
                distance
        )
    }

    /**
     * Get all rounded block positions inside a 3-dimensional area between pos1 and pos2.
     *
     * @param pos1 Starting vector
     * @param pos2 Ending vector
     * @return rounded block positions inside a 3d area between pos1 and pos2
     */
    fun getBlockPositionsInArea(
            pos1: Vec3d,
            pos2: Vec3d
    ): List<BlockPos> {
        val minX: Int = pos1.x.coerceAtMost(pos2.x).roundToInt()
        val maxX: Int = pos1.x.coerceAtLeast(pos2.x).roundToInt()
        val minY: Int = pos1.y.coerceAtMost(pos2.y).roundToInt()
        val maxY: Int = pos1.y.coerceAtLeast(pos2.y).roundToInt()
        val minZ: Int = pos1.z.coerceAtMost(pos2.z).roundToInt()
        val maxZ: Int = pos1.z.coerceAtLeast(pos2.z).roundToInt()
        return BlockPos.stream(minX, minY, minZ, maxX, maxY, maxZ).collect(Collectors.toList())
    }

    /**
     * Get all block positions inside a 3d area between pos1 and pos2
     *
     * @param pos1 Starting blockPos
     * @param pos2 Ending blockPos
     * @return block positions inside a 3d area between pos1 and pos2
     */
    fun getBlockPositionsInArea(pos1: BlockPos, pos2: BlockPos): List<BlockPos> {
        val minX: Int = pos1.x.coerceAtMost(pos2.x)
        val maxX: Int = pos1.x.coerceAtLeast(pos2.x)
        val minY: Int = pos1.y.coerceAtMost(pos2.y)
        val maxY: Int = pos1.y.coerceAtLeast(pos2.y)
        val minZ: Int = pos1.z.coerceAtMost(pos2.z)
        val maxZ: Int = pos1.z.coerceAtLeast(pos2.z)
        return BlockPos.stream(minX, minY, minZ, maxX, maxY, maxZ).collect(Collectors.toList())
    }
}