package toast.client.modules.render;

import net.minecraft.block.Blocks;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import org.lwjgl.glfw.GLFW;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;
import toast.client.utils.WorldUtil;

public class StorageESP extends Module {
    public StorageESP() {
        super("StorageESP", Category.RENDER, -1);
        this.addBool("Chests", true);
        this.addBool("Trapped Chests", true);
        this.addBool("Minecart Chests", true);
        this.addBool("Furnaces", true);
        this.addBool("Smokers", true);
        this.addBool("Blast Furnaces", true);
        this.addBool("Barrels", true);
        this.addBool("Dispensers", true);
        this.addBool("Droppers", true);
    }

    @EventImpl
    public void onUpdate(EventUpdate event) {
        if (mc.world == null) return;
        WorldUtil.getTileEntitiesInWorld(mc.world).forEach((pos, type) -> {
            if ((this.getBool("Chests") && type == Blocks.CHEST) ||
                    (this.getBool("Trapped Chests") && type == Blocks.TRAPPED_CHEST) ||
                    (this.getBool("Furnaces") && type == Blocks.FURNACE) ||
                    (this.getBool("Smokers") && type == Blocks.SMOKER) ||
                    (this.getBool("Blast Furnaces") && type == Blocks.BLAST_FURNACE) ||
                    (this.getBool("Barrels") && type == Blocks.BARREL) ||
                    (this.getBool("Dispensers") && type == Blocks.DISPENSER) ||
                    (this.getBool("Droppers") && type == Blocks.DROPPER)) {
                // Render stuff for block
            }
        });
        mc.world.getEntities().forEach(entity -> {
            if (this.getBool("Minecart Chests") && entity instanceof ChestMinecartEntity) {
                // Render stuff for entities
            }
        });
    }

}
