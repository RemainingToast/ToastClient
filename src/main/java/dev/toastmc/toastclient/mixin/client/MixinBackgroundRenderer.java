package dev.toastmc.toastclient.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.toastmc.toastclient.impl.module.render.NoFog;
import dev.toastmc.toastclient.impl.module.render.NoRender;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {

    @Redirect(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"),
            method = {"render(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)V", "applyFog"}
    )
    private static boolean on(LivingEntity entity, StatusEffect effect) {
        if (effect == StatusEffects.BLINDNESS &&
                NoRender.INSTANCE.isEnabled() &&
                NoRender.INSTANCE.getBlindness().getValue()
        ) {
            return false;
        }
        return entity.hasStatusEffect(effect);
    }

    @Inject(at = @At(value = "HEAD"), method = {"applyFog"}, cancellable = true)
    private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        if (NoFog.INSTANCE.isEnabled()) ci.cancel();
    }

    /*@Inject(
            at = { @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V") },
            method = {"applyFog"},
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void on(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci,
                           CameraSubmersionType cameraSubmersionType,
                           Entity entity,
                           BackgroundRenderer.FogData fogData) {
        if (NoFog.INSTANCE.isEnabled()) RenderSystem.setShaderFogStart(RenderSystem.getShaderFogStart() * 10000);
    }

    @Inject(
            at = { @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V") },
            method = {"applyFog"},
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void on(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci,
                           CameraSubmersionType cameraSubmersionType,
                           Entity entity,
                           BackgroundRenderer.FogData fogData) {
        if (NoFog.INSTANCE.isEnabled()) RenderSystem.setShaderFogEnd(RenderSystem.getShaderFogEnd() * 10000);
    }*/

}
