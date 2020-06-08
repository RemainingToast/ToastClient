package toast.client.modules.movement;

import com.google.common.eventbus.Subscribe;
import toast.client.events.player.EventUpdate;
import toast.client.modules.Module;

public class Sprint extends Module {


    public Sprint() {
        super("Sprint", "Automatically sprint", Category.MOVEMENT, -1);
    }

    @Subscribe
    public void onTick(EventUpdate event) {
        if (mc.player == null) return;
        if (!isEnabled()) return;
        mc.player.setSprinting(mc.player.input.movementForward > 0 && mc.player.input.movementSideways != 0 ||
                mc.player.input.movementForward > 0 && !mc.player.isSneaking());
    }
}
