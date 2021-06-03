package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.ToastClient;
import dev.toastmc.toastclient.api.events.CloseScreenInPortalEvent;
import dev.toastmc.toastclient.api.events.InputUpdateEvent;
import dev.toastmc.toastclient.api.events.PlayerMoveEvent;
import dev.toastmc.toastclient.mixin.extend.ExtendedInput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @Redirect(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(Z)V"),
            method = {"tickMovement"}
    )
    private void on(Input input, boolean bl) {
        Input prev = ((ExtendedInput) input).copy();
        input.tick(bl);
        InputUpdateEvent ev = new InputUpdateEvent(prev, input);
        ToastClient.Companion.getEventBus().post(ev);
        if (ev.isCancelled()) ((ExtendedInput) input).update(prev);
    }

    @Redirect(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"),
            method = {"updateNausea"}
    )
    private void on(MinecraftClient client, Screen screen) {
        CloseScreenInPortalEvent event = new CloseScreenInPortalEvent(screen);
        ToastClient.Companion.getEventBus().post(event);
        if (!event.isCancelled()) {
            client.openScreen(screen);
        }
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"move"},
            cancellable = true
    )
    private void on(MovementType type, Vec3d vec, CallbackInfo info) {
        PlayerMoveEvent event = new PlayerMoveEvent(type, vec);
        ToastClient.Companion.getEventBus().post(event);
        if (event.isCancelled()) info.cancel();
    }

}
