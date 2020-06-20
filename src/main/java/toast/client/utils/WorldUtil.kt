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
        if (!world.isChunkLoaded(BlockPos((chunkX shr 4).toDouble(), 80.0, (chunkX shr 4).toDouble()))) {
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
    fun World.getTileEntitiesInWorld(): LinkedHashMap<BlockPos, Block> {
        val map =
                LinkedHashMap<BlockPos, Block>()
        this.blockEntities.forEach(Consumer { tilePos: BlockEntity ->
            val pos = tilePos.pos
            map[pos] = this.getBlockState(pos).block
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
        val minX: Int = this.x.coerceAtMost(end.x).roundToInt()
        val maxX: Int = this.x.coerceAtLeast(end.x).roundToInt()
        val minY: Int = this.y.coerceAtMost(end.y).roundToInt()
        val maxY: Int = this.y.coerceAtLeast(end.y).roundToInt()
        val minZ: Int = this.z.coerceAtMost(end.z).roundToInt()
        val maxZ: Int = this.z.coerceAtLeast(end.z).roundToInt()
        return BlockPos.stream(minX, minY, minZ, maxX, maxY, maxZ).collect(Collectors.toList())
    }

    /**
     * Get all block positions inside a 3d area between pos1 and pos2
     *
     * @param end Ending blockPos
     * @return block positions inside a 3d area between pos1 and pos2
     */
    fun BlockPos.getBlockPositionsInArea(end: BlockPos): List<BlockPos> {
        val minX: Int = this.x.coerceAtMost(end.x)
        val maxX: Int = this.x.coerceAtLeast(end.x)
        val minY: Int = this.y.coerceAtMost(end.y)
        val maxY: Int = this.y.coerceAtLeast(end.y)
        val minZ: Int = this.z.coerceAtMost(end.z)
        val maxZ: Int = this.z.coerceAtLeast(end.z)
        return BlockPos.stream(minX, minY, minZ, maxX, maxY, maxZ).collect(Collectors.toList())
    }
}