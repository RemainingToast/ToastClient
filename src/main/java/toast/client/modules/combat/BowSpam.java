package toast.client.modules.combat;

import net.minecraft.item.BowItem;
import net.minecraft.util.Identifier;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;

public class BowSpam extends Module {

    int ticksLeft = -1;

    public BowSpam() {
        super("BowSpam", "Makes your bow fully automatic.", Category.COMBAT, -1);
        this.settings.addMode("Mode", "No Charge", "No Charge", "Medium Charge", "Full Charge");
    }

    @EventImpl
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null || !(mc.player.getMainHandStack().getItem() instanceof BowItem) || mc.player.getMainHandStack().getItem().getPropertyGetter(new Identifier("pulling")).call(mc.player.getMainHandStack(), mc.world, mc.player) == 0.0F) {
            ticksLeft = -1;
            return;
        }
        if (ticksLeft != -1) {
            ticksLeft--;
        } else {
            if (this.isMode("No Charge")) {
                ticksLeft = 2;
            } else if (this.isMode("Medium Charge")) {
                ticksLeft = 4;
            } else if (this.isMode("Full Charge")) {
                ticksLeft = 20;
            }
        }
        if (ticksLeft == 0) {
            mc.interactionManager.stopUsingItem(mc.player);
            ticksLeft = -1;
        }
    }
}
