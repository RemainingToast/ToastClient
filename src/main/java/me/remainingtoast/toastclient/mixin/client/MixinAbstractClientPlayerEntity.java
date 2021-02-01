package me.remainingtoast.toastclient.mixin.client;

import me.remainingtoast.toastclient.client.module.render.Capes;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity {

    @Inject(method = "getCapeTexture", at = @At("HEAD"), cancellable = true)
    public void getCapeTexture(CallbackInfoReturnable<Identifier> cir){
        if(Capes.INSTANCE.isEnabled()){
            switch (Capes.INSTANCE.getCapeType().getValue()){
                case OLD_MOJANG: {
                    cir.setReturnValue(new Identifier("toastclient", "capes/old_mojang.png"));
                    return;
                }
                case MINECON2013:{
                    cir.setReturnValue(new Identifier("toastclient", "capes/minecon_2013.png"));
                    return;
                }
                case MINECON2016: {
                    cir.setReturnValue(new Identifier("toastclient", "capes/minecon_2016.png"));
                    return;
                }
            }
        }
    }
}
