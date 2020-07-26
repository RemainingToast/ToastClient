package toast.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.modules.combat.Surround;

import static toast.client.ToastClient.MODULE_MANAGER;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    @Inject(at = @At("HEAD"), method = "jump()V", cancellable = true)
    public void jump(CallbackInfo ci) {
        Surround surround = (Surround) MODULE_MANAGER.getModule(Surround.class);
        if (surround.getEnabled() && surround.getBool("Center")) {
            ci.cancel();
        }
    }
}
