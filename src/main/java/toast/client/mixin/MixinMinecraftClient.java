package toast.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toast.client.ToastClient;
import toast.client.events.player.EventUpdate;
import toast.client.modules.misc.Panic;
import toast.client.utils.RandomMOTD;

import static toast.client.ToastClient.eventBus;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tick(CallbackInfo ci) {
        EventUpdate event = new EventUpdate();
        eventBus.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "getWindowTitle", at = @At(value = "RETURN"), cancellable = true)
    private void getWindowTitle(CallbackInfoReturnable cir) {
        if (!Panic.IsPanicking()) {
            cir.setReturnValue(ToastClient.cleanPrefix + " " + ToastClient.version + " | " + RandomMOTD.randomMOTD());
        }
    }

}
