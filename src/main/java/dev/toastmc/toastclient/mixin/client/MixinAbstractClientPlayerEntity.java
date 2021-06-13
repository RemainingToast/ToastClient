package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.api.util.entity.CapeUtil;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity {

    @Shadow @Nullable protected abstract PlayerListEntry getPlayerListEntry();

    @Inject(
            at = {@At("HEAD")},
            method = {"getCapeTexture"},
            cancellable = true
    )
    private void on(CallbackInfoReturnable<Identifier> cir){
            if(CapeUtil.INSTANCE.getCapeType(this.getPlayerListEntry().getProfile().getId()) != null) {
                cir.setReturnValue(CapeUtil.INSTANCE.getIdentifierFromString(CapeUtil.INSTANCE.getCapeType(this.getPlayerListEntry().getProfile().getId())));
            }
    }
}
