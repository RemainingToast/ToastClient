package dev.toastmc.client.mixin.client;

import dev.toastmc.client.ToastClient;
import dev.toastmc.client.event.events.PacketEvent;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.concurrent.Future;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static void handlePacket(Packet<?> packet, PacketListener listener, CallbackInfo info) {
        PacketEvent.Receive receive = new PacketEvent.Receive(packet);
        ToastClient.EVENT_BUS.post(receive);
        if (receive.isCancelled())
            info.cancel();
    }

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"), cancellable = true)
    public void send(Packet<?> packet, GenericFutureListener<? extends java.util.concurrent.Future<? super Void>> genericFutureListener_1, CallbackInfo ci) {
        PacketEvent.Send ep = new PacketEvent.Send(packet);
        ToastClient.EVENT_BUS.post(ep);
        if (ep.isCancelled()) ci.cancel();
        if (packet instanceof ChatMessageC2SPacket) {
            ChatMessageC2SPacket packet2 = (ChatMessageC2SPacket) packet;
            if (packet2.getChatMessage().startsWith(ToastClient.Companion.getCMD_PREFIX())) {
                String cmd = packet2.getChatMessage().replaceFirst(ToastClient.Companion.getCMD_PREFIX(), "").toLowerCase();
                if (packet2.getChatMessage().contains(" ")) {
                    cmd = cmd.split(" ")[0];
                }
                String[] args = packet2.getChatMessage().toLowerCase().replaceFirst(ToastClient.Companion.getCMD_PREFIX() + cmd, "").split(" ");
                String[] betterArgs = Arrays.copyOfRange(args, 1, args.length);
                System.out.println("cmd: " + cmd + ", args: " + Arrays.toString(betterArgs));
                ToastClient.Companion.getCOMMAND_MANAGER().executeCmd(cmd, betterArgs);
                ci.cancel();
            }
        }
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    private void sendImmediately(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener, CallbackInfo info) {
        PacketEvent.Send send = new PacketEvent.Send(packet);
        ToastClient.EVENT_BUS.post(send);
        if (send.isCancelled())
            info.cancel();
    }
}
