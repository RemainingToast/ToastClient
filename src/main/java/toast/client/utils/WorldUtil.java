package toast.client.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class WorldUtil {
    
    /**
     * Asynchronously get all matches of given Block(s) inside a given Chunk and collect them into a map of BlockPos and Block
     *
     * @param world World of the chunk
     * @param chunkX Chunk-level X value (normal X / 16.0D)
     * @param chunkZ Chunk-level Z value (normal Z / 16.0D)
     * @param matches List of blocks to match against blocks inside the Box
     * @return Matches inside the Box collected as a map of BlockPos and Block
     */
    public ConcurrentHashMap<BlockPos, Block> searchChunk(World world, int chunkX, int chunkZ, List<Block> matches) throws InterruptedException {
        ConcurrentHashMap<BlockPos, Block> queue = new ConcurrentHashMap<>();
        if (!world.isChunkLoaded(new BlockPos(chunkX * 16d, 80d, chunkZ * 16d))) { return queue; }
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            for (int x = chunkX * 16; x < chunkX * 16 + 15; x++) {
                for (int y = 0; y < 255; y++) {
                    for (int z = chunkZ * 16; z < chunkZ * 16 + 15; z++) {
                        Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                        if (matches.contains(block)) {
                            queue.put(new BlockPos(x, y, z), block);
                        }
                    }
                }
            }
            latch.countDown();
        }).start();
        latch.await();
        return queue;
    }

    /**
     * Asynchronously get all matches of given Block(s) inside a given Box and collect them into a map of BlockPos and Block
     *
     * @param world World of the Box
     * @param box Box to search for matches within
     * @param matches List of blocks to match against blocks inside the Box
     * @return Matches inside the Box collected as a map of BlockPos and Block
     */
    public ConcurrentHashMap<BlockPos, Block> searchBox(World world, Box box, List<Block> matches) throws InterruptedException {
        ConcurrentHashMap<BlockPos, Block> map = new ConcurrentHashMap<>();
        // I'm trusting that the chunk is loaded
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            int x1 = MathHelper.floor(box.x1);
            int x2 = MathHelper.ceil(box.x2);
            int y1 = MathHelper.floor(box.y1);
            int y2 = MathHelper.ceil(box.y2);
            int z1 = MathHelper.floor(box.z1);
            int z2 = MathHelper.ceil(box.z2);
            BlockPos.stream(x1, y1, z1, x2 - 1, y2 - 1, z2 - 1).forEach(pos -> {
                Block block = world.getBlockState(pos).getBlock();
                if (matches.contains(block)) {
                    map.put(pos, block);
                }
            });
            latch.countDown();
        }).start();
        latch.await();
        return map;
    }

    /**
     * Get all tile entities inside a given Chunk and collect them into a map of BlockPos and Block
     *
     * @see Chunk#getBlockEntityPositions()
     * @param world World of the chunk
     * @param chunkX Chunk-level X value (normal X / 16.0D)
     * @param chunkZ Chunk-level Z value (normal Z / 16.0D)
     * @return Tile entities inside the Chunk collected as a map of BlockPos and Block
     */
    public LinkedHashMap<BlockPos, Block> getTileEntitiesInChunk(World world, int chunkX, int chunkZ) {
        LinkedHashMap<BlockPos, Block> map = new LinkedHashMap<>();
        Chunk chunk = world.getChunk(chunkX, chunkZ);
        if (!world.isChunkLoaded(new BlockPos(chunkX * 16d, 80d, chunkZ * 16d))) { return map; }
        chunk.getBlockEntityPositions().forEach(tilePos -> {
            map.put(tilePos, world.getBlockState(tilePos).getBlock());
        });
        return map;
    }

    /**
     * Gets distance between two vectors
     *
     * @param vecA First Vector
     * @param vecB Second Vector
     * @return the distance between two vectors
     */
    public static double getDistance(Vec3d vecA, Vec3d vecB) {
        return Math.sqrt(Math.pow(vecA.x - vecB.x, 2) + Math.pow(vecA.y - vecB.y, 2) + Math.pow(vecA.z - vecB.z, 2));
    }

    /**
     * Gets vectors between two given vectors (startVec and destinationVec) every (distance between the given vectors) / steps
     *
     * @param startVec Beginning vector
     * @param destinationVec Ending vector
     * @param steps distance between given vectors
     * @return all vectors between startVec and destinationVec divided by steps
     */
    public static ArrayList<Vec3d> extendVec(Vec3d startVec, Vec3d destinationVec, int steps) {
        ArrayList<Vec3d> returnList = new ArrayList<>(steps + 1);
        double stepDistance = getDistance(startVec, destinationVec) / steps;

        for (int i = 0; i < Math.max(steps, 1) + 1; i++) {
            returnList.add(advanceVec(startVec, destinationVec, stepDistance * i));
        }

        return returnList;
    }

    // Returns

    /**
     * Moves a vector towards a destination based on distance
     *
     * @param startVec Starting vector
     * @param destinationVec returned vector
     * @param distance distance to move startVec by
     * @return vector based on startVec that is moved towards destinationVec by distance
     */
    public static Vec3d advanceVec(Vec3d startVec, Vec3d destinationVec, double distance) {
        Vec3d advanceDirection = destinationVec.subtract(startVec).normalize();
        if (destinationVec.distanceTo(startVec) < distance) return destinationVec;
        return advanceDirection.multiply(distance);
    }

    /**
     * Get all rounded block positions inside a 3-dimensional area between pos1 and pos2.
     *
     * @param pos1 Starting vector
     * @param pos2 Ending vector
     * @return rounded block positions inside a 3d area between pos1 and pos2
     */
    public static List<BlockPos> getBlockPositionsInArea(Vec3d pos1, Vec3d pos2) {
        int minX = (int) Math.round(Math.min(pos1.x, pos2.x));
        int maxX = (int) Math.round(Math.max(pos1.x, pos2.x));

        int minY = (int) Math.round(Math.min(pos1.y, pos2.y));
        int maxY = (int) Math.round(Math.max(pos1.y, pos2.y));

        int minZ = (int) Math.round(Math.min(pos1.z, pos2.z));
        int maxZ = (int) Math.round(Math.max(pos1.z, pos2.z));

        return BlockPos.stream(minX, minY, minZ, maxX, maxY, maxZ).collect(Collectors.toList());
    }

    /**
     * Get all block positions inside a 3d area between pos1 and pos2
     *
     * @param pos1 Starting blockPos
     * @param pos2 Ending blockPos
     * @return block positions inside a 3d area between pos1 and pos2
     */
    public static List<BlockPos> getBlockPositionsInArea(BlockPos pos1, BlockPos pos2) {
        int minX = Math.min(pos1.getX(), pos2.getX());
        int maxX = Math.max(pos1.getX(), pos2.getX());

        int minY = Math.min(pos1.getY(), pos2.getY());
        int maxY = Math.max(pos1.getY(), pos2.getY());

        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return BlockPos.stream(minX, minY, minZ, maxX, maxY, maxZ).collect(Collectors.toList());
    }
}
