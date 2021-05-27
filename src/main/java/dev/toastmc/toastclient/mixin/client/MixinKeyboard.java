package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.ToastClient;
import dev.toastmc.toastclient.api.command.CommandManager;
import dev.toastmc.toastclient.api.module.ModuleManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Inject(
            at = @At("HEAD"),
            method = "onKey",
            cancellable = true
    )
    private void on(long window, int keyInt, int scancode, int i, int j, CallbackInfo ci) {
        String key = GLFW.glfwGetKeyName(keyInt, scancode);
        if(key != null && key.equals(CommandManager.prefix)){
            if(ToastClient.Companion.getInstance().getMc().currentScreen == null) {
                ToastClient.Companion.getInstance().getMc().openScreen(new ChatScreen(CommandManager.prefix));
            }
        } else {
            ModuleManager.INSTANCE.onKeyRelease(window, keyInt, scancode);
        }
    }
}
