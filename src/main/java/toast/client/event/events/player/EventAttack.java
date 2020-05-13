package toast.client.event.events.player;

import toast.client.event.events.AbstractSkippableEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class EventAttack extends AbstractSkippableEvent {
    public LivingEntity entity = null;
    public BlockPos block = null;

    public EventAttack(LivingEntity entity) {
        this.entity = entity;
    }
    public EventAttack(BlockPos block) {
        this.block = block;
    }

    public BlockPos getBlock() {
        return block;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public boolean isAttackingEntity() {
        return entity != null;
    }

    public boolean isAttackingBlock() {
        return block != null;
    }
}
