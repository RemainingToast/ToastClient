package toast.client.modules.movement;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import toast.client.event.EventImpl;
import toast.client.event.events.network.EventPacketSent;
import toast.client.modules.Module;
import toast.client.utils.Logger;

public class AutoWalk extends Module {

    public AutoWalk() {
        super("AutoWalk", "Automatically walk forwards", Category.MOVEMENT, -1);
        this.settings.addMode("Mode", "Simple","Simple","Baritone");
    }

    @EventImpl
    public void onEvent(EventPacketSent e){
        {
            if (mc.player == null) return;
            if(this.settings.getMode("Mode").equals("Simple")) {
                if (e.getPacket() instanceof PlayerMoveC2SPacket || e.getPacket() instanceof InventoryS2CPacket) {
                    mc.options.sprintToggled = true;
                    mc.options.keyForward.setPressed(true);
                }
            }else if (this.settings.getMode("Mode").equals("Baritone")){
                Logger.message("Ree baritone integrated yet", Logger.ERR, false);

                mc.options.keyForward.setPressed(false);
            }
        }
    }
    public void onDisable(){
        if(mc.player == null) return;
        mc.options.keyForward.setPressed(false);
    }
}
