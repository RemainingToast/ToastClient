package toast.client.mixin;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static toast.client.ToastClient.MODULE_MANAGER;

@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Inject(at = @At(value = "RETURN"), method = "onKey")
    public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo ci) {
        MODULE_MANAGER.onKey(key, i);
    }
}
