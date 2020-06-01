package toast.client.modules.render;

import net.minecraft.block.Blocks;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;
import toast.client.utils.WorldUtil;

public class StorageESP extends Module {
    public StorageESP() {
        super("StorageESP", "Highlights storage blocks in the world.", Category.RENDER, -1);
        this.settings.addBoolean("Chests", true);
        this.settings.addBoolean("Trapped Chests", true);
        this.settings.addBoolean("Minecart Chests", true);
        this.settings.addBoolean("Furnaces", true);
        this.settings.addBoolean("Smokers", true);
        this.settings.addBoolean("Blast Furnaces", true);
        this.settings.addBoolean("Barrels", true);
        this.settings.addBoolean("Dispensers", true);
        this.settings.addBoolean("Droppers", true);
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
