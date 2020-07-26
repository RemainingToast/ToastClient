package toast.client.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toast.client.events.player.EventAttack;

import static toast.client.ToastClient.eventBus;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    public void attackEntity(PlayerEntity playerEntity_1, Entity entity_1, CallbackInfo ci) {
        try {
            EventAttack event = new EventAttack(entity_1);
            eventBus.post(event);
            if (event.isCancelled()) ci.cancel();
        } catch (NullPointerException null_is_annoying) {
        }
    }

    @Inject(method = "attackBlock", at = @At("RETURN"))
    public void attackBlock(BlockPos blockPos_1, Direction direction_1, CallbackInfoReturnable info) {
        EventAttack event = new EventAttack(blockPos_1);
        eventBus.post(event);
        if (event.isCancelled()) info.setReturnValue(true);
    }
}