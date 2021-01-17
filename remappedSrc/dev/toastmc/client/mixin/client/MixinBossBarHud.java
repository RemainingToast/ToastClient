package me.remainingtoast.toastclient.mixin.client;

import dev.toastmc.client.ToastClient;
import dev.toastmc.client.module.render.NoRender;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class MixinBossBarHud {

    private NoRender mod = (NoRender) ToastClient.Companion.getMODULE_MANAGER().getModuleByClass(NoRender.class);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(CallbackInfo info) {
        if(mod.getEnabled() && mod.getBossbar()){
            info.cancel();
        }
    }

}
