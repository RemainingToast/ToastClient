package toast.client.modules.player;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.math.BlockPos;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventAttack;
import toast.client.modules.Module;

public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool", Category.PLAYER, -1);
    }

    private int lastSlot = 0;

//TODO: make this also look at enchantments
    public void setSlot(BlockPos blockPos) {
        if(mc.player == null || mc.world == null) return;
        float bestSpeed = 0.0F;
        int bestSlot = -1;
        if (mc.player.isCreative()) return;
        Block block = mc.world.getBlockState(blockPos).getBlock();
        for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
            ItemStack item = mc.player.inventory.getInvStack(i);
            if (!item.isEmpty()) {
                float speed = item.getMiningSpeed(block.getDefaultState());
                if (speed > bestSpeed) {
                    bestSpeed = speed;
                    bestSlot = i;
                }
            }
        }
        //System.out.println("best slot: "+bestSlot+", selected: "+mc.player.inventory.selectedSlot);
        if (bestSlot != -1 && bestSlot != mc.player.inventory.selectedSlot) {
            lastSlot = mc.player.inventory.selectedSlot;
            mc.player.inventory.selectedSlot = bestSlot;
        }
    }

    public void setSlot(LivingEntity entity) {
        if(mc.player == null) return;
        float bestValue = 0.0F;
        int bestSlot = -1;
        if (mc.player.isCreative()) return;
        for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
            ItemStack item = mc.player.inventory.getInvStack(i);
            if (!item.isEmpty() && item.getItem() instanceof ToolItem) {
                float attackValue = ((ToolItem) item.getItem()).getMaterial().getAttackDamage();
                if (attackValue > bestValue) {
                    bestValue = attackValue;
                    bestSlot = i;
                }
            }
        }
        //System.out.println("best slot: "+bestSlot+", selected: "+mc.player.inventory.selectedSlot);
        if (bestSlot != -1 && bestSlot != mc.player.inventory.selectedSlot) {
            lastSlot = mc.player.inventory.selectedSlot;
            mc.player.inventory.selectedSlot = bestSlot;
        }
    }

    public void onDisable() {
        if(mc.player == null) return;
        mc.player.inventory.selectedSlot = lastSlot;
    }

    @EventImpl
    public void onEvent(EventAttack event) {
        //System.out.println("attack event: entity: "+event.isAttackingEntity()+" block: "+event.isAttackingBlock());
        if(event.isAttackingBlock()) {
            setSlot(event.getBlock());
        } else if(event.isAttackingEntity()) {
            setSlot(event.getEntity());
        }
    }
}
