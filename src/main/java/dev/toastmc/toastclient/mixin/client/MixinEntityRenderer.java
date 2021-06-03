package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.impl.module.render.NameTags;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {

  @Inject(
      at = {@At("HEAD")},
      method = {"renderLabelIfPresent"},
      cancellable = true)
  protected void on(
      T entity,
      Text text,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      CallbackInfo ci) {
    // TODO: Make Event
    if (NameTags.INSTANCE.isEnabled()) {
      NameTags.INSTANCE.renderNameTag(entity, text.asString(), matrices, vertexConsumers, light);
      ci.cancel();
    }
  }
}
