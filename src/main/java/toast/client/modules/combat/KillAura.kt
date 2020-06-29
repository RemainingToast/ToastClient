package toast.client.modules.combat

import com.google.common.eventbus.Subscribe
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.util.Hand
import org.lwjgl.glfw.GLFW
import toast.client.events.network.EventSyncedUpdate
import toast.client.modules.Module
import toast.client.utils.MovementUtil
import toast.client.utils.TimerUtil

/**
 * Module that automatically attacks selected entity types if they are close enough
 */
class KillAura : Module("KillAura", "Automatically attacks mobs and players around you.", Category.COMBAT, GLFW.GLFW_KEY_K) {
    private val timer = TimerUtil()
    override fun onEnable() {
        timer.reset()
    }

    override fun onDisable() {
        timer.reset()
    }

    @Subscribe
    fun onEvent(event: EventSyncedUpdate?) {
        if ((mc.player ?: return).isSpectator) return
        if ((!(mc.player ?: return).isAlive || (mc.player ?: return).health <= 0f) && getBool("StopOnDeath")) {
            this.disable()
        }
        if (timer.isDelayComplete(1000 / getDouble("CPS"))) {
            for (entity in (mc.world ?: return).entities) {
                if (!entity.isAlive) return
                var shouldHit = false
                when {
                    entity is AnimalEntity && getBool("Animals") -> shouldHit = true
                    entity is PlayerEntity && getBool("Players") && entity.getEntityId() != (mc.player
                            ?: return).entityId -> shouldHit = true
                    entity is MobEntity && getBool("Monsters") -> shouldHit = true
                }
                if (shouldHit) {
                    if ((mc.player ?: return).distanceTo(entity) <= getDouble("Reach").toInt()) {
                        if (settings.getMode("LookType") != "None") MovementUtil.lookAt(entity.pos, settings.getMode("LookType") == "Packet")
                        (mc.networkHandler ?: return).sendPacket(PlayerInteractEntityC2SPacket(entity))
                        (mc.player ?: return).attack(entity)
                        (mc.player ?: return).swingHand(Hand.MAIN_HAND)
                        timer.reset()
                        return
                    }
                }
            }
            timer.reset()
        }
    }

    init {
        settings.addBoolean("Players", true)
        settings.addBoolean("Animals", false)
        settings.addBoolean("Monsters", false)
        settings.addBoolean("StopOnDeath", true)
        settings.addMode("LookType", "Packet", "Packet", "Client", "None")
        settings.addSlider("CPS", 1.0, 20.0, 20.0)
        settings.addSlider("Reach", 1.0, 4.0, 6.0)
    }
}