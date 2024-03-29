package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.ToastClient;
import dev.toastmc.toastclient.api.events.ScreenEvent;
import dev.toastmc.toastclient.api.events.TickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    public ClientWorld world;

    @Shadow public ClientPlayerEntity player;

    @Inject(
            at = {@At(value = "HEAD")},
            method = {"tick"},
            cancellable = true
    )
    private void on(CallbackInfo info) {
        TickEvent.Client event;
        if (player != null && world != null) {
           event = new TickEvent.Client.InGame();
        } else {
            event = new TickEvent.Client.OutOfGame();
        }
        ToastClient.Companion.getEventBus().post(event);
        if (event.isCancelled()) info.cancel();
    }

    @ModifyVariable(
            at = @At("HEAD"),
            method = {"setScreen"},
            argsOnly = true
    )
    private Screen setScreen(Screen screen) {
        ScreenEvent.Closed closedEvent = new ScreenEvent.Closed(MinecraftClient.getInstance().currentScreen);
        ToastClient.Companion.getEventBus().post(closedEvent);
        if (closedEvent.isCancelled()) {
            return MinecraftClient.getInstance().currentScreen;
        }
        ScreenEvent.Displayed displayedEvent = new ScreenEvent.Displayed(screen);
        ToastClient.Companion.getEventBus().post(displayedEvent);
        if (displayedEvent.isCancelled()) {
            return MinecraftClient.getInstance().currentScreen;
        }
        return displayedEvent.getScreen();
    }
}
