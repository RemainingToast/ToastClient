package toast.client.events.player;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import toast.client.events.AbstractSkippableEvent;

public class EventAttack extends AbstractSkippableEvent {
    public Entity entity = null;
    public BlockPos block = null;

    public EventAttack(Entity entity) {
        this.entity = entity;
    }

    public EventAttack(BlockPos block) {
        this.block = block;
    }

    public BlockPos getBlock() {
        return block;
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isAttackingEntity() {
        return entity != null;
    }

    public boolean isAttackingBlock() {
        return block != null;
    }
}
