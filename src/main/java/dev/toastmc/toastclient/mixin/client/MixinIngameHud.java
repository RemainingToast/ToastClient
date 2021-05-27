package dev.toastmc.toastclient.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinIngameHud {

    @Inject(at = @At(value = "RETURN"), method = "render", cancellable = true)
    public void render(MatrixStack matrixStack, float float_1, CallbackInfo info) {
//        OverlayEvent event = new OverlayEvent(matrixStack);
//        ToastClient.EVENT_BUS.post(event);
//        if (event.isCancelled())
//            info.cancel();
    }

    @Inject(at = @At("HEAD"), method = "renderPumpkinOverlay()V", cancellable = true)
    private void onRenderPumpkinOverlay(CallbackInfo ci) {
//        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getPumpkin().getValue())
//            ci.cancel();
    }

}
