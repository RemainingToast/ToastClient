package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.ToastClient;
import dev.toastmc.toastclient.api.events.ClipAtLedgeEvent;
import dev.toastmc.toastclient.api.events.PlayerAttackEntityEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Inject(
            at = {@At("HEAD")},
            method = {"attack"},
            cancellable = true
    )
    private void on(Entity entity, CallbackInfo info) {
        PlayerAttackEntityEvent event = new PlayerAttackEntityEvent(entity);
        ToastClient.INSTANCE.getEventBus().post(event);
        if (info.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"clipAtLedge"},
            cancellable = true
    )
    private void on(CallbackInfoReturnable<Boolean> cir) {
        ClipAtLedgeEvent event = new ClipAtLedgeEvent((PlayerEntity) (Object) this, false);
        ToastClient.INSTANCE.getEventBus().post(event);
        cir.setReturnValue(event.getClip());
    }
}
