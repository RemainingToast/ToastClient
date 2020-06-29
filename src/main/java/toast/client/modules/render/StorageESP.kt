package toast.client.modules.render

import com.google.common.eventbus.Subscribe
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.vehicle.ChestMinecartEntity
import net.minecraft.util.math.BlockPos
import toast.client.events.player.EventRender
import toast.client.modules.Module
import toast.client.utils.WorldUtil.getTileEntitiesInWorld
import java.util.function.Consumer

/**
 * Highlights storage TileEntities in the world
 */
class StorageESP : Module("StorageESP", "Highlights storage blocks in the world.", Category.RENDER, -1) {
    override fun onEnable() {
        disable()
    }

    @Subscribe
    fun onUpdate(event: EventRender?) {
        if (mc.world == null) return
        mc.world!!.getTileEntitiesInWorld().forEach { (pos: BlockPos?, type: Block) ->
            if (getBool("Chests") && type === Blocks.CHEST ||
                    getBool("Trapped Chests") && type === Blocks.TRAPPED_CHEST ||
                    getBool("Furnaces") && type === Blocks.FURNACE ||
                    getBool("Smokers") && type === Blocks.SMOKER ||
                    getBool("Blast Furnaces") && type === Blocks.BLAST_FURNACE ||
                    getBool("Barrels") && type === Blocks.BARREL ||
                    getBool("Dispensers") && type === Blocks.DISPENSER ||
                    getBool("Droppers") && type === Blocks.DROPPER) {
                // Render stuff for block
            }
        }
        mc.world!!.entities.forEach(Consumer { entity: Entity? ->
            if (getBool("Minecart Chests") && entity is ChestMinecartEntity) {
                // Render stuff for entities
            }
        })
    }

    init {
        settings.addBoolean("Chests", true)
        settings.addBoolean("Trapped Chests", true)
        settings.addBoolean("Minecart Chests", true)
        settings.addBoolean("Furnaces", true)
        settings.addBoolean("Smokers", true)
        settings.addBoolean("Blast Furnaces", true)
        settings.addBoolean("Barrels", true)
        settings.addBoolean("Dispensers", true)
        settings.addBoolean("Droppers", true)
    }
}