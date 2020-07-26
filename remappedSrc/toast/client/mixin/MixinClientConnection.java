package toast.client.mixin;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.ToastClient;
import toast.client.events.network.EventPacketReceived;
import toast.client.events.network.EventPacketSent;
import toast.client.modules.misc.Panic;

import java.util.Arrays;
import java.util.concurrent.Future;

import static toast.client.ToastClient.COMMAND_HANDLER;
import static toast.client.ToastClient.eventBus;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Shadow
    private Channel channel;

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"), cancellable = true)
    public void send(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> genericFutureListener_1, CallbackInfo ci) {
        EventPacketSent ep = new EventPacketSent(packet);
        eventBus.post(ep);
        if (ep.isCancelled()) ci.cancel();
        if (packet instanceof ChatMessageC2SPacket) {
            ChatMessageC2SPacket packet2 = (ChatMessageC2SPacket) packet;
            if (packet2.getChatMessage().startsWith(ToastClient.cmdPrefix)) {
                String cmd = packet2.getChatMessage().replaceFirst(ToastClient.cmdPrefix, "");
                if (packet2.getChatMessage().contains(" ")) {
                    cmd = cmd.split(" ")[0];
                }
                String[] args = packet2.getChatMessage().replaceFirst(ToastClient.cmdPrefix + cmd, "").split(" ");
                String[] betterArgs = Arrays.copyOfRange(args, 1, args.length);
                System.out.println("cmd: " + cmd + ", args: " + Arrays.toString(betterArgs));
                if (!Panic.IsPanicking()) {
                    COMMAND_HANDLER.executeCmd(cmd, betterArgs);
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (this.channel.isOpen()) {
            try {
                EventPacketReceived ep = new EventPacketReceived(packet);
                eventBus.post(ep);
                if (ep.isCancelled()) ci.cancel();
            } catch (Exception ignored) {
            }
        }

    }
}
