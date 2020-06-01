package toast.client.modules.movement;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import toast.client.event.EventImpl;
import toast.client.event.events.network.EventPacketReceived;
import toast.client.modules.Module;

public class Velocity extends Module {
    public double lastx = 0;
    public double lasty = 0;
    public double lastz = 0;

    public Velocity() {
        super("Velocity", "Changes your velocity, can stop things from pushing you.", Module.Category.MOVEMENT, -1);
        this.settings.addMode("Mode", "Vanilla", "Vanilla", "YTeleport");
        this.settings.addBoolean("Cancel", true);
        this.settings.addSlider("HVel", -100.0, 0.0, 100.0);
        this.settings.addSlider("VVel", -100.0, 0.0, 100.0);
    }

    @Override
    public void onEnable() {
        this.lastx = 0D;
        this.lasty = 0D;
        this.lastz = 0D;
    }

    @Override
    public void onDisable() {
        this.lastx = 0D;
        this.lasty = 0D;
        this.lastz = 0D;
    }


    @EventImpl
    public void onPacket(EventPacketReceived e) {
        if (this.isEnabled()) {
            if (mc.player == null) return;
            if (this.isMode("Vanilla")) {
                if (e.getPacket() instanceof ExplosionS2CPacket) {
                    ExplosionS2CPacket s27 = (ExplosionS2CPacket) e.getPacket();
                    if (!this.getBool("Cancel")) {
                        double hvel = this.getDouble("HVel");
                        double vvel = this.getDouble("VVel");
                        mc.player.setVelocity(s27.getX() * (hvel * 0.00001),
                                s27.getY() * (vvel * 0.00001), s27.getZ() * (hvel * 0.00001));
                    }
                    e.setCancelled(true);
                }
                if (e.getPacket() instanceof EntityVelocityUpdateS2CPacket) {
                    EntityVelocityUpdateS2CPacket s12 = (EntityVelocityUpdateS2CPacket) e.getPacket();
                    if (s12.getId() != mc.player.getEntityId()) return;

                    if (!this.getBool("Cancel")) {
                        double hvel = this.getDouble("HVel");
                        double vvel = this.getDouble("VVel");
                        mc.player.setVelocity(s12.getVelocityX() * (hvel * 0.00001),
                                s12.getVelocityY() * (vvel * 0.00001), s12.getVelocityZ() * (hvel * 0.00001));
                    }
                    e.setCancelled(true);
                }
            } else if (this.isMode("YTeleport")) {
                if (mc.player.hurtTime == 0F) {
                    this.lastx = mc.player.getX();
                    this.lasty = mc.player.getY();
                    this.lastz = mc.player.getZ();
                } else if (mc.player.hurtTime >= 8) {
                    mc.player.setPos(this.lastx, this.lasty + 50D, this.lastz);
                }
            }
        }
    }
}
