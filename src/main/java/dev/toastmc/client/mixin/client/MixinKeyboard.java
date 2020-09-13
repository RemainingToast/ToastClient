package dev.toastmc.client.mixin.client;

import dev.toastmc.client.event.KeyPressEvent;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.toastmc.client.ToastClient.EVENT_BUS;

@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Inject(at = @At("HEAD"), method = "onKey", cancellable = true)
    public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo ci) {
        KeyPressEvent event = new KeyPressEvent(window, key, scancode, i, j);
        EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }
}
