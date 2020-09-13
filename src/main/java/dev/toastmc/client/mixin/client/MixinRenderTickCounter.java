package dev.toastmc.client.mixin.client;

import dev.toastmc.client.ToastClient;
import dev.toastmc.client.module.movement.Timer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.class)
public class MixinRenderTickCounter {
    @Shadow
    public float lastFrameDuration;

    private static Timer mod = (Timer) ToastClient.Companion.getMODULE_MANAGER().getModuleByClass(Timer.class);

    @Inject(method = "beginRenderTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/RenderTickCounter;prevTimeMillis:J"))
    private void onBeingRenderTick(long a, CallbackInfoReturnable<Integer> info) {
        lastFrameDuration *= mod.getMultiplier();
    }
}
