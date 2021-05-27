package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.ToastClient;
import dev.toastmc.toastclient.api.events.OverlayEvent;
import dev.toastmc.toastclient.impl.module.render.NoRender;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinIngameHud {

    @Inject(
            at = {@At(value = "RETURN")},
            method = {"render"},
            cancellable = true
    )
    private void on(MatrixStack matrixStack, float float_1, CallbackInfo info) {
        OverlayEvent event = new OverlayEvent(matrixStack);
        ToastClient.INSTANCE.getEventBus().post(event);
        if (event.isCancelled())
            info.cancel();
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"renderPumpkinOverlay()V"},
            cancellable = true
    )
    private void on(CallbackInfo ci) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getPumpkin().getValue())
            ci.cancel();
    }

}
