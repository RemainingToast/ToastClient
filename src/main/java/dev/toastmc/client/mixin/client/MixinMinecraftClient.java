package dev.toastmc.client.mixin.client;

import dev.toastmc.client.ToastClient;
import dev.toastmc.client.event.events.TickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow
    public ClientWorld world;

    @Inject(method = "tick", at = @At(value = "INVOKE"), cancellable = true)
    public void tick(CallbackInfo info) {
        TickEvent.Client event = ToastClient.Companion.getMINECRAFT().player == null ? new TickEvent.Client.OutOfGame() : new TickEvent.Client.InGame();
        ToastClient.EVENT_BUS.post(event);
        if (event.isCancelled()) info.cancel();
    }
}
