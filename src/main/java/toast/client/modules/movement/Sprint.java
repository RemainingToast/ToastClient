package toast.client.modules.movement;

import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;

public class Sprint extends Module {


    public Sprint() {
        super("Sprint", "Automatically sprint", Category.MOVEMENT, -1);
    }

    @EventImpl
    public void onTick(EventUpdate event) {
        if (mc.player == null) return;
        if (!isEnabled()) return;
        mc.player.setSprinting(mc.player.input.movementForward > 0 && mc.player.input.movementSideways != 0 ||
                mc.player.input.movementForward > 0 && !mc.player.isSneaking());
    }
}
