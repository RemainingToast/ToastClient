package dev.toastmc.toastclient.mixin.client;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Inject(at = @At("HEAD"), method = "onKey", cancellable = true)
    public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo ci) {
        if(i == 1){
//            ModuleManager.INSTANCE.toggleBind(key);
//            ToastClient.Companion.getCLICKGUI().handleKeyEvent(scancode);
        }
    }
}
