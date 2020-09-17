package dev.toastmc.client.module.combat

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.WorldUtil.BEDS
import dev.toastmc.client.util.WorldUtil.openBlock
import dev.toastmc.client.util.WorldUtil.sphere
import dev.toastmc.client.util.mc
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import java.util.stream.Collectors
import kotlin.math.floor


@ModuleManifest(
        label = "BedAura",
        category = Category.COMBAT
)
class BedAura : Module() {
    @Setting(name = "Range") var range = 3

    override fun onEnable() {
        EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        if (mc.world!!.registryKey.value.path != "overworld") {
            val beds = sphere(getFloorPos(mc.player!!), range).stream().filter { isBed(it) }.collect(Collectors.toList())
            for (pos in beds) {
                if (!mc.player!!.isSneaking) {
                    openBlock(pos)
                }
                println("Bed found at $pos")
            }
            if(beds.isNotEmpty()) println(beds)
        }
    })

    private fun getFloorPos(player: PlayerEntity): BlockPos {
        return BlockPos(floor(player.x), floor(player.y), floor(player.z))
    }

    private fun isBed(pos: BlockPos?): Boolean {
        val block: Block = mc.world!!.getBlockState(pos).block
        return BEDS.contains(block)
    }
}