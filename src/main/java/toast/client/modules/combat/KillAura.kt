package toast.client.modules.combat

import com.google.common.eventbus.Subscribe
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.util.Hand
import org.lwjgl.glfw.GLFW
import toast.client.events.player.EventUpdate
import toast.client.modules.Module
import toast.client.utils.MovementUtil
import toast.client.utils.TimerUtil

class KillAura : Module("KillAura", "Automatically attacks mobs and players around you.", Category.COMBAT, GLFW.GLFW_KEY_K) {
    private val timer = TimerUtil()
    override fun onEnable() {
        timer.reset()
    }

    override fun onDisable() {
        timer.reset()
    }

    @Subscribe
    fun onEvent(event: EventUpdate?) {
        if (mc.player == null || mc.world == null || mc.networkHandler == null) return
        if (mc.player!!.isSpectator) return
        if ((!mc.player!!.isAlive || mc.player!!.health <= 0f) && getBool("StopOnDeath")) {
            this.disable()
        }
        val CPS = getDouble("CPS")
        if (timer.hasReached(timer.convertToMS(CPS?.toInt()!!).toFloat())) {
            for (entity in mc.world!!.entities) {
                if (!entity.isAlive) return
                var shouldHit = false
                when {
                    entity is AnimalEntity && getBool("Animals") -> shouldHit = true
                    entity is PlayerEntity && getBool("Players") && entity.getEntityId() != mc.player!!.entityId -> shouldHit = true
                    entity is MobEntity && getBool("Monsters") -> shouldHit = true
                }
                if (shouldHit) {
                    if (mc.player!!.distanceTo(entity) <= getDouble("Reach")!!.toInt()) {
                        if (settings.getMode("LookType") != "None") MovementUtil.lookAt(entity.pos, settings.getMode("LookType") == "Packet")
                        mc.networkHandler!!.sendPacket(PlayerInteractEntityC2SPacket(entity))
                        mc.player!!.attack(entity)
                        mc.player!!.swingHand(Hand.MAIN_HAND)
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