package dev.toastmc.client.module.combat

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.PacketEvent
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.MessageUtil
import dev.toastmc.client.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.Listener
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket
import java.util.*
import kotlin.collections.HashMap

@ModuleManifest(
    label = "TotemNotifier",
    description = "Notify Totem Pops",
    category = Category.COMBAT
)
class TotemNotifier : Module() {
    private val totemPops: MutableMap<UUID, Int> = HashMap()

    override fun onEnable() {
        totemPops.clear()
        EVENT_BUS.subscribe(onReceivePacket)
        EVENT_BUS.subscribe(onTick)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onReceivePacket)
        EVENT_BUS.unsubscribe(onTick)
    }

    @EventHandler
    private val onReceivePacket: Listener<PacketEvent.Receive> = Listener({ event: PacketEvent.Receive ->
            if (event.packet !is EntityStatusS2CPacket) return@Listener
            val p = event.packet
            if (p.status.toInt() != 35) return@Listener
            val entity = p.getEntity(mc.world) ?: return@Listener
            synchronized(totemPops) {
                var pops = totemPops.getOrDefault(entity.uuid, 0)
                pops++
                totemPops[entity.uuid] = pops
                MessageUtil.sendMessage("${entity.name.string} popped $pops ${if (pops == 1) "totem" else "totems"}", MessageUtil.Color.GRAY)
            }
        })

    @EventHandler
    private val onTick: Listener<TickEvent.Client.InGame> = Listener<TickEvent.Client.InGame>({
        synchronized(totemPops) {
            for (player in mc.world!!.players) {
                if (!totemPops.containsKey(player.uuid)) continue
                if (player.deathTime > 0 || player.health <= 0) {
                    val pops = totemPops.remove(player.uuid)!!
                    MessageUtil.sendMessage("${player.name.string} died after popping $pops ${if (pops == 1) "totem" else "totems"}", MessageUtil.Color.GRAY)
                }
            }
        }
    })
}