package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.ToastClient;
import dev.toastmc.toastclient.api.events.KeyEvent;
import dev.toastmc.toastclient.api.managers.command.CommandManager;
import dev.toastmc.toastclient.api.managers.module.ModuleManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
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
        KeyEvent event = new KeyEvent(window, keyInt, scancode);
        ToastClient.Companion.getEventBus().post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
