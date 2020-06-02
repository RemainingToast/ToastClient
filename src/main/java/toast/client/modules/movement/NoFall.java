package toast.client.modules.movement;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "Stops the player form taking fall damage", Category.MOVEMENT, -1);
    }

    @EventImpl
    public void onUpdate(EventUpdate event) {
        if (mc.player == null) return;
        if (mc.player.isFallFlying() && !(mc.player.fallDistance > 3f)) return;
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket(true));
    }
}
