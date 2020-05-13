package toast.client.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toast.client.event.EventManager;
import toast.client.event.events.player.EventAttack;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    public void attackEntity(PlayerEntity playerEntity_1, Entity entity_1, CallbackInfo ci) {
        EventAttack event = new EventAttack((LivingEntity) entity_1);
        EventManager.call(event);
        if(event.isCancelled()) ci.cancel();
    }

    @Inject(method = "attackBlock", at = @At("RETURN"))
    public void attackBlock(BlockPos blockPos_1, Direction direction_1, CallbackInfoReturnable info) {
        EventAttack event = new EventAttack(blockPos_1);
        EventManager.call(event);
        if(event.isCancelled()) info.setReturnValue(true);
    }
}