package toast.client.modules.combat;

import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.SlotActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;
import toast.client.utils.Logger;

public class AutoTotem extends Module {
    protected int totems;
    boolean moving;
    boolean returning;
    public AutoTotem() {
        super("AutoTotem", "Automatically places totem into offhand", Category.COMBAT, -1);
    }

    @EventImpl
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.currentScreen instanceof ContainerScreen) return;
        totems = player.inventory.main.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) totems++;
        else {
            if (!player.inventory.offHand.isEmpty()) return;
            if (moving) {
                mc.interactionManager.clickSlot(0, 45, 0, SlotActionType.PICKUP, player);
                moving = false;
                if (!player.inventory.isInvEmpty()) returning = true;
                return;
            }
            if (player.inventory.isInvEmpty()) {
                if (totems == 0) return;
                int t = -1;
                for (int i = 0; i < 45; i++) {
                    if (player.inventory.getInvStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                        t = i;
                        break;
                    }
                }
                if (t == -1) return;
                mc.interactionManager.clickSlot(0, t < 9 ? t + 36 : t, 0, SlotActionType.PICKUP, player);
                moving = true;
            }
        }
    }
}
