package me.remainingtoast.toastclient.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.remainingtoast.toastclient.client.module.render.NoFog;
import me.remainingtoast.toastclient.client.module.render.NoRender;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"), method = {"render(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)V", "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V" })
    private static boolean hasStatusEffect(LivingEntity entity, StatusEffect effect) {
        if (effect == StatusEffects.BLINDNESS && NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getBlindness().getValue())
            return false;
        return entity.hasStatusEffect(effect);
    }

    @Inject(method = "applyFog", locals = LocalCapture.CAPTURE_FAILHARD, at = { @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;fogStart(F)V") })
    private static void applyFogModifyStart(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, FluidState fluidState, Entity entity, float start) {
        if (NoFog.INSTANCE.isEnabled()) RenderSystem.fogStart(start * 10000);
    }

    @Inject(method = "applyFog", locals = LocalCapture.CAPTURE_FAILHARD, at = { @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lcom/mojang/blaze3d/systems/RenderSystem;fogEnd(F)V") })
    private static void applyFogModifyEnd(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo info, FluidState fluidState, Entity entity, float start, float end) {
        if (NoFog.INSTANCE.isEnabled()) RenderSystem.fogEnd(end * 10000);
    }

}
