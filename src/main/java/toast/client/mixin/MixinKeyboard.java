package toast.client.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.gui.hud.clickgui.ClickGuiScreen;
import toast.client.modules.ModuleManager;
import toast.client.modules.render.ClickGui;

@Mixin(Keyboard.class)
public class MixinKeyboard {
    @Inject(at = @At(value = "RETURN"), method = "onKey")
    public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo ci) {
        ModuleManager.onKey(window, key, scancode, i, j);
        if (key == ModuleManager.getModule(ClickGui.class).getKey()) MinecraftClient.getInstance().openScreen(new ClickGuiScreen());
    }
}
