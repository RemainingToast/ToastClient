package dev.toastmc.client.mixin;

import dev.toastmc.client.ToastClient;
import dev.toastmc.client.modules.combat.Surround;
import dev.toastmc.client.modules.misc.PortalChat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Inject(method = "updateNausea", at = @At("HEAD"), cancellable = true)
    public void onUpdateNausea(CallbackInfo ci) {
        if (ToastClient.MODULE_MANAGER.getModule(PortalChat.class).getEnabled()) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "move", cancellable = true)
    public void move(CallbackInfo ci) {
        Surround surround = (Surround) ToastClient.MODULE_MANAGER.getModule(Surround.class);
        if (surround.getEnabled() && surround.getBool("Center")) {
            ci.cancel();
        }
    }
}
