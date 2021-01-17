package me.remainingtoast.toastclient.mixin.client;

import me.remainingtoast.toastclient.ToastClient;
import me.remainingtoast.toastclient.api.event.RenderEvent;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(at = @At("HEAD"), method = "renderHand", cancellable = true)
    private void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        RenderEvent.World event = new RenderEvent.World(tickDelta, matrices, camera);
        ToastClient.EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }

}
