package toast.client.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.modules.ModuleManager;
import toast.client.modules.misc.PortalChat;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Inject(method = "updateNausea", at = @At("HEAD"), cancellable = true)
    public void onUpdateNausea(CallbackInfo ci) {
        if (ModuleManager.getModule(PortalChat.class).isEnabled()) {
            ci.cancel();
        }
    }
}
