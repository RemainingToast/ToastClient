package toast.client.modules.movement;

import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;

public class FastStop extends Module {
    public FastStop() {
        super("Fast Stop", Category.MOVEMENT, -1);
        this.settings.addBoolean("Air Stop", false);
    }

    @EventImpl
    public void onUpdate(EventUpdate event) {
        if (mc.player == null) return;
        if (!mc.options.keyForward.isPressed() && !mc.options.keyBack.isPressed() && !mc.options.keyLeft.isPressed() && !mc.options.keyRight.isPressed()) {
            if (mc.player.onGround) {
                mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
            } else if (this.getBool("Air Stop") && !mc.player.onGround) {
                mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
            }
        }
    }
}
