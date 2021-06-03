package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.ToastClient;
import dev.toastmc.toastclient.api.events.EntityEvent;
import dev.toastmc.toastclient.api.events.EntityVelocityMultiplierEvent;
import dev.toastmc.toastclient.api.events.MoveEntityFluidEvent;
import dev.toastmc.toastclient.api.events.UpdateLookEvent;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

  @Shadow public World world;

  @Redirect(
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V",
              ordinal = 0),
      method = {"pushAwayFrom"})
  private void on(Entity entity, double x, double y, double z) {
    EntityEvent.EntityCollision event = new EntityEvent.EntityCollision(entity, x, y, z);
    ToastClient.Companion.getEventBus().post(event);
    if (event.isCancelled()) return;
    entity.addVelocity(event.getX(), event.getY(), event.getZ());
  }

  @Redirect(
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/fluid/FluidState;getVelocity(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/Vec3d;"),
      method = {"updateMovementInFluid"})
  private Vec3d on(FluidState fluidState, BlockView world, BlockPos pos) {
    Vec3d vec = fluidState.getVelocity(world, pos);
    MoveEntityFluidEvent event = new MoveEntityFluidEvent(((Entity) (Object) this), vec);
    ToastClient.Companion.getEventBus().post(event);
    return event.isCancelled() ? Vec3d.ZERO : event.getMovement();
  }

  @Inject(
      at = {@At("RETURN")},
      method = {"getVelocityMultiplier"},
      cancellable = true)
  private void on(CallbackInfoReturnable<Float> cir) {
    float returnValue = cir.getReturnValue();
    EntityVelocityMultiplierEvent event =
        new EntityVelocityMultiplierEvent((Entity) (Object) this, returnValue);
    ToastClient.Companion.getEventBus().post(event);
    if (!event.isCancelled() && event.getMultiplier() != returnValue) {
      cir.setReturnValue(event.getMultiplier());
    }
  }

  @Inject(
      at = {@At("HEAD")},
      method = {"changeLookDirection"},
      cancellable = true)
  private void on(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
    UpdateLookEvent event = new UpdateLookEvent(cursorDeltaX, cursorDeltaY);
    ToastClient.Companion.getEventBus().post(event);
    if (event.isCancelled()) ci.cancel();
  }
}
