package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.ToastClient;
import dev.toastmc.toastclient.api.events.RenderEvent;
import dev.toastmc.toastclient.impl.module.player.NoEntityTrace;
import dev.toastmc.toastclient.impl.module.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(
            at = {@At("HEAD")},
            method = {"bobViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"},
            cancellable = true
    )
    private void on(MatrixStack matrixStack, float f, CallbackInfo ci) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getHurtcam().getValue() && !(MinecraftClient.getInstance().world == null))
            ci.cancel();
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"showFloatingItem"},
            cancellable = true
    )
    private void on(ItemStack itemStack_1, CallbackInfo ci) {
        if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getTotem().getValue() && itemStack_1.getItem() == Items.TOTEM_OF_UNDYING)
            ci.cancel();
    }

    @Redirect(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0),
            method = {"renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V"}
    )
    private float on(float delta, float first, float second) {
        if (!(NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getNausea().getValue()))
            return MathHelper.lerp(delta, first, second);
        return 0;
    }


    @Inject(
            at = {@At("HEAD")},
            method = {"renderHand"},
            cancellable = true
    )
    private void on(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        RenderEvent.World event = new RenderEvent.World(tickDelta, matrices, camera);
        ToastClient.Companion.getEventBus().post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/EntityHitResult;getEntity()Lnet/minecraft/entity/Entity;"),
            method = {"updateTargetedEntity"},
            cancellable = true
    )
    private void on(float tickDelta, CallbackInfo info) {
        if (NoEntityTrace.INSTANCE.work() && MinecraftClient.getInstance().crosshairTarget != null) {
            if (MinecraftClient.getInstance().crosshairTarget.getType() == HitResult.Type.BLOCK) {
                MinecraftClient.getInstance().getProfiler().pop();
                info.cancel();
            }
        }
    }
}
