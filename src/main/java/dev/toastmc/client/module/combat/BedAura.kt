package dev.toastmc.client.module.combat

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.mc
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.block.entity.BedBlockEntity
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import kotlin.math.sqrt

@ModuleManifest(
    label = "BedAura",
    category = Category.COMBAT
)
class BedAura : Module() {
    @Setting(name = "Suicide") var suicide = true
    @Setting(name = "Break") var breakk = true
    @Setting(name = "Range") var range = 3

    override fun onEnable() {
        EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null || mc.world!!.registryKey.value.path == "overworld" || mc.world!!.blockEntities == null) return@EventHook
        for (b in mc.world!!.blockEntities) {
            if (b is BedBlockEntity) {
                println("${distance(b.pos, mc.player!!.blockPos)} blocks away, in range = ${distance(b.pos, mc.player!!.blockPos) <= range}")
                mc.player!!.isSneaking = false
                mc.interactionManager!!.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, BlockHitResult(mc.player!!.pos, Direction.UP, b.pos, false))
            }
        }
    })

    private fun distance(blockPos: BlockPos, blockPos1: BlockPos): Double {
        return sqrt((blockPos1.x - blockPos.x * blockPos1.x - blockPos.x + blockPos1.y - blockPos.x * blockPos1.y - blockPos.x + blockPos1.y - blockPos.z * blockPos1.y - blockPos.z).toDouble())
    }
}