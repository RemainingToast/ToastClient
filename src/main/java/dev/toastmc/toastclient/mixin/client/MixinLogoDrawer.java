package dev.toastmc.toastclient.mixin.client;

import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoDrawer.class)
public class MixinLogoDrawer {

    @Mutable
    @Shadow
    @Final
    public static Identifier EDITION_TEXTURE;

    @Inject(
            at = {@At("HEAD")},
            method = {"draw(Lnet/minecraft/client/util/math/MatrixStack;IFI)V"}
    )
    private void on(MatrixStack matrices, int screenWidth, float alpha, int y, CallbackInfo ci) {
        EDITION_TEXTURE = new Identifier("toastclient", "title/edition.png");
    }

}
