package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.impl.module.render.NoRender;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class MixinBossBarHud {

    @Inject(
            at = {@At("HEAD")},
            method = {"render"},
            cancellable = true
    )
    private void on(CallbackInfo info) {
        if(NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getBossbar().getValue()){
            info.cancel();
        }
    }

}
