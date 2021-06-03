package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.impl.module.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class MixinInGameOverlayRenderer {

  @Inject(
      at = {@At("HEAD")},
      method = {
        "renderFireOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V"
      },
      cancellable = true)
  private static void on(
      MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo ci) {
    if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getFire().getValue()) ci.cancel();
  }

  @Inject(
      at = {@At("HEAD")},
      method = {
        "renderUnderwaterOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V"
      },
      cancellable = true)
  private static void on1(
      MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo ci) {
    if (NoRender.INSTANCE.isEnabled() && NoRender.INSTANCE.getUnderwater().getValue()) ci.cancel();
  }
}
