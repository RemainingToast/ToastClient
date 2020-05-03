package toast.client.modules.movement;

import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;
import org.lwjgl.glfw.GLFW;

public class Fly extends Module {
    public Fly() {
        super("Fly", Category.MOVEMENT, GLFW.GLFW_KEY_G);
        this.addModes("Vanilla");
    }

    @EventImpl
    public void onUpdate(EventUpdate event) {
        if(mc.player == null) return;
        if((mc.player.onGround || mc.player.fallDistance <= 0) && this.isEnabled()) {
            mc.player.abilities.allowFlying = true;
            mc.player.abilities.flying = true;
            mc.player.abilities.setFlySpeed(0.1F);
        }
    }

    public void onEnable() {
        if(mc.player == null) return;
        mc.player.abilities.allowFlying = true;
        mc.player.abilities.flying = true;
        mc.player.abilities.setFlySpeed(0.1F);
    }

    public void onDisable() {
        if(mc.player == null) return;
        if(!mc.player.abilities.creativeMode) {
            mc.player.abilities.allowFlying = false;
        }
        if(mc.player.isSpectator()) {
            mc.player.abilities.allowFlying = true;
            mc.player.abilities.flying = true;
            mc.player.abilities.setFlySpeed(0.05F);
            return;
        }
        mc.player.abilities.flying = false;
        mc.player.abilities.setFlySpeed(0.05F);
    }
}
