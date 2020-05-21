package toast.client.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

@Environment(EnvType.CLIENT)
public class WorldUtil {
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

    public LinkedHashMap<BlockPos, Block> getTileEntitiesInChunk(World world, int chunkX, int chunkZ) {
        LinkedHashMap<BlockPos, Block> map = new LinkedHashMap<>();
        Chunk chunk = world.getChunk(chunkX, chunkZ);
        if (!world.isChunkLoaded(new BlockPos(chunkX * 16d, 80d, chunkZ * 16d))) { return map; }
        chunk.getBlockEntityPositions().forEach(tilePos -> {
            map.put(tilePos, world.getBlockState(tilePos).getBlock());
        });
        return map;
    }
}
