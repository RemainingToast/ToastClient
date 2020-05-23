package toast.client.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;
import toast.client.utils.MovementUtil;
import toast.client.utils.TimerUtil;

public class KillAura extends Module {

    private TimerUtil timer = new TimerUtil();

    public KillAura() {
        super("KillAura", Category.COMBAT, GLFW.GLFW_KEY_K);
        this.settings.addBoolean("Players", true);
        this.settings.addBoolean("Animals", false);
        this.settings.addBoolean("Monsters", false);
        this.settings.addBoolean("StopOnDeath", true);
        this.settings.addMode("LookType", "Packet", "Packet", "Client", "None");
        this.settings.addSlider("CPS", 1, 20, 20);
        this.settings.addSlider("Reach", 1, 4, 6);
    }

    public void onEnable() {
        timer.reset();
    }

    public void onDisable() {
        timer.reset();
    }

    @EventImpl
    public void onEvent(EventUpdate event) {
        if (mc.player == null || mc.world == null || mc.getNetworkHandler() == null) return;
        if (mc.player.isSpectator()) return;
        if ((!mc.player.isAlive() || mc.player.getHealth() <= 0F) && this.getBool("StopOnDeath")) {
            this.setEnabled(false);
        }
        double CPS = this.getDouble("CPS");
        if (timer.hasReached((float) timer.convertToMS((int) CPS))) {
            for (Entity entity : mc.world.getEntities()) {
                if (!entity.isAlive()) return;
                boolean shouldHit = false;
                if (entity instanceof AnimalEntity && this.getBool("Animals")) {
                    shouldHit = true;
                } else if (entity instanceof PlayerEntity && this.getBool("Players") && entity.getEntityId() != mc.player.getEntityId()) {
                    shouldHit = true;
                } else if (entity instanceof MobEntity && this.getBool("Monsters")) {
                    shouldHit = true;
                }
                if (shouldHit) {
                    if (mc.player.distanceTo(entity) <= (int) this.getDouble("Reach")) {
                        if (!this.settings.getMode("LookType").equals("None")) {
                            MovementUtil.lookAt(entity.getPos(), this.settings.getMode("LookType").equals("Packet"));
                        }
                        mc.getNetworkHandler().sendPacket(new PlayerInteractEntityC2SPacket(entity));
                        mc.player.attack(entity);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        timer.reset();
                        return;
                    }
                }
            }
            timer.reset();
        }
    }
}
