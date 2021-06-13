package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.impl.module.render.Capes;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.URI;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity {

    @Inject(
            at = {@At("HEAD")},
            method = {"getCapeTexture"},
            cancellable = true
    )
    private void on(CallbackInfoReturnable<Identifier> cir){
        if(Capes.INSTANCE.isEnabled()){
            if(true) {
                cir.setReturnValue(Capes.INSTANCE.getIdentifierFromMode());
            }
            return;
        }
    }
}
