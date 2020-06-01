package toast.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.modules.ModuleManager;
import toast.client.modules.player.Surround;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
    @Inject(at = @At("HEAD"), method = "jump()V", cancellable = true)
    public void jump (CallbackInfo ci) {
        if (ModuleManager.getModule(Surround.class).isEnabled() && ModuleManager.getModule(Surround.class).getBool("Center")) {
            ci.cancel();
        }
    }
}
