package dev.toastmc.client.mixin.client;

import dev.toastmc.client.ToastClient;
import dev.toastmc.client.event.events.ScreenEvent;
import dev.toastmc.client.event.events.TickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/DisableableProfiler;push(Ljava/lang/String;)V", ordinal = 0), cancellable = true)
    public void tick(CallbackInfo info) {
        TickEvent.Client event;
        if (ToastClient.Companion.getMINECRAFT().player != null && ToastClient.Companion.getMINECRAFT().world != null) {
            event = new TickEvent.Client.InGame();
        } else {
            event = new TickEvent.Client.OutOfGame();
        }
        ToastClient.EVENT_BUS.post(event);
        if (event.isCancelled()) info.cancel();
    }

    @ModifyVariable(method = "openScreen", at = @At("HEAD"))
    private Screen openScreen(Screen screen) {
        ScreenEvent.Closed closedEvent = new ScreenEvent.Closed(ToastClient.Companion.getMINECRAFT().currentScreen);
        ToastClient.EVENT_BUS.post(closedEvent);
        if (closedEvent.isCancelled()) {
            return ToastClient.Companion.getMINECRAFT().currentScreen;
        }
        ScreenEvent.Displayed displayedEvent = new ScreenEvent.Displayed(screen);
        ToastClient.EVENT_BUS.post(displayedEvent);
        if (displayedEvent.isCancelled()) {
            return ToastClient.Companion.getMINECRAFT().currentScreen;
        }
        return displayedEvent.getScreen();
    }

}
