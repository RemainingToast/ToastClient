package dev.toastmc.toastclient.mixin.client;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public class MixinSharedConstants {

  /** @author Morgz Makes you able to "chat" § etc (normally "illegal" characters) */
  @Inject(
      at = {@At("HEAD")},
      method = {"isValidChar"},
      cancellable = true)
  private static void on(CallbackInfoReturnable<Boolean> info) {
    info.setReturnValue(true);
  }
}
