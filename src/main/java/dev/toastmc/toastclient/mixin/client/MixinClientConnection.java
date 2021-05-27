package dev.toastmc.toastclient.mixin.client;

import dev.toastmc.toastclient.ToastClient;
import dev.toastmc.toastclient.api.events.PacketEvent;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Future;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Inject(
            at = {@At("HEAD")},
            method = {"handlePacket"},
            cancellable = true
    )
    private static void on(Packet<?> packet, PacketListener listener, CallbackInfo info) {
        PacketEvent.Receive receive = new PacketEvent.Receive(packet);
        ToastClient.Companion.getEventBus().post(receive);
        if (receive.isCancelled())
            info.cancel();
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V"},
            cancellable = true
    )
    private void on(Packet<?> packet, GenericFutureListener<? extends java.util.concurrent.Future<? super Void>> genericFutureListener_1, CallbackInfo ci) {
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
    private void on1(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener, CallbackInfo info) {
        PacketEvent.Send send = new PacketEvent.Send(packet);
        ToastClient.Companion.getEventBus().post(send);
        if (send.isCancelled())
            info.cancel();
    }
}
