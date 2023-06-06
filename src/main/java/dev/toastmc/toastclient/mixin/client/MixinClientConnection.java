package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.ToastClient;
import dev.toastmc.toastclient.api.events.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Inject(
            at = {@At("HEAD")},
            method = {"handlePacket"},
            cancellable = true
    )
    private static <T extends PacketListener> void on(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        PacketEvent.Receive receive = new PacketEvent.Receive(packet);
        ToastClient.Companion.getEventBus().post(receive);
        if (receive.isCancelled())
            ci.cancel();
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"send(Lnet/minecraft/network/packet/Packet;)V"},
            cancellable = true
    )
    private void on(Packet<?> packet, CallbackInfo ci) {
        PacketEvent.Send send = new PacketEvent.Send(packet);
        ToastClient.Companion.getEventBus().post(send);
        if (send.isCancelled())
            ci.cancel();
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"sendImmediately"},
            cancellable = true
    )
    private void on1(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        PacketEvent.Send send = new PacketEvent.Send(packet);
        ToastClient.Companion.getEventBus().post(send);
        if (send.isCancelled())
            ci.cancel();
    }
}
