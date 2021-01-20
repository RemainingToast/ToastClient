package me.remainingtoast.toastclient.mixin.client;

import me.remainingtoast.toastclient.api.util.CapeUtil;
import me.remainingtoast.toastclient.client.module.render.Capes;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity {

    @Shadow @Nullable protected abstract PlayerListEntry getPlayerListEntry();

    @Inject(method = "getCapeTexture", at = @At("HEAD"), cancellable = true)
    public void getCapeTexture(CallbackInfoReturnable<Identifier> cir){
        UUID uuid = getPlayerListEntry().getProfile().getId();;
        if(uuid != null){
            if(Capes.INSTANCE.isEnabled() && CapeUtil.INSTANCE.hasCape(uuid)){
                switch (Capes.INSTANCE.getCapeType().getValue()){
                    case OLD_MOJANG: {
                        cir.setReturnValue(new Identifier("toastclient", "capes/old_mojang.png"));
                        return;
                    }
                    case MINECON_2013:{
                        cir.setReturnValue(new Identifier("toastclient", "capes/minecon_2013.png"));
                        return;
                    }
                    case MINECON_2016: {
                        cir.setReturnValue(new Identifier("toastclient", "capes/minecon_2016.png"));
                        return;
                    }
                }
            }
        }
    }
}
