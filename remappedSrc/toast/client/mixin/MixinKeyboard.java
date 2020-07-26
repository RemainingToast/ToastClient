package toast.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static toast.client.ToastClient.CONFIG_MANAGER;
import static toast.client.ToastClient.MODULE_MANAGER;

@Environment(EnvType.CLIENT)
@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Inject(at = @At(value = "RETURN"), method = "onKey")
    public void onKey(long window, int key, int scancode, int action, int j, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen == null) {
            MODULE_MANAGER.onKey(key, action);
            CONFIG_MANAGER.checkForMacro(key, action);
        }
    }
}
