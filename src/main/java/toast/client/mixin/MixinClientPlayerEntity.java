package toast.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.modules.ModuleManager;
import toast.client.modules.misc.PortalChat;
import toast.client.modules.player.Surround;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Inject(method = "updateNausea", at = @At("HEAD"), cancellable = true)
    public void onUpdateNausea(CallbackInfo ci) {
        if (ModuleManager.getModule(PortalChat.class).isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "move", cancellable = true)
    public void move(CallbackInfo ci) {
        if (ModuleManager.getModule(Surround.class).isEnabled() && ModuleManager.getModule(Surround.class).getBool("Center")) {
            ci.cancel();
        }
    }
}
