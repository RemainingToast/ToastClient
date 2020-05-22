package toast.client.mixin;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toast.client.ToastClient;
import toast.client.event.EventManager;
import toast.client.event.events.player.EventUpdate;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.modules.dev.Panic;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tick(CallbackInfo ci) {
        EventUpdate event = new EventUpdate();
        EventManager.call(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "getWindowTitle", at = @At(value = "RETURN"), cancellable = true)
    private void getWindowTitle(CallbackInfoReturnable cir) {
        if(Panic.IsPanicking()) {
        } else {
            cir.setReturnValue("Minecraft "+SharedConstants.getGameVersion().getName()+" | "+ToastClient.cleanPrefix+" "+ToastClient.version);
        }
    }

}
