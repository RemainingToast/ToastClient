package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.ToastClient;
import dev.toastmc.toastclient.api.events.EntityEvent.EntityDamage;
import dev.toastmc.toastclient.api.events.EntityEvent.EntityDeath;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(
        at = {@At("RETURN")},
        method = {"applyDamage"}
    )
    private void on(DamageSource source, float amount, CallbackInfo ci) {
        EntityDamage event = new EntityDamage((LivingEntity) (Object) this, source, amount);
        ToastClient.Companion.getEventBus().post(event);
    }

    @Inject(
        at = {@At("RETURN")},
        method = {"onDeath"}
    )
    private void on(DamageSource source, CallbackInfo ci) {
        EntityDeath event = new EntityDeath((LivingEntity) (Object) this, source);
        ToastClient.Companion.getEventBus().post(event);
    }
}
