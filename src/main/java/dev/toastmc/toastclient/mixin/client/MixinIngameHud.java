package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.ToastClient;
import dev.toastmc.toastclient.api.events.OverlayRenderEvent;
import dev.toastmc.toastclient.impl.module.render.NoRender;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinIngameHud {

    @Shadow @Final private static Identifier PUMPKIN_BLUR;

    @Inject(
            at = {@At(value = "RETURN")},
            method = {"render"},
            cancellable = true
    )
    private void on(MatrixStack matrixStack, float float_1, CallbackInfo info) {
        OverlayRenderEvent event = new OverlayRenderEvent(matrixStack);
        ToastClient.Companion.getEventBus().post(event);
        if (event.isCancelled())
            info.cancel();
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"renderOverlay"},
            cancellable = true
    )
    private void on(Identifier texture, float opacity, CallbackInfo ci) {
        if (texture == PUMPKIN_BLUR &&
                NoRender.INSTANCE.isEnabled() &&
                NoRender.INSTANCE.getPumpkin().getValue()) {
            ci.cancel();
        }
    }

}
