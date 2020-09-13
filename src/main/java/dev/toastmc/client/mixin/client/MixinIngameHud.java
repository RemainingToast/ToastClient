package dev.toastmc.client.mixin.client;

import dev.toastmc.client.ToastClient;
import dev.toastmc.client.event.OverlayEvent;
import dev.toastmc.client.module.render.NoRender;
import dev.toastmc.client.util.UtilKt;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinIngameHud {

    private static NoRender mod = (NoRender) ToastClient.Companion.getMODULE_MANAGER().getModuleByClass(NoRender.class);


    @Inject(at = @At(value = "RETURN"), method = "render", cancellable = true)
    public void render(MatrixStack matrixStack, float float_1, CallbackInfo info) {
        OverlayEvent event = new OverlayEvent(matrixStack);
        ToastClient.EVENT_BUS.post(event);
        if (event.isCancelled())
            info.cancel();
    }

    @Inject(at = @At("HEAD"), method = "renderPumpkinOverlay()V", cancellable = true)
    private void onRenderPumpkinOverlay(CallbackInfo ci) {
        if (mod.getEnabled() && mod.getPumpkin())
            ci.cancel();
    }
}
