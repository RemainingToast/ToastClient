package toast.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.modules.misc.PortalChat;
import toast.client.modules.combat.Surround;

import static toast.client.ToastClient.MODULE_MANAGER;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Inject(method = "updateNausea", at = @At("HEAD"), cancellable = true)
    public void onUpdateNausea(CallbackInfo ci) {
        if (MODULE_MANAGER.getModule(PortalChat.class).getEnabled()) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "move", cancellable = true)
    public void move(CallbackInfo ci) {
        Surround surround = (Surround) MODULE_MANAGER.getModule(Surround.class);
        if (surround.getEnabled() && surround.getBool("Center")) {
            ci.cancel();
        }
    }
}
