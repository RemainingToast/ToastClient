package dev.toastmc.toastclient.mixin.client;

import com.mojang.authlib.GameProfile;
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
        if (this.getPlayerListEntry() == null || this.getPlayerListEntry().getProfile() == null) {
            return;
        }

        GameProfile profile = this.getPlayerListEntry().getProfile();

        if (CapeUtil.INSTANCE.getCapeType(profile.getId()) != null) {
            String capeType = CapeUtil.INSTANCE.getCapeType(profile.getId());
            String capeType1 = capeType == null ? "NONE" : capeType;
            cir.setReturnValue(CapeUtil.INSTANCE.getIdentifierFromString(capeType1));
        }
    }
}
