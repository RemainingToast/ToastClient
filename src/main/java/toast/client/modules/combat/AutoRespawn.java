package toast.client.modules.combat;

import net.minecraft.client.gui.screen.DeathScreen;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;
import toast.client.utils.TimerUtil;

public class AutoRespawn extends Module {

    private TimerUtil timer = new TimerUtil();

    public AutoRespawn() {
        super("AutoRespawn", Category.COMBAT, -1);
        this.addNumberOption("Speed", 2, 1, 20);
    }

    public void onEnable() {
       timer.reset();
    }

    public void onDisable() {
        timer.reset();
    }

    @EventImpl
    public void onEvent(EventUpdate event) {
        if(mc.currentScreen != null) {
            if(this.timer.isDelayComplete((long) (this.getDouble("Speed") * 1000L))) {
                this.timer.setLastMS();
                if(mc.currentScreen instanceof DeathScreen) {
                    mc.player.requestRespawn();
                    mc.openScreen(null);
                }
            }
        }
    }
}
